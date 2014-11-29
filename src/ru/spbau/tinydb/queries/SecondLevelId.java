package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class SecondLevelId {

    @NotNull
    private final String tableName;
    @NotNull
    private final String attributeName;

    public SecondLevelId(@NotNull String tableName, @NotNull String attributeName) {
        this.tableName = tableName;
        this.attributeName = attributeName;
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }

    @NotNull
    public String getAttributeName() {
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
