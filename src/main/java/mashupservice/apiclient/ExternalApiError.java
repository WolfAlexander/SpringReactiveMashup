package mashupservice.apiclient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Represents an error received when calling to an external API
 */
@AllArgsConstructor
@Getter
public class ExternalApiError extends RuntimeException{
    private HttpStatus responseStatus;
    private String errorMessage;
}
