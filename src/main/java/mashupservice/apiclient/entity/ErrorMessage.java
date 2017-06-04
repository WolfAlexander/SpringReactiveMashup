package mashupservice.apiclient.entity;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = ErrorMessageDeserializer.class)
public class ErrorMessage {
    private String message;
}

class ErrorMessageDeserializer extends JsonDeserializer<ErrorMessage>{
    @Override
    public ErrorMessage deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);

        String errorMessage = rootNode.findPath("error").textValue();

        return new ErrorMessage(errorMessage);
    }
}
