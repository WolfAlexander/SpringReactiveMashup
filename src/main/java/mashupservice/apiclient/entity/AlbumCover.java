package mashupservice.apiclient.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

/**
 * Representation of an album cover from cover art achive
 */
@Getter
@Setter
@NoArgsConstructor
@JsonDeserialize(using = AlbumCoverDeserializer.class)
public class AlbumCover {
    private String coverImageSrc;
}

/**
 * Deserializes Json response from Cover Art Archive API to an AlbumCover object
 */
class AlbumCoverDeserializer extends JsonDeserializer<AlbumCover> {
    @Override
    public AlbumCover deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);

        AlbumCover albumCover = new AlbumCover();
        albumCover.setCoverImageSrc(getImageSource(rootNode));

        return albumCover;
    }

    private String getImageSource(JsonNode rootNode){
        return rootNode.findPath("image").textValue();
    }
}
