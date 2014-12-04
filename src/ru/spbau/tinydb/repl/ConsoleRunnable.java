package ru.spbau.tinydb.repl;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.grammar.SQLGrammarParser;
import ru.spbau.tinydb.queries.IQuery;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author adkozlov
 */
public class ConsoleRunnable extends REPLRunnable<String> {

    private static final String SHELL_PREFIX = "$ ";

    protected ConsoleRunnable(@NotNull String dbFileName,
                              @Nullable String outputFileName,
                              @Nullable String errorsFileName) throws FileNotFoundException {
        super(dbFileName, System.in, outputFileName, errorsFileName);
    }

    @Override
    public void run() {
        while (true) {
            printShell();

            String queryString = readQuery();
            if (queryString == null) {
                continue;
            }

            IQuery query = parseQuery(queryString);
            if (query != null) {
                executeAndPrintResult(query);
            }
        }
    }

    @Nullable
    private String readQuery() {
        try {
            StringBuilder result = new StringBuilder();

            String line;
            while (!(line = getStdIn().readLine()).equals("")) {
                result.append(line);
            }

            return result.toString();
        } catch (IOException e) {
            handleException(e);
        }

        return null;
    }

    @Nullable
    private IQuery parseQuery(@NotNull String query) {
        SQLGrammarParser parser = createParser(query);

        if (parser != null) {
            try {
                return parser.query().result;
            } catch (DBException e) {
                handleException(e);
            }
        }

        return null;
    }

    private void printShell() {
        getStdOut().print(SHELL_PREFIX);
        getStdOut().flush();
    }

    @NotNull
    @Override
    protected ANTLRInputStream createANTLRInputStream(@NotNull String query) {
        return new ANTLRInputStream(query);
    }
}
