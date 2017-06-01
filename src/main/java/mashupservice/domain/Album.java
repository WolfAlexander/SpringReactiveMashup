package mashupservice.domain;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;

@Getter
public class Album {
    private String id;
    private String title;
    @Setter private String coverImage;

    public Album(String id, String title) {
        this.id = id;
        this.title = title;
    }
}
