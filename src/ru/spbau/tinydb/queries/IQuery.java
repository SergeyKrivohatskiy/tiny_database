package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.common.DBException;

import java.util.concurrent.Callable;

/**
 * @author adkozlov
 */
public interface IQuery<R> extends Callable<R> {

    @Override
    @NotNull
    R call() throws DBException;
}
