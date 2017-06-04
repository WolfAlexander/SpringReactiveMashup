package mashupservice.configuration;

import mashupservice.MashupServiceApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Testing if cache is working
 * Test scenarios:
 * 1. Getting existing value
 * 2. Getting non-existing value
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MashupServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CacheConfigurationTest {
    private static final String TEST_CACHE = "test";

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void addValueToCache(){
        cacheManager.getCache(TEST_CACHE).put("key", "value");
    }

    @Test
    public void gettingExistingValue(){
        String cachedValue = (String) cacheManager.getCache(TEST_CACHE).get("key").get();

        assertEquals("value", cachedValue);
    }

    @Test
    public void gettingNonExistingValue(){
        try{
            cacheManager.getCache(TEST_CACHE).get("non-key").get();
            fail();
        }catch (NullPointerException ex){
            //successful test
        }
    }
}