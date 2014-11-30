package ru.spbau.tinydb.tinyDatabase;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.grammar.SQLGrammarLexer;
import ru.spbau.tinydb.grammar.SQLGrammarParser;
import ru.spbau.tinydb.queries.IQuery;

import java.io.*;

/**
 * @author adkozlov
 */
public class REPL implements Runnable {

    private static final String SUCCESS_MESSAGE_FORMAT = "OK\n%s";
    private static final String FAILURE_MESSAGE_FORMAT = "ERROR\n%s";

    @NotNull
    private final BufferedReader stdIn;
    @NotNull
    private final PrintWriter stdOut;
    @NotNull
    private final PrintWriter stdErr;

    public REPL(@NotNull InputStream standardInputStream, @NotNull OutputStream standardOutputStream,
                @NotNull OutputStream standardErrorStream) {
        this.stdIn = new BufferedReader(new InputStreamReader(standardInputStream));
        this.stdOut = new PrintWriter(new OutputStreamWriter(standardOutputStream));
        this.stdErr = new PrintWriter(new OutputStreamWriter(standardErrorStream));
    }

    @Override
    public void run() {
        while (true) {
            stdOut.print("$ ");
            stdOut.flush();

            String queryString = readQuery();
            if (queryString == null) {
                continue;
            }

            IQuery query = parseQuery(queryString);
            if (query == null) {
                continue;
            }


            // TODO: add result of query execution
            if (executeQuery(query)) {
                stdOut.printf(SUCCESS_MESSAGE_FORMAT, "");
            }
        }
    }

    @Nullable
    private String readQuery() {
        try {
            StringBuilder result = new StringBuilder();

            String line;
            while (!(line = stdIn.readLine()).equals("")) {
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
        try {
            SQLGrammarParser parser = new SQLGrammarParser(
                    new CommonTokenStream(new SQLGrammarLexer(new ANTLRInputStream(query))));

            return parser.query().result;
        } catch (RecognitionException e) {
            handleException(e);
        } catch (IllegalArgumentException e) {
            printFailureMessage("Illegal query");
        }

        return null;
    }

    private boolean executeQuery(@NotNull IQuery query) {
        try {
            query.execute();

            return true;
        } catch (DBException e) {
            handleException(e);
        }

        return false;
    }

    private void printFailureMessage(@NotNull String message) {
        stdErr.printf(FAILURE_MESSAGE_FORMAT, message);
        stdErr.flush();
    }

    private void handleException(@NotNull Exception e) {
        printFailureMessage(e.getMessage());
    }

    public static void main(String[] args) {
        new Thread(new REPL(System.in, System.out, System.err)).start();
    }
}
