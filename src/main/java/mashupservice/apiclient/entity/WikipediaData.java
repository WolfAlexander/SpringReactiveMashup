package mashupservice.apiclient.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

/**
 * Representation of the Wikipedia API response
 */
@Getter
@Setter
@NoArgsConstructor
@JsonDeserialize(using = WikipediaResponseDeserializer.class)
public class WikipediaData extends ExternalApiResponse{
    private String artistDescription;

}

/**
 * WikiDescription object deserializer maps Json to the object
 */
class WikipediaResponseDeserializer extends JsonDeserializer<WikipediaData> {

    @Override
    public WikipediaData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);

        WikipediaData wiki = new WikipediaData();
        wiki.setArtistDescription(getArtistDescriptionFromJson(rootNode));

        return wiki;
    }

    private String getArtistDescriptionFromJson(JsonNode rootNode){
        String descriptionValue = rootNode.findPath("extract").textValue();

        if(descriptionValue == null)
            throw new ClassCastException("Wikipedia response has no 'extract' value");

        return descriptionValue;
    }
}
