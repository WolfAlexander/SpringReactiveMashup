package mashupservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@Setter
public class Artist {
    private String mbid;
    private String description;
    private List<Album> albums;
}
