import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MainCandidate {

    public static void main(String[] args) throws IOException,
            CsvDataTypeMismatchException,
            CsvRequiredFieldEmptyException {

        List<Candidate> candidates = converteCsvParaCandidate();

        mostraPercentagemDosCandidatosAndroidIosQa(candidates);
        mostraIdadeMediaCandidatosQA(candidates);
        mostraNumeroDeEstadosDistintos(candidates);
        mostraNomeEstadoMenorOrcorrencia(candidates);
        ordenaPorOrdemAlfabetica(candidates);
        salvaComoSortedAppAcademyCandidates(candidates);

    }

    private static void mostraNomeEstadoMenorOrcorrencia(List<Candidate> candidates) {
        List<String> estados = getEstados(candidates);
        Map<String, Long> estadosGroupBy = getEstadosGroupBy(estados);
        Map<String, Long> estadosOrdenados = ordenaEstadosAgrupados(estadosGroupBy);

        int count = 1;

        System.out.println("\nRank dos 2 estados com menos ocorrências:");

        for (Map.Entry<String, Long> entry : estadosOrdenados.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();

            if (count <= 2){
                System.out.println(String.format("#" + count + " %s - %s candidatos", key, value));
            }

            count++;
        }
    }

    private static Map<String, Long> ordenaEstadosAgrupados(Map<String, Long> estadosGroupBy) {
        Map<String, Long> estadosOrdenados = new LinkedHashMap<>();
        estadosGroupBy.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue()
                ).forEachOrdered(e -> estadosOrdenados.put(e.getKey(), e.getValue()));
        return estadosOrdenados;
    }

    private static Map<String, Long> getEstadosGroupBy(List<String> estados) {
        Map<String, Long> estadosGroupBy =
                estados.stream()
                        .collect(
                                Collectors.groupingBy(
                                        Function.identity(), Collectors.counting()
                                )
                        );
        return estadosGroupBy;
    }

    private static List<String> getEstados(List<Candidate> candidates) {
        List<String> estados = candidates.stream()
                .map(Candidate::getEstado)
                .collect(Collectors.toList());
        return estados;
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
                .filter(distinctByKey(Candidate::getEstado))
                .count();

        System.out.println("\n\nNúmero de estados distintos presentes na lista: " + numeroDeEstadosDistintos);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private static List<Candidate> converteCsvParaCandidate() throws IOException {
        List<Candidate> candidates = Files.lines(Paths.get("AppAcademy_Candidates.csv"))
                .skip(1)
                .map(line -> line.split(";"))
                .map(col -> new Candidate(col[0], col[1], col[2], col[3])).collect(Collectors.toList());
        return candidates;
    }


    private static void ordenaPorOrdemAlfabetica(List<Candidate> candidates) {
        candidates.sort(Comparator.comparing(Candidate::getName));
        System.out.println("\nGerando lista ordenada...");
    }

    private static void salvaComoSortedAppAcademyCandidates(List<Candidate> candidates) throws
            CsvDataTypeMismatchException,
            CsvRequiredFieldEmptyException, IOException {
        Writer writer = null;

        try {
            writer = Files.newBufferedWriter(Paths.get("Sorted_AppAcademy_Candidates.csv"));
            writer.append("\"Nome\";\"Vaga\";\"Idade\";\"Estado\"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        StatefulBeanToCsv<Candidate> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                .withSeparator(';')
                .build();

        beanToCsv.write(candidates);

        writer.flush();
        writer.close();

        System.out.println("Lista ordenada salva como: Sorted_AppAcademy_Candidates.csv");
    }

    private static void mostraIdadeMediaCandidatosQA(List<Candidate> candidates) {
        int mediaIdades = (int) candidates.stream()
                .filter(candidate -> candidate.getVaga().startsWith("QA"))
                .map(c -> c.getIdade().substring(0, c.getIdade().indexOf(" ")))
                .mapToInt(n -> Integer.parseInt(n)).average().getAsDouble();

        System.out.print("\nIdade dos candidatos de QA é: " + mediaIdades);
    }
}
