package mashupservice.controller;

import mashupservice.MashupServiceApplication;
import mashupservice.apiclient.CoverArtArchiveClient;
import mashupservice.apiclient.MusicBrainzClient;
import mashupservice.apiclient.WikipediaClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import static org.junit.Assert.*;

/**
 * TODO: fix this test, ignored since not all is done with a class that is used by the controller
 * Testing artist REST controller
 * Testing scenarios:
 * 1. Getting artist with proper mbid
 * 2. Getting artist with wrong mbid
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MashupServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArtistControllerTest {
    @Autowired
    private ArtistController artistController;

    @Ignore
    @Test
    public void getArtistWithProperMBID() throws Exception {
        StepVerifier.create(artistController.getArtist("5b11f4ce-a62d-471e-81fc-a69a8278c7da"))
                .consumeNextWith(artist -> {
                    assertNotNull(artist);
                    assertNotNull(artist.getMbid());
                    assertEquals("5b11f4ce-a62d-471e-81fc-a69a8278c7da", artist.getMbid());
                    assertNotNull(artist.getDescription());
                    assertNotNull(artist.getAlbums());
                })
                .expectComplete()
                .verify();
    }
}