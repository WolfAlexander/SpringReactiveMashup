package mashupservice.configuration;

import mashupservice.apiclient.ArtistMashup;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Application configuration for running in the production
 */
@Configuration
@Profile("production")
public class ProductionConfiguration {
    @Bean
    public ArtistMashup createArtistMacshp(CacheManager cacheManager){
        return new ArtistMashup(cacheManager);
    }
}
