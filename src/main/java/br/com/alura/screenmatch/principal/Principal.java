package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private  List<DadosSerie> dadosSeries = new ArrayList<>();

    @Autowired
    private SerieRepository repository;

    private List<Serie> series = new ArrayList<>();

    private Optional <Serie> serieBusca;

    public Principal(SerieRepository repository) {
        this.repository = repository;
    }


    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0){
            var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar Séries Buscadas
                4 - Buscar Séries por Titulo     
                5 - Buscar Séries por Ator   
                6 - Top 5 Séries
                7 - Buscar Séries por Categorias/Gênero         
                8 - Buscar Séries por Quantidade de Temporadas    
                9 - Buscar por nome de episódios        
                10 - Buscar top 5 episodio de séries   
                0 - Sair                                 
                """;

        System.out.println(menu);
        opcao = leitura.nextInt();
        leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3 :
                    listarSeriesBuscadas();
                    break;
                case 4 :
                    buscarSeriePorTitulo();
                    break;
                case 5 :
                    buscarSeriePorAtor();
                    break;
                case 6 :
                    buscarTop5Series();
                    break;
                case 7 :
                    buscarSeriesPorCatergoria();
                    break;
                case 8 :
                    buscarQntdTemporadas();
                    break;
                case 9 :
                    buscarEpisodioPortrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        // dadosSeries.add(dados);
        repository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma Série pelo nome:");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repository.findByTituloContainingIgnoreCase(nomeSerie);
        if (serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        }else{
            System.out.println("Série não encontrada");
        }
    }
    private void listarSeriesBuscadas() {

        series = repository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }
    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma Série pelo nome:");
        var nomeSerie = leitura.nextLine();
        serieBusca = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBusca.isPresent()){
            System.out.println("Dados da Serie: " + serieBusca.get());
        } else{
            System.out.println("Serie não encontrada");
        }
    }


    private void buscarSeriePorAtor() {
        System.out.println("Qual o nome para busca?");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries em que " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }
    private void buscarTop5Series() {
        List<Serie> serieTop = repository.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }
    private void buscarSeriesPorCatergoria() {
        System.out.println("Deseja buscar qual série de que categoria/genero: ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromStringPt(nomeGenero);
        List<Serie> seriesPorcategoria = repository.findByGenero(categoria);
        System.out.println("Séries da Categoria " + nomeGenero);
        seriesPorcategoria.forEach(System.out::println);
    }
    private void buscarQntdTemporadas() {
        System.out.println("Deseja assistir uma série com até quantas temporadas?");
        var qntdTemporada = leitura.nextInt();
        System.out.println("Avaliações a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriePorTemporada = repository.seriesPorTemporadaEAValiacao(qntdTemporada, avaliacao );
        System.out.println("Principais Séries com até : " + qntdTemporada);
        seriePorTemporada.forEach(System.out::println);
    }
    private void buscarEpisodioPortrecho() {
        System.out.println("Qual o nome  do episodio para busca?");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repository.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
    }
    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repository.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                    e.getSerie().getTitulo(), e.getTemporada(),
                    e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao())) ;
        }
    }

}

