package mashupservice.apiclient;

import mashupservice.apiclient.entity.ErrorMessage;
import mashupservice.apiclient.entity.ExternalApiResponse;
import mashupservice.configuration.CacheConfiguration;
import mashupservice.apiclient.entity.MusicBrainzData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * MusicBrainz API client
 * Provides wikipedia id and list of albums for a specific MusicBrainsId
 */
@Service
public class MusicBrainzClient extends CachingRemoteClient {
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
    Mono<ExternalApiResponse> collectArtistDataByMbid(Mono<String> mbid) {
        log.debug("Collecting the artist data by mbid ");

        return mbid
                .flatMap(id -> {
                    try {
                        return Mono.just((MusicBrainzData) getObjectFromCache(CacheConfiguration.MUSIC_BRAINZ_CACHE, id));
                    } catch (NullPointerException ex) {
                        return getArtistDataFromRemote(id);
                    }
                });
    }

    private Mono<ExternalApiResponse> getArtistDataFromRemote(String mbid) {
        log.debug("Getting artist MusicBrainz data from remote with mbid " + mbid);

        return remote.get()
                .uri("/artist/" + mbid + "?&fmt=json&inc=url-rels+release-groups")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .timeout(Duration.ofSeconds(2))
                .flatMap(this::deserializeResponse)
                .doOnSuccess(musicBrainzData -> cacheObject(CacheConfiguration.MUSIC_BRAINZ_CACHE, mbid, musicBrainzData));
    }

    private Mono<ExternalApiResponse> deserializeResponse(ClientResponse clientResponse) {
        HttpStatus responseStatus = clientResponse.statusCode();

        if (responseStatus == HttpStatus.OK) {
            return clientResponse.bodyToMono(MusicBrainzData.class);
        } else
            return clientResponse.bodyToMono(ErrorMessage.class)
                    .flatMap(errorMessage -> Mono.error(new ExternalApiError(responseStatus, errorMessage.getMessage())));
    }
}
