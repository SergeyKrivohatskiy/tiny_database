package queries;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author adkozlov
 */
public class CreateTableQuery implements IQuery {

    @NotNull
    private final String id;
    @NotNull
    private final List<Attribute> attributes;

    public CreateTableQuery(@NotNull String id, @NotNull List<Attribute> attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public @NotNull List<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "CreateTableQuery{" +
                "id='" + id + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    @Override
    public void execute() {

    }
}
