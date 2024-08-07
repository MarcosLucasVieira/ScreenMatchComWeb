package br.com.alura.screenmatch.service;


import br.com.alura.screenmatch.dto.SerieDto;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repository;

    public List<SerieDto> obterTodasAsSeries (){
        return converteDados(repository.findAll());
    }

    public List<SerieDto> obterTop5Series() {
        return converteDados(repository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDto> obterLancamentos() {
        return converteDados(repository.findTop5ByOrderByEpisodiosDataLancamentoDesc());
    }
    
    private List<SerieDto> converteDados(List<Serie> series){
        return series.stream()
                .map(s -> new SerieDto(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }


}
