import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MainCandidate {

    public static void main(String[] args) throws IOException,
            CsvDataTypeMismatchException,
            CsvRequiredFieldEmptyException {
        Scanner path = new Scanner(System.in);

        System.out.print("Informe o path o local do arquivo: ");
        String pathArquivoEntrada = path.nextLine();

        List<Candidate> candidates = converteCsvParaCandidate(pathArquivoEntrada);

        //Todo:vorganizar casas decimais
        mostraPercentagemDosCandidatosAndroidIosQa(candidates);

        //TODO:mostrar a idade média dos candidatos de QA

        //TODO:mostrar o nome do estado e a quantidade de candidatos dos 2 estados com menos ocorrências

        mostraNumeroDeEstadosDistintos(candidates);
        ordenaPorOrdemAlfabetica(candidates);

        System.out.print("Informe o path onde quer salvar o arquivo: ");
        String pathArquivoSaida = path.nextLine();

        salvaComoSortedAppAcademyCandidates(candidates,pathArquivoSaida);

    }

    private static void mostraPercentagemDosCandidatosAndroidIosQa(List<Candidate> candidates) {
        int totalRegistro = candidates.size();

        float qtdeAndroid = candidates.stream()
                .filter(c -> c.getVaga().startsWith("Android"))
                .count();
        float percentualAndroid = (qtdeAndroid * 100) / totalRegistro;

        float qtdeIos = candidates.stream()
                .filter(c -> c.getVaga().startsWith("iOS"))
                .count();
        float percentualIos = (qtdeIos * 100) / totalRegistro;

        float qtdeAQa = candidates.stream()
                .filter(c -> c.getVaga().startsWith("QA"))
                .count();
        float percentualQa = (qtdeAQa * 100) / totalRegistro;

        String msg = "Proporção de candidatos por vaga: \n" +
                "Android: " + percentualAndroid + "%\n" +
                "iOS    : " + percentualIos + "%\n" +
                "QA     : " + percentualQa + "%";

        System.out.println(msg);
    }

    private static void mostraNumeroDeEstadosDistintos(List<Candidate> candidates) {
        int numeroDeEstadosDistintos = (int) candidates.stream()
                .filter(distinctByKey(p -> p.getEstado()))
                .count();

        System.out.println("\nNúmero de estados distintos presentes na lista: " + numeroDeEstadosDistintos);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private static List<Candidate> converteCsvParaCandidate(String path) throws IOException {
        List<Candidate> candidates = Files.lines(Paths.get(path))
                .skip(1)
                .map(line -> line.split(";"))
                .map(col -> new Candidate(col[0], col[1], col[2], col[3])).collect(Collectors.toList());

        return candidates;
    }


    private static void ordenaPorOrdemAlfabetica(List<Candidate> candidates) {
        candidates.sort(Comparator.comparing(Candidate::getName));
        System.out.println("\nGerando lista ordenada...");
    }

    private static void salvaComoSortedAppAcademyCandidates(List<Candidate> candidates,String path) throws

            CsvDataTypeMismatchException,
            CsvRequiredFieldEmptyException, IOException {
        Writer writer = null;

        try {
            writer = Files.newBufferedWriter(Paths.get(path + "Sorted_AppAcademy_Candidates.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StatefulBeanToCsv<Candidate> beanToCsv = new StatefulBeanToCsvBuilder(writer).build();

        beanToCsv.write(candidates);

        writer.flush();
        writer.close();

        System.out.println("Lista ordenada salva como: Sorted_AppAcademy_Candidates.csv");
    }
}
