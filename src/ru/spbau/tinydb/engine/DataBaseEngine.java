package ru.spbau.tinydb.engine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.spbau.tinydb.btree.BxTree;
import ru.spbau.tinydb.btree.BxTreeEntry;
import ru.spbau.tinydb.bufferManager.BufferManager;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.cursors.AtributesCursor;
import ru.spbau.tinydb.cursors.NLJoinCursor;
import ru.spbau.tinydb.cursors.WhereCursor;
import ru.spbau.tinydb.expressions.comparison.JoinOnExpression;
import ru.spbau.tinydb.metainformation.MetaInformationTable;
import ru.spbau.tinydb.queries.Attribute;
import ru.spbau.tinydb.queries.SecondLevelId;
import ru.spbau.tinydb.queries.SelectionTable;
import ru.spbau.tinydb.queries.WhereCondition;
import ru.spbau.tinydb.table.Record;
import ru.spbau.tinydb.table.Table;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author adkozlov
 */
public class DataBaseEngine implements AutoCloseable {

    @NotNull
    private static final DataBaseEngine INSTANCE = new DataBaseEngine();
    @NotNull
    private static final Map<String, IDataBase> DB_INSTANCES = new HashMap<>();

    private DataBaseEngine() {
    }

    public static DataBaseEngine getInstance() {
        return INSTANCE;
    }

    @NotNull
    public static IDataBase getDBInstance(@NotNull String dbFileName) throws DBException {
        IDataBase result = DB_INSTANCES.get(dbFileName);

        if (result == null) {
            result = new DataBase(dbFileName);
            DB_INSTANCES.put(dbFileName, result);
        }

        return result;
    }

    @Override
    public void close() throws Exception {
        for (IDataBase instance : DB_INSTANCES.values()) {
            instance.close();
        }
    }

    public static class DataBase implements IDataBase {

        @NotNull
        private final MetaInformationTable metaInf;
        @NotNull
        private final BufferManager bufferManager;

        public DataBase(@NotNull String dbFileName) throws DBException {
            bufferManager = new BufferManager(dbFileName);
            metaInf = new MetaInformationTable(bufferManager);
        }

        @Override
        public int delete(@NotNull String tableName, @NotNull WhereCondition filter) throws DBException {
            Table table = findTable(tableName);
            Iterator<Record> recordIterator = executeFilter(filter, table);
            int removed = 0;

            while (recordIterator.hasNext()) {
                Record record = recordIterator.next();
                if (filter.check(record.getAtributes())) {
                    table.remove(record.getRecordId());
                    removed += 1;
                }
            }

            return removed;
        }

        public int insert(@NotNull String tableName,
                          @NotNull List<String> attrs, @NotNull List<Object> record)
                throws DBException {
            Table table = findTable(tableName);

            Collection<Attribute> schema = table.getSchema();
            Object[] row = new Object[schema.size()];

            int i = 0;
            for (Attribute attr : schema) {
                int idx = attrs.indexOf(attr.getAttributeName());
                if (idx == -1) {
                	// TODO Maybe move this out?
                	// DB shouldn't think about default atr values IMO
                    row[i] = getDefault(attr.getDataType());
                } else {
                    row[i] = record.get(idx);
                }
                i += 1;
            }

            try {
                table.insertRecord(row);
            } catch (UnsupportedEncodingException | ExecutionException | ClassCastException e) {
                throw new DBException(e);
            }

            return 1;
        }

        @NotNull
        private Table findTable(@NotNull String tableName) throws DBException {
        	// This is O(Table sizes sum) operation
        	// TODO create table cache here or inside of metaInf
        	// Do not forget about indexes
            Table table = metaInf.loadTable(tableName);

            if (table == null) {
                throw new DBException("Table " + tableName + " doesn't exist");
            }
            return table;
        }

