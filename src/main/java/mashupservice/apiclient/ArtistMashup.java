package mashupservice.apiclient;

import com.sun.org.apache.regexp.internal.RE;
import mashupservice.apiclient.entity.AlbumCover;
import mashupservice.apiclient.entity.MusicBrainzData;
import mashupservice.apiclient.entity.WikipediaResponse;
import mashupservice.domain.*;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * TODO: FIRST TRY, change
 */
@Service
public class ArtistMashup {
    private final WikipediaClient wikipediaClient;
    private final MusicBrainzClient musicBrainzClient;
    private final CoverArtArchiveClient coverArtArchiveClient;

    private Mono<WikipediaResponse> wikipediaResponseMono;
    private Mono<AlbumCover> coverArtArchiveMono;

    public ArtistMashup(CacheManager cacheManager) {
        this.wikipediaClient = new WikipediaClient(cacheManager);
        this.musicBrainzClient = new MusicBrainzClient(cacheManager);
        this.coverArtArchiveClient = new CoverArtArchiveClient(cacheManager);
    }

    /**
     * Performs mashup of data from different external APIs
     * @param mbid - MusicBrainzId of a specific artist
     * @return  MBID, the Wikipedia description of the artist and list of all the albums created by the artist
     */
    public Mono<Artist> getArtistByMbid(String mbid){
        Mono<MusicBrainzData> musicBrainzDataMono = musicBrainzClient.collectArtistDataByMbid(Mono.just(mbid)).subscribe();

        return Mono.just(new Artist())
            .flatMap(artist -> {
                artist.setMbid(mbid);

                return musicBrainzDataMono
                        .flatMap(musicBrainzData -> {
                            initArtistWikiDescription(musicBrainzData, artist);
                            initAlbumCovers(musicBrainzData, artist);

                            return Mono.just(artist).delayUntilOther(wikipediaResponseMono).delayUntilOther(coverArtArchiveMono).doOnSuccess(Mono::just);
                        });
            });
    }

    private void initArtistWikiDescription(MusicBrainzData musicBrainzData, Artist artist){
        wikipediaResponseMono = getWikipediaData(musicBrainzData);

        wikipediaResponseMono.subscribe(wikiValue ->{
            artist.setDescription(wikiValue.getArtistDescription());
        });
    }

    private Mono<WikipediaResponse> getWikipediaData(MusicBrainzData musicBrainzData){
        return wikipediaClient.getArtistDescriptionById(Mono.just(musicBrainzData.getWikiArtistId()).subscribe());
    }

    private void initAlbumCovers(MusicBrainzData musicBrainzData, Artist artist){
            musicBrainzData.getAlbums().forEach(album -> {
                coverArtArchiveMono = coverArtArchiveClient.getImage(Mono.just(album.getId()));

                coverArtArchiveMono.subscribe(albumCover -> {
                    album.setCoverImage(albumCover.getCoverImageSrc());
                });
            });

        artist.setAlbums(musicBrainzData.getAlbums());
    }
}
