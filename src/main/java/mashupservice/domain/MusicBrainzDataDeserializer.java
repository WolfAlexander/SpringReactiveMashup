package mashupservice.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Deserialize the Json response from MusicBrains API to an MusicBrainzData object
 */
public class MusicBrainzDataDeserializer extends JsonDeserializer<MusicBrainzData> {
    @Override
    public MusicBrainzData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);

        MusicBrainzData musicBrainzData = new MusicBrainzData();
        JsonNode wikipediaNode = getWikipediaNode(rootNode);

        String wikiArtistId =  getWikiArtistId(wikipediaNode);
        musicBrainzData.setWikiArtistId(wikiArtistId);

        List<Album> albums = getAlbumList(rootNode);
        musicBrainzData.setAlbums(albums);

        return musicBrainzData;
    }

    private JsonNode getWikipediaNode(JsonNode rootNode){
        ArrayNode relations =  (ArrayNode)rootNode.findPath("relations");

        Stream<JsonNode> relationNodes = StreamSupport.stream(Spliterators.spliteratorUnknownSize(relations.elements(), Spliterator.ORDERED), false);

        return relationNodes
                .filter(node -> node.path("type").textValue().equals("wikipedia"))
                .findAny()
                .orElseThrow(() -> new RuntimeException("No relation of type 'wikipedia' found in relations"));
    }

    private String getWikiArtistId(JsonNode wikipediaNode){
        return wikipediaNode.path("url").path("resource").textValue().replace("https://en.wikipedia.org/wiki/", "");
    }

    private List<Album> getAlbumList(JsonNode rootNode){
        JsonNode releaseGroups = rootNode.findPath("release-groups");

        return Stream.of(releaseGroups)
            .map(jsonNode -> {
                String id = jsonNode.path("id").textValue();
                String title = jsonNode.path("title").textValue();

                return new Album(id, title);
            })
            .collect(Collectors.toList());
    }
}
