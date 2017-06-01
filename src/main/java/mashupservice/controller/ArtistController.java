package mashupservice.controller;

import mashupservice.apiclient.ArtistMashup;
import mashupservice.domain.Artist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Artist rest controller
 */
@RestController
@RequestMapping("/v1")
public class ArtistController {
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
    public Mono<Artist> getArtist(@PathVariable String mbid){
        return artistMashup.getArtistByMbid(mbid);
    }
}
