package ru.spbau.tinydb.btree;

class Split {
    public final int key;
    public final Node left;
    public final Node right;

    public Split(int k, Node l, Node r) {
        key = k;
        left = l;
        right = r;
    }
}