package mashupservice.apiclient;

import mashupservice.configuration.CacheConfiguration;
import mashupservice.apiclient.entity.MusicBrainzData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * MusicBrainz API client
 * Provides wikipedia id and list of albums for a specific MusicBrainsId
 */
@Service
public class MusicBrainzClient extends CachingRemoteClient{
    private static final Logger log = LoggerFactory.getLogger(MusicBrainzClient.class);

    /**
     * @param cacheManager - cache manager to to put and retrieve entities
     */
    public MusicBrainzClient(CacheManager cacheManager) {
        super(cacheManager, "http://musicbrainz.org/ws/2/");
    }

    /**
     * @param mbid - MusicBrainzId of a specific artist
     * @return wikipedia id and list of albums of a given artist
     */
    Mono<MusicBrainzData> collectArtistDataByMbid(Mono<String> mbid){
        log.info("Collecting the artist data by mbid ");

        return mbid
            .flatMap(id -> {
                try{
                    return Mono.just((MusicBrainzData) getObjectFromCache(CacheConfiguration.MUSIC_BRAINZ_CACHE, id));
                }catch (NullPointerException ex){
                    return getArtistDataFromRemote(id);
                }
            });
    }

    private Mono<MusicBrainzData> getArtistDataFromRemote(String mbid){
        log.info("Getting artist MusicBrainz data from remote with mbid " + mbid);

        return remote.get()
                .uri("/artist/" + mbid + "?&fmt=json&inc=url-rels+release-groups")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(MusicBrainzData.class)
                .doOnSuccess(musicBrainzData -> cacheObject(CacheConfiguration.MUSIC_BRAINZ_CACHE, mbid, musicBrainzData));
    }
}
