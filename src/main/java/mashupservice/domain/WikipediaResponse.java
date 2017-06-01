package mashupservice.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;

/**
 * Representation of the Wikipedia API response
 */
@Getter
@Setter
@NoArgsConstructor
@JsonDeserialize(using = WikipediaResponseDeserializer.class)
public class WikipediaResponse {
    private String artistDescription;

}
