package mashupservice.apiclient;

import mashupservice.configuration.CacheConfiguration;
import mashupservice.domain.AlbumCover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client for Cover Art Archive API
 * Provides album cover images for given album ids
 */
@Service
class CoverArtArchiveClient {
    private static final Logger log = LoggerFactory.getLogger(WikipediaClient.class);
    private final CacheManager cacheManager;
    private final WebClient coverArtArchiveClient;

    /**
     * @param cacheManager - cache manager to to put and retrieve entities
     */
    CoverArtArchiveClient(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.coverArtArchiveClient = WebClient.create();
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

    private Mono<AlbumCover> getImage(Mono<String> albumId){
        return albumId
                .flatMap(id -> getImageFromCache(id).switchIfEmpty(getImageFromRemote(id)));
    }

    private Mono<AlbumCover> getImageFromCache(String albumId){
        log.debug("Getting image from cache with album " + albumId);

        Cache.ValueWrapper valueWrapper = cacheManager.getCache(CacheConfiguration.COVER_ART_CACHE).get(albumId);

        if(valueWrapper == null)
            return Mono.empty();
        return Mono.just((AlbumCover)valueWrapper.get());
    }

    /**
     * TODO: Change implementation
     */
    private Mono<AlbumCover> getImageFromRemote(String albumId){
        log.debug("Getting image from remote with album " + albumId);

        return coverArtArchiveClient
                .get()
                .uri("http://coverartarchive.org/release-group/"+albumId)
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .flatMap(clientResponse -> {
                    String redirectLocation0 = clientResponse.headers().asHttpHeaders().getFirst("Location");
                    if(redirectLocation0 != null)
                        return coverArtArchiveClient.get()
                                .uri(redirectLocation0)
                                .accept(MediaType.TEXT_PLAIN)
                                .exchange()
                                .flatMap(clientResponse1 -> {
                                    String redirectLocation1 = clientResponse1.headers().asHttpHeaders().getFirst("Location");

                                    return coverArtArchiveClient.get()
                                            .uri(redirectLocation1)
                                            .accept(MediaType.APPLICATION_JSON)
                                            .retrieve()
                                            .bodyToMono(AlbumCover.class)
                                            .doOnSuccess(albumCover -> putImageCoverIntoCache(albumId, albumCover));
                            });
                    else
                        return Mono.just(new AlbumCover());
                });
    }

    private void putImageCoverIntoCache(String albumId, AlbumCover albumCover){
        cacheManager.getCache(CacheConfiguration.COVER_ART_CACHE).put(albumId, albumCover);
    }
}