package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.tinyDatabase.TinyDatabase;

import java.util.Collections;
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
        this.attributes = Collections.unmodifiableList(attributes);
    }

    @NotNull
    public String getId() {
        return id;
    }

    public @NotNull List<Attribute> getAttributes() {
        return attributes;
    }

    @NotNull
    @Override
    public String toString() {
        return "CreateTableQuery{" +
                "id='" + id + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    @Override
    public void execute() {
    	try {
			TinyDatabase db = TinyDatabase.getInstance();
            if (db.createTable(getId(), getAttributes())) {
                //table is created
			} else {
				//table is already exist
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
