package mashupservice.apiclient.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * WikiDescription object deserializer maps Json to the object
 */
class WikipediaResponseDeserializer extends JsonDeserializer<WikipediaResponse> {

    @Override
    public WikipediaResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);

        WikipediaResponse wiki = new WikipediaResponse();
        wiki.setArtistDescription(getArtistDescriptionFromJson(rootNode));

        return wiki;
    }

    private String getArtistDescriptionFromJson(JsonNode rootNode){
        String descriptionValue = rootNode.findPath("extract").textValue();

        if(descriptionValue == null)
            throw new RuntimeException("Wikipedia response has no 'extract' value");

        return descriptionValue;
    }
}