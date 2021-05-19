import com.opencsv.CSVReader;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OpenCsv {
    private static final String SAMPLE_CSV_FILE_PATH = "book.csv";

    public static void main(String[] args) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        List<Book> books = converteCsvParaCandidate();
        OpenCsv.ordenaCsvPorNome(books);
        OpenCsv.gerarNovoCsvEmOrdemAlfabetica(books);
    }

    private static List<Book> converteCsvParaCandidate() throws IOException {
        List<Book> books = new ArrayList<>();
        try (
                Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
                CSVReader csvReader = new CSVReader(reader);
        ) {
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                Book book = new Book();
                book.setName(nextRecord[0]);
                book.setPrice(Integer.parseInt(nextRecord[1]));
                book.setAuthor(nextRecord[2]);

                books.add(book);
            }
        }
        return books;
    }


    private static List<Book> ordenaCsvPorNome(List<Book> books) {
        books.sort(Comparator.comparing(Book::getAuthor));
        return books;
    }

    private static void gerarNovoCsvEmOrdemAlfabetica(List<Book> books) throws CsvDataTypeMismatchException,
            CsvRequiredFieldEmptyException, IOException {
        Writer writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get("pessoas.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StatefulBeanToCsv<Book> beanToCsv = new StatefulBeanToCsvBuilder(writer).build();

        beanToCsv.write(books);

        writer.flush();
        writer.close();
    }
}
