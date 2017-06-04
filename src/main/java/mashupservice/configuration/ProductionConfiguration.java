package mashupservice.configuration;

import mashupservice.apiclient.ArtistMashup;
import mashupservice.apiclient.CoverArtArchiveClient;
import mashupservice.apiclient.MusicBrainzClient;
import mashupservice.apiclient.WikipediaClient;
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
    public WikipediaClient wikipediaClient(CacheManager cacheManager){
        return new WikipediaClient(cacheManager);
    }

    @Bean
    public MusicBrainzClient musicBrainzClient(CacheManager cacheManager){
        return new MusicBrainzClient(cacheManager);
    }

    @Bean
    public CoverArtArchiveClient coverArtArchiveClient(CacheManager cacheManager){
        return new CoverArtArchiveClient(cacheManager);
    }

    @Bean
    public ArtistMashup artistMashup(WikipediaClient wikipediaClient, CoverArtArchiveClient coverArtArchiveClient, MusicBrainzClient musicBrainzClient){
        return new ArtistMashup(wikipediaClient, musicBrainzClient, coverArtArchiveClient);
    }
}
