package queries;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class Attribute {

    public abstract static class DataType {
    }

    public static class IntegerType extends DataType {

        @Override
        @NotNull
        public String toString() {
            return "integer";
        }
    }

    public static class DoubleType extends DataType {

        @Override
        @NotNull
        public String toString() {
            return "double";
        }
    }

    public static class VarcharType extends DataType {

        private final int length;

        public VarcharType(int length) {
            if (length < 1) {
                throw new CreateTableException("Varchar length cannot be less than 1.");
            }

            this.length = length;
        }

        public int getLength() {
            return length;
        }

        @Override
        @NotNull
        public String toString() {
            return "varchar(" + length + ")";
        }
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
    @NotNull
    public String toString() {
        return "Attribute{" +
                "attributeName='" + attributeName + '\'' +
                ", dataType=" + dataType +
                '}';
    }
}
