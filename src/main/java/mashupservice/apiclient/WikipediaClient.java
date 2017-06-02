package mashupservice.apiclient;

import mashupservice.configuration.CacheConfiguration;
import mashupservice.apiclient.entity.WikipediaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Client to the Wikipedia API
 * Provides the artist description
 */
@Service
public class WikipediaClient extends CachingRemoteClient{
    private static final Logger log = LoggerFactory.getLogger(WikipediaClient.class);

    /**
     * @param cacheManager - cache manager to to put and retrieve entities
     */
    public WikipediaClient(CacheManager cacheManager) {
        super(cacheManager, "https://en.wikipedia.org/w/");
    }

    /**
     * Provides description of the artist from the wikipedia
     * @param wikiArtistId - wikipedia identifier to identify the artist entity
     * @return a description of the artist
     */
    Mono<WikipediaResponse> getArtistDescriptionById(Mono<String> wikiArtistId){
        log.info("Getting artist description with id: " + wikiArtistId);

        return wikiArtistId
            .flatMap(artistId -> {
                try{
                    return Mono.just((WikipediaResponse) super.getObjectFromCache(CacheConfiguration.WIKIPEDIA_CACHE, artistId));
                }catch (NullPointerException ex){
                    return getDescriptionFromRemote(artistId);
                }
            });
    }

    private Mono<WikipediaResponse> getDescriptionFromRemote(String wikiArtistId) {
        log.info("Getting artist description from remote with id: " + wikiArtistId);

         return remote
                .get()
                .uri("/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles="+wikiArtistId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(WikipediaResponse.class)
                .doOnSuccess(wikipediaResponse -> cacheObject(CacheConfiguration.WIKIPEDIA_CACHE, wikiArtistId, wikipediaResponse));
    }
}
