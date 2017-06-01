package mashupservice.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = MusicBrainzDataDeserializer.class)
@ToString
public class MusicBrainzData {
    private String wikiArtistId;
    private List<Album> albums;
}
