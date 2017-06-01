package mashupservice.domain;

import mashupservice.apiclient.entity.MusicBrainzDataDeserializer;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * TODO: create tests
 * Testing deserialization object for MusicBrainsData
 * Testing scenarios:
 * 1. Proper Json response deserialization
 * 2. Deserializing Json response with missing wikipedia relation
 * 3. Deserializing Json response with missing relation-group
 */
public class MusicBrainzDataDeserializerTest {
    private MusicBrainzDataDeserializer deserializer;

    @Before
    public void initForEachTest(){
        deserializer = new MusicBrainzDataDeserializer();
    }

    @Test
    public void deserializingProperJson() throws JSONException, IOException {
        File resourcesDirectory = new File("src/test/resources/MusicBrainsProperJson.txt");
    }
}