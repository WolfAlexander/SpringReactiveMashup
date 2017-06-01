package mashupservice.apiclient;

import mashupservice.MashupServiceApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Testing client to the Cover Art Archive API
 * Testing scenarios:
 * 1. Getting an existing cover images
 * 2. Getting and non-existing cover image
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MashupServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CoverArtArchiveClientTest {
    private CoverArtArchiveClient coverArtArchiveClient;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void initForEachTest(){
        coverArtArchiveClient = new CoverArtArchiveClient(cacheManager);
    }

    @Test
    public void gettingExistingCoverImages(){
        List<String> albumIds = new ArrayList<>();
        albumIds.add("01cf1391-141b-3c87-8650-45ade6e59070");
        albumIds.add("178b993e-fa9c-36d3-9d73-c5a8ba0c748d");
        albumIds.add("1a0edfef-ed8a-4664-8911-1ee69c39ae26");
        albumIds.add("1b022e01-4da6-387b-8658-8678046e4cef");


        StepVerifier
                .create(coverArtArchiveClient.getImages(Flux.fromIterable(albumIds)))
                .consumeNextWith(albumCover -> assertNotNull(albumCover.getCoverImageSrc()))
                .consumeNextWith(albumCover -> assertNotNull(albumCover.getCoverImageSrc()))
                .consumeNextWith(albumCover -> assertNotNull(albumCover.getCoverImageSrc()))
                .consumeNextWith(albumCover -> assertNotNull(albumCover.getCoverImageSrc()))
                .expectComplete()
                .verify();
    }

    @Test
    public void gettinNonExistingCoverImages(){
        List<String> albumIds = new ArrayList<>();
        albumIds.add("12345678-1234-1234-1234-123456789012");
        albumIds.add("12345678-1234-1234-1234-123456789013");
        albumIds.add("12345678-1234-1234-1234-123456789014");


        StepVerifier
                .create(coverArtArchiveClient.getImages(Flux.fromIterable(albumIds)))
                .consumeNextWith(albumCover -> assertNull(albumCover.getCoverImageSrc()))
                .consumeNextWith(albumCover -> assertNull(albumCover.getCoverImageSrc()))
                .consumeNextWith(albumCover -> assertNull(albumCover.getCoverImageSrc()))
                .expectComplete()
                .verify();
    }
}