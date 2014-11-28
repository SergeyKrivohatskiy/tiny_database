package queries;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author adkozlov
 */
public class InsertIntoQuery implements IQuery {

    @NotNull
    private final List<String> attributes;
    @NotNull
    private final List<Object> values;

    public InsertIntoQuery(@NotNull List<String> attributes, @NotNull List<Object> values) {
        this.attributes = attributes;
        this.values = values;
    }

    @Override
    public void execute() {

    }

    @Override
    public String toString() {
        return "InsertIntoQuery{" +
                "attributes=" + attributes +
                ", values=" + values +
                '}';
    }
}
