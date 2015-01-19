package ru.spbau.tinydb.btree;

import java.util.Iterator;

import ru.spbau.tinydb.bufferManager.BufferManager;

public class IndexNode extends Node {

	protected IndexNode(int pageIndex, BufferManager bm) {
		super(pageIndex, bm);
	}

	@Override
	public Split insert(int key, int value) {
		
		return null;
	}

	@Override
	public Iterator<BxTreeEntry> find(int key, boolean includeKey) {
		int size = getSize();
		int index = findKey(key, includeKey);
		if(index == size) {
			index = size - 1;
		}
		int currentKey = getKey(index);
		int nextNodePage;
		if(key < currentKey) {
			nextNodePage = getData(index);
		} else {
			if(index == size - 1) {
				nextNodePage = getLink();
			} else {
				nextNodePage = getData(index + 1);
			}
		}
		return loadNode(nextNodePage, bm).find(key, includeKey);
	}

}
