package br.com.joaodev.TabelaFipe.main;

import br.com.joaodev.TabelaFipe.model.Dados;
import br.com.joaodev.TabelaFipe.model.Modelos;
import br.com.joaodev.TabelaFipe.model.Veiculo;
import br.com.joaodev.TabelaFipe.service.ConsumindoApi;
import br.com.joaodev.TabelaFipe.service.ConsumindoDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private Scanner scanner = new Scanner(System.in);
    private String endereco;
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConsumindoApi consumo = new ConsumindoApi();
    private ConsumindoDados conversor = new ConsumindoDados();
    private String opcao;
    private String sair = "sair";

    public void exibirMenu (){
        while (true) {
            var menu = """
                    *** Opções: ***
                    
                    Carro
                    Moto
                    Caminhão
                    sair
                    
                    Digite uma das opções acima: """;
            System.out.println(menu);
            opcao = scanner.nextLine();

            if (opcao.equals("sair")){
                System.out.println("Saindo do sistema");
                break;
            }

            if (opcao.toLowerCase().contains("carr")) {
                endereco = URL_BASE + "carros/marcas";
            } else if (opcao.toLowerCase().contains("mot")) {
                endereco = URL_BASE + "motos/marcas";
            } else {
                endereco = URL_BASE + "caminhoes/marcas";
            }

            var json = consumo.obterDados(endereco);
            //System.out.println(json);

            var marcas = conversor.obterlista(json, Dados.class);
            marcas.stream()
                    .sorted(Comparator.comparing(Dados::codigo))
                    .forEach(System.out::println);

            System.out.println("Informe o codigo da marca para consulta: ");
            var codigo = scanner.nextLine();

            endereco = endereco + "/" + codigo + "/modelos";
            json = consumo.obterDados(endereco);
            var modeloLista = conversor.obterDados(json, Modelos.class);

            System.out.println("\nModelos desta marca: ");
            modeloLista.modelos().stream()
                    .sorted(Comparator.comparing(Dados::codigo))
                    .forEach(System.out::println);

            System.out.println("Digite um trecho do nome do carro que voce quer consultar: ");
            var nomeVeiculo = scanner.nextLine();

            List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                    .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                    .collect(Collectors.toList());

            System.out.println("\nModelos filtrados: ");
            System.out.println(modelosFiltrados);

            System.out.println("Digite o codigo do modelo: ");
            var codigoModelo = scanner.nextLine();

            endereco = endereco + "/" + codigoModelo + "/anos";
            json = consumo.obterDados(endereco);
            List<Dados> anos = conversor.obterlista(json, Dados.class);
            List<Veiculo> veiculos = new ArrayList<>();

            for (int i = 0; i < anos.size(); i++) {
                var enderecoAnos = endereco + "/" + anos.get(i).codigo();
                json = consumo.obterDados(enderecoAnos);
                Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
                veiculos.add(veiculo);
            }

            System.out.println("\nTodos os veiculos filtrados com avaliações por ano: ");
            veiculos.forEach(System.out::println);
        }
    }
}

