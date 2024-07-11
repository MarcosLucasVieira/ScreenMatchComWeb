package br.com.visionview.visionview.model;

import com.fasterxml.jackson.annotation.JsonAlias;


public record InfoSerie(@JsonAlias("Title") String titulo,
                        @JsonAlias("totalSeasons") Integer totalTemporadas,
                        @JsonAlias ("imdbRating") String avaliacao )              {
}
