package ru.spbau.tinydb.btree;


import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.bufferManager.BufferManager;
import ru.spbau.tinydb.bufferManager.BufferView;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.utils.Utils;

public class BxTree<Key extends BxTree.KeyType> {
    /**
     * the maximum number of keys in the leaf node, N must be > 0
     */
    private final int N;
    private final BufferManager manager;
    /**
     * Pointer to the root node. It may be a leaf or an inner node, but it is never null.
     */
    private Node root;

    /**
     * Create a new empty tree.
     */
    public BxTree(BufferManager bufferManager, int size) {
        this.manager = bufferManager;
        N = size;
        root = new LNode(bufferManager.getFreePage());
        root.write();

    }

    public BxTree(BufferManager bufferManager, int rootPage, int size) {
        this.manager = bufferManager;
        N = size;
    }


    public void insert(Key key, Integer value) {
        System.out.println("insert key=" + key);
        Split result = root.insert(key, value);
        if (result != null) {
            // The old root was splitted in two parts.
            // We have to create a new root pointing to them
            INode _root = new INode();
            _root.num = 1;
            _root.keys[0] = result.key;
            _root.children[0] = result.left;
            _root.children[1] = result.right;
            root = _root;
        }
    }

    /**
     * Looks for the given key. If it is not found, it returns null.
     * If it is found, it returns the associated value.
     */
    public Integer find(Key key) {
        Node node = root;
        while (node instanceof BxTree.INode) { // need to traverse down to the leaf
            INode inner = (INode) node;
            int idx = inner.getLoc(key);
            node = inner.children[idx];
        }

        //We are @ leaf after while loop
        LNode leaf = (LNode) node;
        int idx = leaf.getLoc(key);
        if (idx < leaf.num && leaf.keys[idx].equals(key)) {
            return leaf.values[idx];
        } else {
            return null;
        }
    }

    public void dump() {
        root.dump();
    }

    enum type_id {
        leaf,
        inner
    }

    abstract static public class KeyType implements Comparable {
        abstract int getSize();

        abstract byte[] getBytes();
    }

    static public class IntegerKey extends KeyType {
        private int key;

        public IntegerKey(int key) {
            this.key = key;
        }

        @Override
        int getSize() {
            return Integer.SIZE;
        }

        @Override
        byte[] getBytes() {
            return Utils.intToBytes(key);
        }

        @Override
        public int compareTo(@NotNull Object o) {
            if (o instanceof IntegerKey) {
                IntegerKey other = (IntegerKey) o;
                return key - other.key;
            } else {
                throw new DBException("Incorrect type of key in index");
            }
        }
    }

    static public class DoubleKey extends KeyType {
        private double key;

        public DoubleKey(double key) {
            this.key = key;
        }

        @Override
        int getSize() {
            return Double.SIZE;
        }

        @Override
        byte[] getBytes() {
            return Utils.doubleToBytes(key);
        }

        @Override
        public int compareTo(@NotNull Object o) {
            if (o instanceof DoubleKey) {
                return Double.compare(key, ((DoubleKey) o).key);
            } else {
                throw new DBException("Incorrect type of key in index");
            }
        }
    }

    abstract class Node {
        protected int num;
        protected Key[] keys;
        protected int page;

        abstract public int getLoc(Key key);

        // returns null if no split, otherwise returns split info
        abstract public Split insert(Key key, Integer value);

        abstract public void dump();

        abstract public int size();

        public abstract void write();

        abstract public void load();

        public abstract type_id checkClassType();

    }

    class LNode extends Node {
        public final type_id TYPE_ID = type_id.leaf;
        int[] values;

        LNode(int pageIndex) {
            page = pageIndex;
        }

        LNode() {
            page = manager.getFreePage();
            values = new int[N];

        }

        /**
         * Returns the position where 'key' should be inserted in a leaf node
         * that has the given keys.
         */
        public int getLoc(Key key) {
            // Simple linear search.
            for (int i = 0; i < num; i++) {
                if (keys[i].compareTo(key) >= 0) {
                    return i;
                }
            }
            return num;
        }

        public Split insert(Key key, Integer value) {
            // Simple linear search
            int i = getLoc(key);
            if (this.num == N) { // The node was full. We must split it
                int mid = (N + 1) / 2;
                int sNum = this.num - mid;
                LNode sibling = new LNode();

                sibling.num = sNum;
                System.arraycopy(this.keys, mid, sibling.keys, 0, sNum);
                System.arraycopy(this.values, mid, sibling.values, 0, sNum);
                this.num = mid;
                if (i < mid) {
                    // Inserted element goes to left sibling
                    this.insertNonfull(key, value, i);
                } else {
                    // Inserted element goes to right sibling
                    sibling.insertNonfull(key, value, i - mid);
                }
                // Notify the parent about the split
                Split result = new Split(sibling.keys[0], //make the right's key >= result.key
                        this,
                        sibling);
                return result;
            } else {
                // The node was not full
                this.insertNonfull(key, value, i);
                return null;
            }
        }

