package mashupservice.exception;

/**
 * This exception is thrown when an a value in cache is not found for a given key
 */
public class ValueInCacheNotFound extends RuntimeException {
    /**
     * @param message - messages regarding the exception
     */
    public ValueInCacheNotFound(String message) {
        super(message);
    }
}
