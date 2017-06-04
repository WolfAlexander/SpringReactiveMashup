package mashupservice.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A representation of as error returned to the consumer
 */
@Getter
@AllArgsConstructor
class RestServiceError {
    private String message;
}
