package mashupservice.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A representation of as error returns to the consumer
 */
@Getter
@AllArgsConstructor
public class RestServiceError {
    private String message;
}
