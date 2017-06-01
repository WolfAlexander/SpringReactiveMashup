package mashupservice.apiclient;

import mashupservice.MashupServiceApplication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.Assert.*;


/**
 * Testing the WikipediaClient class
 * Testing scenarios:
 * 1. Getting an existing artist
 * 2. Getting an non-existing artist
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MashupServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WikipediaClientTest {
    private WikipediaClient wikipediaClient;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void initEachTest(){
        wikipediaClient = new WikipediaClient(cacheManager);
    }

    @Test
    public void gettingExistingArtist(){
            StepVerifier
                .create(wikipediaClient.getArtistDescriptionById(Mono.just("Nirvana_(band)")))
                .consumeNextWith(Assert::assertNotNull)
                .expectComplete()
                .verify();
    }

    @Test
    public void gettingNonExisting(){
        StepVerifier
            .create(wikipediaClient.getArtistDescriptionById(Mono.just("dijfisdf")))
            .expectError()
            .verify();
    }

}