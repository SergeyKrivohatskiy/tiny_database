package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author adkozlov
 */
public class CreateIndexQuery implements IQuery {

    @NotNull
    private final String indexName;
    @NotNull
    private final String tableName;
    @NotNull
    private final List<String> attributeNames;
    private final boolean isUnique;
    private final boolean isAscending;
    private final boolean isUsingHash;

    public CreateIndexQuery(@NotNull String indexName, @NotNull String tableName, @NotNull List<String> attributeNames,
                            boolean isUnique, boolean isAscending, boolean isUsingHash) {
        this.indexName = indexName;
        this.tableName = tableName;
        this.attributeNames = Collections.unmodifiableList(attributeNames);
        this.isUnique = isUnique;
        this.isAscending = isAscending;
        this.isUsingHash = isUsingHash;
    }

    @NotNull
    public String getIndexName() {
        return indexName;
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }

    @NotNull
    public List<String> getAttributeNames() {
        return attributeNames;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public boolean isAscending() {
        return isAscending;
    }

    public boolean isUsingHash() {
        return isUsingHash;
    }

    @Override
    public void execute() {

    }

    @NotNull
    @Override
    public String toString() {
        return "CreateIndexQuery{" +
                "indexName='" + indexName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", attributeNames=" + attributeNames +
                ", isAscending=" + isAscending +
                ", isUsingHash=" + isUsingHash +
                '}';
    }
}
