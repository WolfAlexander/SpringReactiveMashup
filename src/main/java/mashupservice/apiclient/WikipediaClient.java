package mashupservice.apiclient;

import mashupservice.configuration.CacheConfiguration;
import mashupservice.domain.WikipediaResponse;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Client to the Wikipedia API
 * Provides the artist description
 */
@Service
class WikipediaClient {
    private static final Logger log = LoggerFactory.getLogger(WikipediaClient.class);
    private final CacheManager cacheManager;
    private final WebClient engWikipediaAPI;

    /**
     * @param cacheManager - cache manager to to put and retrieve entities
     */
    WikipediaClient(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.engWikipediaAPI = WebClient.create("https://en.wikipedia.org/w/");
    }

    /**
     * Provides description of the artist from the wikipedia
     * @param wikiArtistId - wikipedia identifier to identify the artist entity
     * @return a description of the artist
     */
    Mono<WikipediaResponse> getArtistDescriptionById(Mono<String> wikiArtistId){
        log.debug("Getting artist description with id: " + wikiArtistId);

        return wikiArtistId
                .flatMap(artistId -> getDescriptionFromCache(artistId).switchIfEmpty(getDescriptionFromRemote(artistId)))
                .onErrorResume(throwable -> Mono.error(throwable.getCause()));
    }

    private Mono<WikipediaResponse> getDescriptionFromCache(String wikiArtistId){
        log.debug("Getting artist description from cache with id: " + wikiArtistId);

        Cache.ValueWrapper valueWrapper = cacheManager.getCache(CacheConfiguration.WIKIPEDIA_CACHE).get(wikiArtistId);

        if(valueWrapper == null)
            return Mono.empty();
        else
            return Mono.just((WikipediaResponse)valueWrapper.get());
    }

    private Mono<WikipediaResponse> getDescriptionFromRemote(String wikiArtistId) {
        log.debug("Getting artist description from remote with id: " + wikiArtistId);

         return engWikipediaAPI
                .get()
                .uri("/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles="+wikiArtistId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(WikipediaResponse.class)
                .doOnSuccess(wikipediaResponse -> putNewDescriptionIntoCache(wikiArtistId, wikipediaResponse));
    }

    private void putNewDescriptionIntoCache(String wikiArtistId, WikipediaResponse artistDescription){
        log.debug("Saving the artist description to cache with id: " + wikiArtistId);

        this.cacheManager.getCache(CacheConfiguration.WIKIPEDIA_CACHE).put(wikiArtistId, artistDescription);
    }
}
