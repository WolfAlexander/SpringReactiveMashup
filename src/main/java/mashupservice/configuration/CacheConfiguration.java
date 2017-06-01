package mashupservice.configuration;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration for this application
 * Contains the constants to identify different caches, custom cache manager and key generator
 */
@Configuration
public class CacheConfiguration extends CachingConfigurerSupport{
    public static final String WIKIPEDIA_CACHE = "wiki";
    public static final String MUSIC_BRAINZ_CACHE = "music_brainz";
    public static final String COVER_ART_CACHE = "cover_art_archive";

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(){
            @Override
            protected Cache createConcurrentMapCache(String name) {
                return new ConcurrentMapCache(
                        name,
                        CacheBuilder.newBuilder()
                            .expireAfterWrite(1, TimeUnit.DAYS)
                            .build()
                            .asMap(),
                        false
                );
            }
        };
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }
}
