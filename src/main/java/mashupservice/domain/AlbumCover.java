package mashupservice.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonDeserialize(using = AlbumCoverDeserializer.class)
public class AlbumCover {
    private String coverImageSrc;
}