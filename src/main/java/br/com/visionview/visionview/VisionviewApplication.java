package br.com.visionview.visionview;

import br.com.visionview.visionview.model.InfoEpisdios;
import br.com.visionview.visionview.model.InfoSerie;
import br.com.visionview.visionview.service.ConsumoApi;
import br.com.visionview.visionview.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VisionviewApplication implements CommandLineRunner {

	public static void main(String[] args) {

		SpringApplication.run(VisionviewApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumoApi = new ConsumoApi();
		var json = consumoApi.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=cec54ced");
		System.out.println(json);


		ConverteDados conversor = new ConverteDados();
		InfoSerie info = conversor.obterDados(json, InfoSerie.class);
		System.out.println(info);


		json = consumoApi.obterDados("https://omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=cec54ced");
		InfoEpisdios infoEpisdios = conversor.obterDados(json, InfoEpisdios.class);
		System.out.println(infoEpisdios);



	}
}
