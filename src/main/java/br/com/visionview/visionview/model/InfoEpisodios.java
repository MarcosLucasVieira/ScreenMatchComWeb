package br.com.visionview.visionview.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InfoEpisodios(@JsonAlias("Title") String titulo,
                            @JsonAlias("Episodes") Integer numeroEp,
                            @JsonAlias("imbdRating") String avaliacao,
                            @JsonAlias("Released") String dataLancamento) {
}