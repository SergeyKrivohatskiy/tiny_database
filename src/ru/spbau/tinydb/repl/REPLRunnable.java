package ru.spbau.tinydb.repl;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.tinydb.common.DBANTLRErrorListener;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.grammar.SQLGrammarLexer;
import ru.spbau.tinydb.grammar.SQLGrammarParser;
import ru.spbau.tinydb.queries.IQuery;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author adkozlov
 */
public abstract class REPLRunnable<Q> implements Runnable, AutoCloseable {

    private static final String SUCCESS_MESSAGE_FORMAT = "OK\n%s\n";
    private static final String FAILURE_MESSAGE_FORMAT = "ERROR\n%s\n";

    @NotNull
    private final String dbFileName;
    @NotNull
    private final BufferedReader stdIn;
    @NotNull
    private final PrintWriter stdOut;
    @NotNull
    private final PrintWriter stdErr;

    @Nullable
    private final FileOutputStream fileOutputStream;
    @Nullable
    private final FileOutputStream fileErrorsStream;

    private REPLRunnable(@NotNull String dbFileName,
                         @NotNull InputStream standardInputStream,
                         @NotNull OutputStream standardOutputStream,
                         @NotNull OutputStream standardErrorsStream) {
        this.dbFileName = dbFileName;
        this.stdIn = new BufferedReader(new InputStreamReader(standardInputStream));
        this.stdOut = new PrintWriter(new OutputStreamWriter(standardOutputStream));
        this.stdErr = new PrintWriter(new OutputStreamWriter(standardErrorsStream));

        this.fileOutputStream = standardOutputStream instanceof FileOutputStream ?
                (FileOutputStream) standardOutputStream : null;
        this.fileErrorsStream = standardErrorsStream instanceof FileOutputStream ?
                (FileOutputStream) standardErrorsStream : null;
    }

    protected REPLRunnable(@NotNull String dbFileName,
                           @NotNull InputStream standardInputStream,
                           @Nullable String outputFileName,
                           @Nullable String errorsFileName) throws FileNotFoundException {
        this(dbFileName,
                standardInputStream,
                outputFileName != null ? new FileOutputStream(outputFileName) : System.out,
                errorsFileName != null ? new FileOutputStream(errorsFileName) : System.err
        );
    }

    @NotNull
    public String getDbFileName() {
        return dbFileName;
    }

    @NotNull
    public BufferedReader getStdIn() {
        return stdIn;
    }

    @NotNull
    public PrintWriter getStdOut() {
        return stdOut;
    }

    @NotNull
    public PrintWriter getStdErr() {
        return stdErr;
    }

    @Nullable
    protected final Object executeQuery(@NotNull IQuery query) {
        try {
            Future future = Executors.newSingleThreadExecutor().submit(query);

            return future.get();
        } catch (DBException | InterruptedException | ExecutionException e) {
            handleException(e);
        }

        return null;
    }

    protected void printSuccessMessage(@NotNull String message) {
        stdOut.printf(SUCCESS_MESSAGE_FORMAT, message);
        stdOut.flush();
    }

    protected void printFailureMessage(@NotNull String message) {
        stdErr.printf(FAILURE_MESSAGE_FORMAT, message);
        stdErr.flush();
    }

    protected void handleException(@NotNull Exception e) {
        printFailureMessage(e.getMessage());
    }

    @NotNull
    protected abstract ANTLRInputStream createANTLRInputStream(@NotNull Q query) throws IOException;

    @Nullable
    protected final SQLGrammarParser createParser(@NotNull Q query) {
        try {
            return createParser(createLexer(createANTLRInputStream(query)));
        } catch (IOException e) {
            handleException(e);
        }

        return null;
    }

    @NotNull
    protected static SQLGrammarLexer createLexer(@NotNull ANTLRInputStream inputStream) {
        SQLGrammarLexer result = new SQLGrammarLexer(inputStream);
        result.removeErrorListeners();
        result.addErrorListener(DBANTLRErrorListener.getInstance());

        return result;
    }

    @NotNull
    private static SQLGrammarParser createParser(@NotNull SQLGrammarLexer lexer) {
        SQLGrammarParser result = new SQLGrammarParser(new CommonTokenStream(lexer));
        result.removeErrorListeners();
        result.addErrorListener(DBANTLRErrorListener.getInstance());

        return result;
    }

    @Override
    public void close() throws Exception {
        closeNullableFileOutputStream(fileOutputStream);
        closeNullableFileOutputStream(fileErrorsStream);
    }

    private static void closeNullableFileOutputStream(@Nullable FileOutputStream fileOutputStream) throws IOException {
        if (fileOutputStream != null) {
            fileOutputStream.close();
        }
    }
}
