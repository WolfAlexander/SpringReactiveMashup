package mashupservice.apiclient;

import mashupservice.MashupServiceApplication;
import mashupservice.apiclient.entity.AlbumCover;
import mashupservice.apiclient.entity.MusicBrainzData;
import mashupservice.apiclient.entity.WikipediaResponse;
import mashupservice.domain.Album;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Testing mashup class
 * Testing scenarios:
 * 1. Getting artist with existing mbid
 * 2. Getting artist with unexacting mbid
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MashupServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArtistMashupTest {

    @Autowired
    private ArtistMashup mashup;

    @MockBean
    private CoverArtArchiveClient coverArtArchiveClient;

    @MockBean(name = "musicBrainzClient")
    private MusicBrainzClient musicBrainzClient;

    @MockBean
    private WikipediaClient wikipediaClient;

    @Test
    public void getArtistWithExistingMbid() throws Exception {
        mockProperRemoteClients();

        StepVerifier.create(
                mashup.getArtistByMbid("5dba01ed-b4e9-394e-a875-c7c2ff052133"))
                .consumeNextWith(artist -> {
                    assertNotNull(artist);
                    assertEquals("5dba01ed-b4e9-394e-a875-c7c2ff052133", artist.getMbid());
                    assertEquals("This is artist description", artist.getDescription());
                    assertEquals(1, artist.getAlbums().size());
                })
                .expectComplete()
                .verify();
    }

    private void mockProperRemoteClients(){
        mockProperMusicBrainzRespose();
        mockProperWikiResponse();
        mockProperCoverImageResponse();
    }

    private void mockProperMusicBrainzRespose(){
        MusicBrainzData data = new MusicBrainzData();
        data.setWikiArtistId("wikiArtistId");

        List<Album> albums = new ArrayList<>();
        Album album = new Album("albumId", "albumTitle");
        album.setCoverImage(createProperAlbumCover().getCoverImageSrc());
        albums.add(album);

        data.setAlbums(albums);

        Mockito.when(musicBrainzClient.collectArtistDataByMbid(any()))
                .thenReturn(Mono.just(data));
    }

    private AlbumCover createProperAlbumCover(){
        AlbumCover cover = new AlbumCover();
        cover.setCoverImageSrc("http://coverartarchive.org/release/4692f1c2-c328-48cc-953f-2fdc7606f067/1289830285.jpg");

        return cover;
    }

    private void mockProperWikiResponse(){
        WikipediaResponse wikipediaResponse = new WikipediaResponse();
        wikipediaResponse.setArtistDescription("This is artist description");

        Mockito.when(wikipediaClient.getArtistDescriptionById(any()))
                .thenReturn(Mono.just(wikipediaResponse));
    }

    private void mockProperCoverImageResponse(){
        Mockito.when(coverArtArchiveClient.getImage(any()))
                .thenReturn(Mono.just(createProperAlbumCover()));
    }


    @Test
    public void getArtistByMbidWithUnavailableService(){
        mockMusicBrainzClientWithError();

        StepVerifier.create(mashup.getArtistByMbid("test"))
                .expectError(WebClientException.class)
                .verify();
    }

    private void mockMusicBrainzClientWithError(){
        Mockito.when(musicBrainzClient.collectArtistDataByMbid(any()))
                .thenThrow(new WebClientException("503 Service Not Available"));
    }
}