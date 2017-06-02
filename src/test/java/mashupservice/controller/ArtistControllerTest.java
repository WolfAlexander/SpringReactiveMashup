package mashupservice.controller;

import mashupservice.MashupServiceApplication;
import mashupservice.apiclient.ArtistMashup;
import mashupservice.apiclient.CoverArtArchiveClient;
import mashupservice.apiclient.MusicBrainzClient;
import mashupservice.apiclient.WikipediaClient;
import mashupservice.domain.Album;
import mashupservice.domain.Artist;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Testing artist REST controller
 * Testing scenarios:
 * 1. Getting artist with proper mbid
 * 2. Getting artist with wrong mbid
 * 3. Error happen during mashup
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MashupServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArtistControllerTest {
    @Autowired
    private ArtistController artistController;

    @MockBean
    private ArtistMashup mockedArtistMashup;

    @Test
    public void getArtistWithProperMBID() throws Exception {
        Mockito.when(mockedArtistMashup.getArtistByMbid("5b11f4ce-a62d-471e-81fc-a69a8278c7da"))
                .thenReturn(createArtistMono());

        StepVerifier.create(artistController.getArtist("5b11f4ce-a62d-471e-81fc-a69a8278c7da"))
                .consumeNextWith(artist -> {
                    assertNotNull(artist);
                    assertEquals(HttpStatus.OK, artist.getStatusCode());
                    assertNotNull(artist.getBody().getMbid());
                    assertEquals("5b11f4ce-a62d-471e-81fc-a69a8278c7da", artist.getBody().getMbid());
                    assertNotNull(artist.getBody().getDescription());
                    assertNotNull(artist.getBody().getAlbums());
                })
                .expectComplete()
                .verify();
    }

    private Mono<Artist> createArtistMono(){
        Artist artist = new Artist();
        artist.setMbid("5b11f4ce-a62d-471e-81fc-a69a8278c7da");
        artist.setDescription("Description");

        List<Album> albums = new ArrayList<>();
        albums.add(new Album("albid1", "Album 1 title"));

        artist.setAlbums(albums);

        return Mono.just(artist);
    }

    @Test
    public void getArtistWithNonExistingMBID(){
        Mockito.when(mockedArtistMashup.getArtistByMbid("1b11f1ce-a12d-111e-11fc-a11a1111c1d1"))
                .thenReturn(Mono.error(new WebClientException("404 Not Found")));

        StepVerifier.create(artistController.getArtist("1b11f1ce-a12d-111e-11fc-a11a1111c1d1"))
                .expectError(WebClientException.class)
                .verify();
    }

    @Test
    public void testingErrorDuringTheMashup(){
        Mockito.when(mockedArtistMashup.getArtistByMbid("1b11f1ce-a12d-111e-11fc-a11a1111c1d1"))
                .thenReturn(Mono.error(new NullPointerException()));

        StepVerifier.create(artistController.getArtist("1b11f1ce-a12d-111e-11fc-a11a1111c1d1"))
                .expectError(Exception.class)
                .verify();
    }

}