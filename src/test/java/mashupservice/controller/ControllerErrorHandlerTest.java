package mashupservice.controller;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import mashupservice.apiclient.ExternalApiError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.ipc.netty.http.client.HttpClientException;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.*;

/**
 * Testing controller error handler
 * Testing scenarios:
 * 1. Testing if exception when artist not found is handled properly
 * 2. Testing if exception when some of the external services is handler property
 * 3. Testing if other exceptions with unknown reason are properly handled
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ControllerErrorHandlerTest {
    @Autowired
    private ControllerErrorHandler errorHandler;

    @Test
    public void artistNotFoundHandling() throws Exception {
        ResponseEntity<RestServiceError> response = errorHandler.handleExceptions(new ExternalApiError(HttpStatus.NOT_FOUND, "404 Not Found"));
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void externalServiceUnavailableHandling(){
        ResponseEntity<RestServiceError> response = errorHandler.handleExceptions(new HttpClientException("http//test.com", new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SERVICE_UNAVAILABLE)));
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    public void unknownExceptionHandling(){
        ResponseEntity<RestServiceError> response = errorHandler.handleExceptions(new NullPointerException());
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}