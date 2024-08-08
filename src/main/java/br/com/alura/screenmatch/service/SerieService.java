package br.com.alura.screenmatch.service;


import br.com.alura.screenmatch.dto.EpisodioDto;
import br.com.alura.screenmatch.dto.SerieDto;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
        return converteDados(repository.lancamentosMaisRecentes());
    }

    private List<SerieDto> converteDados(List<Serie> series){
        return series.stream()
                .map(s -> new SerieDto(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }


    public SerieDto obterPorId(Long id) {
        Optional<Serie> serie =  repository.findById(id);

        if (serie.isPresent()){
            Serie s = serie.get();
            return new SerieDto(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
        }
        return null;
    }


    public List<SerieDto> obterSeriesCategoria(String nomeGenero) {
        Categoria categoria = Categoria.fromStringPt(nomeGenero);
        return converteDados(repository.findByGenero(categoria));
    }

    public List<EpisodioDto> obterTodasTemporadas(Long id) {
        Optional<Serie> serie =  repository.findById(id);

        if (serie.isPresent()){
            Serie s = serie.get();
            return s.getEpisodios()
                    .stream()
                    .map(e -> new EpisodioDto(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDto> obterTemporadasPorNumero(Long id, long numero) {
        return repository.obterEpisodiosPorTemporada(id, numero)
                .stream()
                .map(e -> new EpisodioDto(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }
}
