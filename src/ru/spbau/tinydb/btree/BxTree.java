package ru.spbau.tinydb.btree;

import java.util.Iterator;

import ru.spbau.tinydb.bufferManager.BufferManager;

public class BxTree {
	private final BufferManager bm;

    public BxTree(BufferManager bufferManager, int firstPage) {
		// TODO Auto-generated constructor stub
    	this.bm = bufferManager;
    	bm.getPage(firstPage).close();
	}

	public void insert(int key, int recordId) {
		// TODO Auto-generated method stub
	}
	
	public Iterator<BxTreeEntry> find(int from, int to, boolean includeFrom, boolean includeTo) {
		// TODO
		// Execution plan
		// 1. Find first Leaf Node element ">="(or >) from value
		// 2. Create an leaf nodes iterator
		return null;
	}
}