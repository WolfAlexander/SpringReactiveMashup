package mashupservice.apiclient;

import mashupservice.configuration.CacheConfiguration;
import mashupservice.apiclient.entity.AlbumCover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client for Cover Art Archive API
 * Provides album cover images for given album ids
 */
@Service
public class CoverArtArchiveClient extends CachingRemoteClient{
    private static final Logger log = LoggerFactory.getLogger(WikipediaClient.class);
    /**
     * @param cacheManager - cache manager to to put and retrieve entities
     */
    CoverArtArchiveClient(CacheManager cacheManager) {
        super(cacheManager, "");
    }

    /**
     * Provides a stream of album covers for the cover art archive
     * @param listOfAlbumIds - list of album identifiers
     * @return stream of album covers
     */
    Flux<AlbumCover> getImages(Flux<String> listOfAlbumIds){
         return listOfAlbumIds
                 .flatMap(albumId -> getImage(Mono.just(albumId)));
    }

    Mono<AlbumCover> getImage(Mono<String> albumId){

        return albumId
                .flatMap(artistId -> {
                    try{
                        return Mono.just((AlbumCover) super.getObjectFromCache(CacheConfiguration.COVER_ART_CACHE, artistId));
                    }catch (NullPointerException ex){
                        return getImageFromRemote(artistId);
                    }
                });
    }

    /**
     * TODO: Change implementation
     */
    private Mono<AlbumCover> getImageFromRemote(String albumId){
        log.info("Getting image from remote with album " + albumId);

        return remote
                .get()
                .uri("http://coverartarchive.org/release-group/"+albumId)
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .flatMap(clientResponse -> {
                    String redirectLocation0 = clientResponse.headers().asHttpHeaders().getFirst("Location");
                    if(redirectLocation0 != null)
                        return remote.get()
                                .uri(redirectLocation0)
                                .accept(MediaType.TEXT_PLAIN)
                                .exchange()
                                .flatMap(clientResponse1 -> {
                                    String redirectLocation1 = clientResponse1.headers().asHttpHeaders().getFirst("Location");

                                    return remote.get()
                                            .uri(redirectLocation1)
                                            .accept(MediaType.APPLICATION_JSON)
                                            .retrieve()
                                            .bodyToMono(AlbumCover.class)
                                            .doOnSuccess(albumCover -> cacheObject(CacheConfiguration.COVER_ART_CACHE, albumId, albumCover));
                            });
                    else
                        return Mono.just(new AlbumCover());
                });
    }
}