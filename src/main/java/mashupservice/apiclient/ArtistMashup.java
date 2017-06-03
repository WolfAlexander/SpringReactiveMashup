package mashupservice.apiclient;

import mashupservice.apiclient.entity.AlbumCover;
import mashupservice.apiclient.entity.MusicBrainzData;
import mashupservice.apiclient.entity.WikipediaData;
import mashupservice.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * This class perform the mashup using external services and providing artist information
 */
@Service
public class ArtistMashup {
    private static final Logger log = LoggerFactory.getLogger(ArtistMashup.class);

    private final WikipediaClient wikipediaClient;
    private final MusicBrainzClient musicBrainzClient;
    private final CoverArtArchiveClient coverArtArchiveClient;

    private Mono<WikipediaData> wikipediaResponseMono;
    private Mono<AlbumCover> coverArtArchiveMono;

    /**
     * @param wikipediaClient - client to wikipedia api
     * @param musicBrainzClient -  client to the musicbrains api
     * @param coverArtArchiveClient - client to the coverartarchive api
     */
    public ArtistMashup(WikipediaClient wikipediaClient, MusicBrainzClient musicBrainzClient, CoverArtArchiveClient coverArtArchiveClient) {
        this.wikipediaClient = wikipediaClient;
        this.musicBrainzClient = musicBrainzClient;
        this.coverArtArchiveClient = coverArtArchiveClient;
    }

    /**
     * Performs mashup of data from different external APIs
     * @param mbid - MusicBrainzId of a specific artist
     * @return  MBID, the Wikipedia description of the artist and list of all the albums created by the artist
     */
    public Mono<Artist> getArtistByMbid(String mbid){
        log.info("Performing mashup for artist with mbid '" + mbid + "'");

        return Mono.just(new Artist())
            .flatMap(artist -> {
                artist.setMbid(mbid);

                return musicBrainzClient.collectArtistDataByMbid(Mono.just(mbid)).subscribe()
                        .flatMap(musicBrainzData -> {

                            initArtistWikiDescription((MusicBrainzData)musicBrainzData, artist);
                            initAlbumCovers((MusicBrainzData)musicBrainzData, artist);

                            return Mono.just(artist).delayUntilOther(wikipediaResponseMono).delayUntilOther(coverArtArchiveMono).doOnSuccess(Mono::just);

                        });
            });
    }

    private void initArtistWikiDescription(MusicBrainzData musicBrainzData, Artist artist){
        log.info("Getting and setting wikipedia description");

        wikipediaResponseMono = getWikipediaData(musicBrainzData);

        wikipediaResponseMono.subscribe(wikiValue ->{
            artist.setDescription(wikiValue.getArtistDescription());
        });
    }

    private Mono<WikipediaData> getWikipediaData(MusicBrainzData musicBrainzData){
        log.debug("Requesting for wikipedia artist description");

        return wikipediaClient.getArtistDescriptionById(Mono.just(musicBrainzData.getWikiArtistId()))
                .flatMap(externalApiResponse -> Mono.just((WikipediaData)externalApiResponse))
                .subscribe();
    }

    private void initAlbumCovers(MusicBrainzData musicBrainzData, Artist artist){
            log.debug("Requesting album covers for artist '" + artist.getMbid() + "'");

            musicBrainzData.getAlbums().forEach(album -> {
                coverArtArchiveMono = coverArtArchiveClient.getImage(Mono.just(album.getId()));

                coverArtArchiveMono.subscribe(albumCover -> {
                    album.setCoverImage(albumCover.getCoverImageSrc());
                });
            });

        artist.setAlbums(musicBrainzData.getAlbums());
    }
}
