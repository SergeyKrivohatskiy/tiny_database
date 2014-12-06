package ru.spbau.tinydb.table;

import java.util.Map;

import ru.spbau.tinydb.queries.SecondLevelId;

public class Record {
    private final Map<SecondLevelId, Object> atributes;
    private final int recordId;
    
    public Record(Map<SecondLevelId, Object> value, int recordId) {
        this.atributes = value;
        this.recordId = recordId;
    }

    public int getRecordId() {
        return recordId;
    }

    public Map<SecondLevelId, Object> getAtributes() {
        return atributes;
    }
}
