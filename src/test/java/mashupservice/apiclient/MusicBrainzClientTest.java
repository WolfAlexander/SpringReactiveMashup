package mashupservice.apiclient;

import mashupservice.MashupServiceApplication;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * TODO: Tests ignored until finding a way use mockito in this case
 * Testing the MusicBrainzCleint class
 * Testing scenarios:
 * 1. Getting data with existing mbid
 * 2. Getting data with non-existing mbid
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MashupServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MusicBrainzClientTest {
    private MusicBrainzClient musicBrainzClient;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void initEachTest(){
        musicBrainzClient = new MusicBrainzClient(cacheManager);
    }

    @Ignore
    @Test
    public void gettingDataWithExistingMbid() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        StepVerifier
            .create(musicBrainzClient.collectArtistDataByMbid(Mono.just("5b11f4ce-a62d-471e-81fc-a69a8278c7da")))
            .consumeNextWith(musicBrainzData -> {
                assertNotNull(musicBrainzData);
                assertEquals("Nirvana_(band)", musicBrainzData.getWikiArtistId());
                assertNotNull(musicBrainzData.getAlbums());
            })
            .expectComplete()
            .verify();
    }

    @Ignore
    @Test
    public void gettingDataWithNonExistingMbid(){
        StepVerifier
                .create(musicBrainzClient.collectArtistDataByMbid(Mono.just("fdsuhfdsif")))
                .expectError()
                .verify();
    }
}