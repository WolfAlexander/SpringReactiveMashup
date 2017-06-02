package mashupservice.apiclient.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import mashupservice.domain.Album;

import java.util.List;

/**
 * Representation of the information collected from MusicBrainz API
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = MusicBrainzDataDeserializer.class)
public class MusicBrainzData {
    private String wikiArtistId;
    private List<Album> albums;
}
