package queries;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class SecondLevelId {

    @NotNull
    private final FirstLevelId tableName;
    @NotNull
    private final FirstLevelId attributeName;

    public SecondLevelId(@NotNull FirstLevelId tableName, @NotNull FirstLevelId attributeName) {
        this.tableName = tableName;
        this.attributeName = attributeName;
    }

    @NotNull
    public FirstLevelId getTableName() {
        return tableName;
    }

    @NotNull
    public FirstLevelId getAttributeName() {
        return attributeName;
    }

    @NotNull
    @Override
    public String toString() {
        return tableName + "." + attributeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecondLevelId)) return false;

        SecondLevelId that = (SecondLevelId) o;

        if (!attributeName.equals(that.attributeName)) return false;
        return tableName.equals(that.tableName);

    }

    @Override
    public int hashCode() {
        int result = tableName.hashCode();
        result = 31 * result + attributeName.hashCode();
        return result;
    }
}
