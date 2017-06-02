package mashupservice.apiclient.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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