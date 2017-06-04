package mashupservice.controller;

import mashupservice.apiclient.ArtistMashup;
import mashupservice.domain.Artist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Artist rest controller
 * Provides endpoint to request information about an artist
 */
@RestController
@RequestMapping("/v1")
public class ArtistController {
    private static Logger log = LoggerFactory.getLogger(ArtistController.class);
    private final ArtistMashup artistMashup;

    @Autowired
    public ArtistController(ArtistMashup artistMashup) {
        this.artistMashup = artistMashup;
    }

    /**
     * REST endpoint provides artist information
     * @param mbid - MusicBrainzId (MBID) to identifier the artist
     * @return  MBID, the Wikipedia description of the artist and list of all the albums created by the artist
     */
    @GetMapping("/{mbid}")
    public Mono<ResponseEntity<Artist>> getArtist(@PathVariable String mbid){
        log.debug("Request to /{mbid} received with '"+ mbid +"'");

        return artistMashup.getArtistByMbid(mbid)
            .flatMap(artist -> Mono.just(new ResponseEntity<Artist>(artist, HttpStatus.OK)))
                .doOnSuccess(artistResponseEntity -> {
                    log.debug("Request succeed with '" + mbid + "'");
                });
    }
}