        private void insertNonfull(Key key, Integer value, int idx) {
            //if (idx < N && keys[idx].equals(key)) {
            if (idx < num && keys[idx].equals(key)) {
                // We are inserting a duplicate value, simply overwrite the old one
                values[idx] = value;
            } else {
                // The key we are inserting is unique
                System.arraycopy(keys, idx, keys, idx + 1, num - idx);
                System.arraycopy(values, idx, values, idx + 1, num - idx);

                keys[idx] = key;
                values[idx] = value;
                num++;
            }
        }

        public void dump() {

            System.out.println("lNode h==0");
            for (int i = 0; i < num; i++) {
                System.out.println(keys[i]);
            }
        }

        @Override
        public int size() {
            return this.keys[0].getSize() * N + N * Integer.SIZE;
        }

        @Override
        public void write() {
            BufferView view = manager.getPage(page);
            int lastIndex = 0;
            view.setBytes(lastIndex, Utils.intToBytes(TYPE_ID.ordinal()));
            lastIndex += Integer.SIZE;
            view.setBytes(lastIndex, Utils.intToBytes(num));
            for (int i = 0; i < num; i++) {
                view.setBytes(lastIndex, keys[i].getBytes());
                lastIndex += keys[i].getSize();
            }
            for (int i = 0; i < num; i++) {
                view.setBytes(lastIndex, Utils.intToBytes(values[i]));
                lastIndex += Integer.SIZE;
            }
        }

        @Override
        public void load() {
            if (page == 0)
                throw new DBException("Incorrect page in BTreeIndex");
            BufferView view = manager.getPage(page);

        }

        @Override
        public type_id checkClassType() {
            return TYPE_ID;
        }
    }

    class INode extends Node {
        private final static int typeId = 1;
        final Node[] children = new BxTree.Node[N + 1];

        {
            keys = (Key[]) new Comparable[N];
        }

        /**
         * Returns the position where 'key' should be inserted in an inner node
         * that has the given keys.
         */
        public int getLoc(Key key) {
            // Simple linear search. Faster for small values of N or N
            for (int i = 0; i < num; i++) {
                if (keys[i].compareTo(key) > 0) {
                    return i;
                }
            }
            return num;
            // Binary search is faster when N or N is big,
        }

        public Split insert(Key key, Integer value) {
        /* Early split if node is full.
         * This is not the canonical algorithm for B+ trees,
	     * but it is simpler and it does break the definition
	     * which might result in immature split, which might not be desired in database
	     * because additional split lead to tree's height increase by 1, thus the number of disk read
	     * so first search to the leaf, and split from bottom up is the correct approach.
	     */

            if (this.num == N) { // Split
                int mid = (N + 1) / 2;
                int sNum = this.num - mid;
                INode sibling = new INode();
                sibling.num = sNum;
                System.arraycopy(this.keys, mid, sibling.keys, 0, sNum);
                System.arraycopy(this.children, mid, sibling.children, 0, sNum + 1);

                this.num = mid - 1;//this is important, so the middle one elevate to next depth(height), inner node's key don't repeat itself

                // Set up the return variable
                Split result = new Split(this.keys[mid - 1],
                        this,
                        sibling);

                // Now insert in the appropriate sibling
                if (key.compareTo(result.key) < 0) {
                    this.insertNonfull(key, value);
                } else {
                    sibling.insertNonfull(key, value);
                }
                return result;

            } else {// No split
                this.insertNonfull(key, value);
                return null;
            }
        }

        private void insertNonfull(Key key, Integer value) {
            // Simple linear search
            int idx = getLoc(key);
            Split result = children[idx].insert(key, value);

            if (result != null) {
                if (idx == num) {
                    // Insertion at the rightmost key
                    keys[idx] = result.key;
                    children[idx] = result.left;
                    children[idx + 1] = result.right;
                    num++;
                } else {
                    // Insertion not at the rightmost key
                    //shift i>idx to the right
                    System.arraycopy(keys, idx, keys, idx + 1, num - idx);
                    System.arraycopy(children, idx, children, idx + 1, num - idx + 1);

                    children[idx] = result.left;
                    children[idx + 1] = result.right;
                    keys[idx] = result.key;
                    num++;
                }
            } // else the current node is not affected
        }

        /**
         * This one only dump integer key
         */
        public void dump() {
            System.out.println("iNode h==?");
            for (int i = 0; i < num; i++) {
                children[i].dump();
                System.out.print('>');
                System.out.println(keys[i]);
            }
            children[num].dump();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void write() {

        }

        @Override
        public void load() {

        }

        @Override
        public type_id checkClassType() {
            return null;
        }
    }

    class Split {
        public final Key key;
        public final Node left;
        public final Node right;

        public Split(Key k, Node l, Node r) {
            key = k;
            left = l;
            right = r;
        }
    }
}