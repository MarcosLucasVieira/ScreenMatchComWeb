package br.com.visionview.visionview.main;

import br.com.visionview.visionview.model.Episodio;
import br.com.visionview.visionview.model.InfoEpisodios;
import br.com.visionview.visionview.model.InfoSerie;
import br.com.visionview.visionview.model.InfoTemp;
import br.com.visionview.visionview.service.ConsumoApi;
import br.com.visionview.visionview.service.ConverteDados;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

        System.out.println("Digite um trecho do título do episódio");

        var trechoTitulo = leitor.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();
        if(episodioBuscado.isPresent()){
            System.out.println("Episódio encontrado!");
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
        } else {
            System.out.println("Episódio não encontrado!");
        }


//        System.out.println("A partir de que ano você deseja ver os episodios?");
//        var ano = leitor.nextInt();
//        leitor.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano,1,1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e ->e.getDataLancamento() !=null &&  e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " Episodio: " + e.getTitulo() +
//                                " Data de Lançamento: " + e.getDataLancamento()
//                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));


        DoubleSummaryStatistics est = episodios.stream()
                .filter(e-> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade de episódios avaliados: " + est.getCount() );
    }
}
