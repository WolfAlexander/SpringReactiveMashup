package mashupservice.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents album created by the artist
 */
@Getter
public class Album {
    private String id;
    private String title;
    @Setter private String coverImage;

    /**
     * Creating the album with mandatory parameters
     * @param id - identification number used in Cover Art Archive API
     * @param title - title of the album
     */
    public Album(String id, String title) {
        this.id = id;
        this.title = title;
    }
}
