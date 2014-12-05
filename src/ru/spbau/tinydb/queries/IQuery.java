package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.common.DBException;

/**
 * @author adkozlov
 */
public interface IQuery<R> {

    @NotNull
    R execute() throws DBException;
}
