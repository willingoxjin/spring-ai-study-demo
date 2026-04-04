package org.willingoxjin.springai.search;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
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
public class SearchResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String query;

    private List<SearchResult> results;

}