        private Object getDefault(@NotNull Attribute.DataType dataType) {
            if (dataType instanceof Attribute.IntegerType) {
                return 0;
            }
            if (dataType instanceof Attribute.DoubleType) {
                return (double) 0;
            }
            if (dataType instanceof Attribute.VarcharType) {
                return "";
            }

            throw new DBException("Unsupported attribute type");
        }

        public boolean createTable(@NotNull String tableName,
                                   @NotNull Collection<Attribute> schema) throws DBException {
            Table table = metaInf.loadTable(tableName);

            if (table == null) {
                try {
                    metaInf.createTable(tableName, schema);
                } catch (UnsupportedEncodingException | ExecutionException e) {
                    throw new DBException(e);
                }

                return true;
            } else {
                return false;
            }
        }

        @Nullable
        public Collection<Attribute> getTableSchema(@NotNull String tableName) {
            Table table = metaInf.loadTable(tableName);

            return table != null ? table.getSchema() : null;
        }

        @Override
        public void close() throws Exception {
            flush();
        }

        @Override
        public void flush() {
            bufferManager.flushBuffer();
        }

        @Override
        @NotNull
        public Iterator<Map<SecondLevelId, Object>> select(@NotNull SelectionTable tableSelection, @Nullable WhereCondition filter) {
            Table table = findTable(tableSelection.getTableName());
            
			Iterator<Record> recordCursor = executeFilter(filter, table);
            Iterator<Map<SecondLevelId, Object>> resultCursor = new AtributesCursor(recordCursor);

            for (JoinOnExpression joinExpression : tableSelection.getExpressions()) {
                final Table joinTable = findTable(joinExpression.getTableName());

                resultCursor = new NLJoinCursor(resultCursor, new Iterable<Map<SecondLevelId, Object>>() {
                    @Override
                    public Iterator<Map<SecondLevelId, Object>> iterator() {
                        return new AtributesCursor(joinTable.iterator());
                    }
                }, joinExpression);
            }

            return resultCursor;
        }

		private Iterator<Record> executeFilter(WhereCondition filter,
				Table table) {
			Attribute atr = getAtr(filter);
			if(atr != null) {
				BxTree index = table.getIndex(atr);
				// TODO filter to index request
				int from = 0;
				int to = 0;
				boolean includeFrom = false;
				boolean includeTo = false;
				
				Iterator<BxTreeEntry> indexIter = index.find(from, to, includeFrom, includeTo);
				
				return indexIterToRecordIter(table, indexIter);
			}
			return new WhereCursor(table .iterator(), filter);
		}

		private Iterator<Record> indexIterToRecordIter(Table table,
				Iterator<BxTreeEntry> indexIter) {
			return new Iterator<Record>() {
				private Record rec = getNext();
				@Override
				public Record next() {
					if(!hasNext()) {
						throw new IllegalStateException();
					}
					Record oldRec = rec;
					rec = getNext();
					return oldRec;
				}
				
				private Record getNext() {
					try {
						while(indexIter.hasNext()) {
							BxTreeEntry entry = indexIter.next();
							int recordId = entry.value;
							Map<SecondLevelId, Object> atrs = table.getRecord(recordId);
							if(atrs != null) {
								return new Record(atrs, recordId);
							}
						}
						return null;
					} catch (ExecutionException e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public boolean hasNext() {
					return rec != null;
				}
			};
		}

		private Attribute getAtr(WhereCondition filter) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean createIndex(String tableName, List<String> attributeNames)
				throws DBException {
			if(attributeNames.size() != 1) {
				throw new DBException("Index for 1 atribute only");
			}
        	String attributeName = attributeNames.get(0);
			Table table = findTable(tableName);
			int idx = 0;
			for(Attribute atr: table.getSchema()) {
				if(atr.getAttributeName().equals(attributeName)) {
	        		break;
	        	}
	        	idx += 1;
	        }
			return metaInf.createIndex(tableName, idx, attributeName);
		}
    }
}
