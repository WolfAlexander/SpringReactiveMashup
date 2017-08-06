package mashupservice.apiclient;

import mashupservice.exception.ValueInCacheNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * A caching client to a remote services
 */
abstract class CachingRemoteClient {
    private static final Logger log = LoggerFactory.getLogger(CachingRemoteClient.class);
    private final CacheManager cacheManager;
    final WebClient remote;

    /**
     * @param cacheManager - cache manager to to put and retrieve entities
     * @param remoteBaseURL - base url of the remote service
     */
    CachingRemoteClient(CacheManager cacheManager, String remoteBaseURL) {
        this.cacheManager = cacheManager;
        this.remote = WebClient.create(remoteBaseURL);
    }

    /**
     * Putting a value into a specific cache
     * @param key - key to the value to later get the value
     * @param value - the value to be cached
     */
    void cacheObject(Object key, Object value){
        log.debug("Caching " + value + " with key '" + key + "' in cache with name '" + getCacheIdentifier() + "'");

        this.cacheManager.getCache(getCacheIdentifier()).put(key, value);
    }

    /**
     * Method for getting a cached value
     * @param key - key to find the value
     * @return the cached value for a given key
     * @throws ValueInCacheNotFound if value for a give key does not exist
     */
    Object getObjectFromCache(Object key){
        log.debug("Getting value with key '" + key + "' from cache '" + getCacheIdentifier() + "'");

        Cache.ValueWrapper wrappedValue = this.cacheManager.getCache(getCacheIdentifier()).get(key);

        if(wrappedValue != null)
            return wrappedValue.get();
        else
            throw new ValueInCacheNotFound("Value for a key '" + key + "' not found");
    }

    /**
     * @return the identification to identify the cache for the remote client
     */
    abstract String getCacheIdentifier();
}