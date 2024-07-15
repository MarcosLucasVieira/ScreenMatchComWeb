package br.com.visionview.visionview.main;

import br.com.visionview.visionview.model.Episodio;
import br.com.visionview.visionview.model.InfoEpisodios;
import br.com.visionview.visionview.model.InfoSerie;
import br.com.visionview.visionview.model.InfoTemp;
import br.com.visionview.visionview.service.ConsumoApi;
import br.com.visionview.visionview.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private final String ENDERECO = "https://omdbapi.com/?t=";
    private final String API_KEY = "&apikey=cec54ced";
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    public void exibeMenu(){
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o nome da série para busca: ");
        var nomeSerie = leitor.nextLine();
        var json = consumo.obterDados(ENDERECO+ nomeSerie.replace(" ", "+") +API_KEY
        );
        InfoSerie infoSerie = conversor.obterDados(json, InfoSerie.class);
        System.out.println(infoSerie);

        List<InfoTemp> temporadas = new ArrayList<>();

        for (int i = 1; i<= infoSerie.totalTemporadas(); i++){
            json = consumo.obterDados(ENDERECO+ nomeSerie.replace(" ", "+") + "&season="+ i + API_KEY);
            InfoTemp infoTemp = conversor.obterDados(json, InfoTemp.class);
            temporadas.add(infoTemp);
        }
        temporadas.forEach(System.out::println);

//        for (int i = 1; i < infoSerie.totalTemporadas(); i++){
//         List<InfoEpisodios> episodiosTemp = temporadas.get(i).episodios();
//            for(int j  = 1; j < episodiosTemp.size(); j++){
//                System.out.println(episodiosTemp.get(j).titulo());
//            }
//        }

        temporadas.forEach(t -> t .episodios().forEach(e -> System.out.println(e.titulo())));


        List<InfoEpisodios> infoEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());
        System.out.println(infoEpisodios);

        System.out.println("\n Top 5 episódios");
        infoEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(InfoEpisodios::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);


        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);
    }
}
