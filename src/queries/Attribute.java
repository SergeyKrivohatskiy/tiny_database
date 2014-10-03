package queries;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class Attribute {

    public enum DataType {
        INTEGER,
        CHAR,
        VARCHAR
    }

    @NotNull
    private final String attributeName;
    @NotNull
    private final DataType dataType;

    public Attribute(@NotNull String attributeName, @NotNull DataType dataType) {
        this.attributeName = attributeName;
        this.dataType = dataType;
    }

    @NotNull
    public String getAttributeName() {
        return attributeName;
    }

    @NotNull
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "attributeName='" + attributeName + '\'' +
                ", dataType=" + dataType +
                '}';
    }
}
