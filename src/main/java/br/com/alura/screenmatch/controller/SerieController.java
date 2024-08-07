package br.com.alura.screenmatch.controller;


import br.com.alura.screenmatch.dto.SerieDto;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService service;

    @GetMapping
    public List <SerieDto> obterSeries(){
        return service.obterTodasAsSeries();
    }
    @GetMapping("/top5")
    public List<SerieDto> obterTop5Series(){
        return service.obterTop5Series();
    }
    @GetMapping("/lancamentos")
    public List<SerieDto> obterLancamentos(){
        return service.obterLancamentos();
    }
}
