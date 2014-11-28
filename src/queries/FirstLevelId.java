package queries;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class FirstLevelId {

    @NotNull
    private final String id;

    public FirstLevelId(@NotNull String id) {
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FirstLevelId)) return false;

        FirstLevelId that = (FirstLevelId) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
