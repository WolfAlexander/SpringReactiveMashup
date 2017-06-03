package mashupservice.controller;

import mashupservice.apiclient.ExternalApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.ipc.netty.http.client.HttpClientException;

/**
 * Handles error that are thrown in controllers
 */
@ControllerAdvice
public class ControllerErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ArtistController.class);

    /**
     * Handles error of the application
     * @param ex - exception during execution of a request
     * @return a response to the consumer of the service with error information
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<RestServiceError> handleExceptions(Exception ex){
        log.error("Exception caught: " + ex);

       if(ex instanceof HttpClientException && ((HttpClientException) ex).status().code() == HttpStatus.SERVICE_UNAVAILABLE.value())
            return new ResponseEntity<RestServiceError>(
                    new RestServiceError("One of external services is unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
        else if(ex instanceof ExternalApiError)
            return new ResponseEntity<RestServiceError>(
                    new RestServiceError(((ExternalApiError) ex).getErrorMessage()), ((ExternalApiError) ex).getResponseStatus());
        else
            return new ResponseEntity<RestServiceError>(
                    new RestServiceError("An unknown error happen during the request"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
