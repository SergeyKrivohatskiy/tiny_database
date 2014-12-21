package ru.spbau.tinydb.cursors;

import java.util.Iterator;
import java.util.Map;

import ru.spbau.tinydb.queries.SecondLevelId;
import ru.spbau.tinydb.table.Record;

public class AtributesCursor implements
        Iterator<Map<SecondLevelId, Object>> {

    private final Iterator<Record> baseIterator;

    public AtributesCursor(Iterator<Record> recordsIterator) {
        this.baseIterator = recordsIterator;
    }

    @Override
    public boolean hasNext() {
        return baseIterator.hasNext();
    }

    @Override
    public Map<SecondLevelId, Object> next() {
        return baseIterator.next().getAtributes();
    }

	@Override
	public void remove() {
		baseIterator.remove();
	}

}
