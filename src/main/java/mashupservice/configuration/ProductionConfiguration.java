package mashupservice.configuration;

import org.springframework.beans.factory.annotation.Autowired;
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

}
