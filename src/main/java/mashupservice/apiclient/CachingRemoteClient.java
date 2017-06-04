package mashupservice.apiclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * A caching client to a remote services
 */
class CachingRemoteClient {
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
     * @param cacheName - name to identify the cache in which value should be put
     * @param key - key to the value to later get the value
     * @param value - the value to be cached
     */
    void cacheObject(String cacheName, Object key, Object value){
        log.debug("Caching " + value + " with key '" + key + "' in cache with name '" + cacheName + "'");

        this.cacheManager.getCache(cacheName).put(key, value);
    }

    /**
     * Method for getting a cached value
     * @param cacheName - name of the cache where value is cached
     * @param key - key to find the value
     * @return the cached value for a given key
     * @throws NullPointerException if value for a give key does not exist
     */
    Object getObjectFromCache(String cacheName, Object key){
        log.debug("Getting value with key '" + key + "' from cache '" + cacheName + "'");

        return this.cacheManager.getCache(cacheName).get(key).get();
    }
}