package queries;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class DeleteFromQuery implements IQuery {

    @NotNull
    private final WhereFilter filter;

    public DeleteFromQuery(@NotNull WhereFilter filter) {
        this.filter = filter;
    }

    @Override
    public void execute() {

    }

    @Override
    public String toString() {
        return "DeleteFromQuery{" +
                "filter=" + filter +
                '}';
    }
}