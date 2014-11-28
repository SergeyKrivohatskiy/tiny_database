package queries;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author adkozlov
 */
public class SelectFromQuery implements IQuery {

    @NotNull
    private final String tableName;
    @Nullable
    private final List<String> attributes;
    @Nullable
    private final WhereCondition filter;

    public SelectFromQuery(@NotNull String tableName, @Nullable List<String> attributes, @Nullable WhereCondition filter) {
        this.tableName = tableName;
        this.attributes = attributes;
        this.filter = filter;
    }

    @Override
    public void execute() {

    }

    @Override
    public String toString() {
        return "SelectFromQuery{" +
                "tableName='" + tableName + '\'' +
                ", attributes=" + attributes +
                ", filter=" + filter +
                '}';
    }
}
