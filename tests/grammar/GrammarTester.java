package grammar;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import ru.spbau.tinydb.grammar.SQLGrammarLexer;
import ru.spbau.tinydb.grammar.SQLGrammarParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author adkozlov
 */
public class GrammarTester {

    public static final String TESTS_PATH = "testData/grammar";

    private static List<String> testsList(String directory) {
        List<String> result = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                result.add(path.toString());
            }
        } catch (IOException e) {
            handleException(e);
        }

        return result;
    }

    private static void handleException(Exception e) {
        System.err.println(e);
    }

    public static void main(String[] args) {
        for (String testName : testsList(TESTS_PATH)) {
            try {
                SQLGrammarParser parser = new SQLGrammarParser(new CommonTokenStream(
                        new SQLGrammarLexer(new ANTLRInputStream(new FileInputStream(testName)))));

                System.out.println(parser.script().result);
            } catch (IOException e) {
                handleException(e);
            }
        }
    }
}
