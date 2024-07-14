package br.com.visionview.visionview.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InfoTemp(@JsonAlias ("Seasons") Integer temporada,
                       @JsonAlias ("Episodes") List<InfoEpisodios> episodios) {
}
