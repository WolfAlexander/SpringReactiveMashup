package mashupservice.apiclient;

import mashupservice.configuration.CacheConfiguration;
import mashupservice.domain.MusicBrainzData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * MusicBrainz API client
 * Provides wikipedia id and list of albums for a specific MusicBrainsId
 */
@Service
class MusicBrainzClient {
    private static final Logger log = LoggerFactory.getLogger(MusicBrainzClient.class);
    private final CacheManager cacheManager;
    private final WebClient musicBrainzClient;

    /**
     * @param cacheManager - cache manager to to put and retrieve entities
     */
    MusicBrainzClient(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.musicBrainzClient = WebClient.create("http://musicbrainz.org/ws/2/");
    }

    /**
     * @param mbid - MusicBrainzId of a specific artist
     * @return wikipedia id and list of albums of a given artist
     */
    Mono<MusicBrainzData> collectArtistDataByMbid(Mono<String> mbid){
        log.debug("Collecting the artist data by mbid " + mbid);


        return mbid
                .flatMap(id -> getArtistDataFromCache(id).switchIfEmpty(getArtistDataFromRemote(id)))
                .onErrorResume(throwable -> Mono.error(throwable.getCause()));
    }

    private Mono<MusicBrainzData> getArtistDataFromCache(String mbid){
        log.debug("Getting artist MusicBrainz data from cache with mbid " + mbid);

        Cache.ValueWrapper valueWrapper = cacheManager.getCache(CacheConfiguration.MUSIC_BRAINZ_CACHE).get(mbid);

        if(valueWrapper == null)
            return Mono.empty();
        else
            return Mono.just((MusicBrainzData)valueWrapper.get());
    }

    private Mono<MusicBrainzData> getArtistDataFromRemote(String mbid){
        log.debug("Getting artist MusicBrainz data from remote with mbid " + mbid);

        return musicBrainzClient.get()
                .uri("/artist/" + mbid + "?&fmt=json&inc=url-rels+release-groups")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(MusicBrainzData.class)
                .doOnSuccess(musicBrainzData -> putNewDataIntoCache(mbid, musicBrainzData));
    }

    private void putNewDataIntoCache(String mbid, MusicBrainzData musicBrainzData){
        log.debug("Saving the music brainz data to cache with mbid " + mbid);

        this.cacheManager.getCache(CacheConfiguration.MUSIC_BRAINZ_CACHE).put(mbid, musicBrainzData);
    }
}
