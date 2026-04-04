package org.willingoxjin.springai.search;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Jin.Nie
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SearchResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String title;
    private String url;
    private String content;
    private String engine;
    private Double score;
    private String category;

}
