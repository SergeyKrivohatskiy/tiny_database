package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.tinyDatabase.TinyDatabase;

import java.util.Collections;
import java.util.List;

/**
 * @author adkozlov
 */
public class CreateTableQuery extends TableNameContainer implements IQuery {

    @NotNull
    private final List<Attribute> attributes;

    public CreateTableQuery(@NotNull String tableName, @NotNull List<Attribute> attributes) {
        super(tableName);
        this.attributes = Collections.unmodifiableList(attributes);
    }

    public @NotNull List<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public void execute() {
    	try {
			TinyDatabase db = TinyDatabase.getInstance();
            if (db.createTable(getTableName(), getAttributes())) {
                //table is created
			} else {
				//table is already exist
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @NotNull
    @Override
    public String toString() {
        return "CreateTableQuery{" +
                "attributes=" + attributes +
                "} " + super.toString();
    }
}
