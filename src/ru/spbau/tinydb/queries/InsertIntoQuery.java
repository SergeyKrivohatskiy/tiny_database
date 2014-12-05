package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.tinyDatabase.TinyDatabase;

import java.util.Collections;
import java.util.List;

/**
 * @author adkozlov
 */
public class InsertIntoQuery extends TableNameContainer implements IQuery<Integer> {

    @NotNull
    private final List<String> attributes;
    @NotNull
    private final List<Object> values;

    public InsertIntoQuery(@NotNull String tableName, @NotNull List<String> attributes, @NotNull List<Object> values) {
        super(tableName);
        this.attributes = Collections.unmodifiableList(attributes);
        this.values = Collections.unmodifiableList(values);
    }

    @NotNull
    public List<String> getAttributes() {
        return attributes;
    }

    @NotNull
    public List<Object> getValues() {
        return values;
    }

    @NotNull
    @Override
    public Integer execute() throws DBException {
        TinyDatabase.getInstance().insertRecord(getTableName(), getAttributes(), getValues());

        // TODO return correct value of affected rows
        return 1;
    }

    @NotNull
    @Override
    public String toString() {
        return "InsertIntoQuery{" +
                "attributes=" + attributes +
                ", values=" + values +
                "} " + super.toString();
    }
}
