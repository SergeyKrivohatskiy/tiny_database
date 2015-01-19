package ru.spbau.tinydb.btree;

import java.util.Iterator;

import ru.spbau.tinydb.bufferManager.BufferManager;
import ru.spbau.tinydb.utils.Utils;

public class LeafNode extends Node {

	protected LeafNode(int pageIndex, BufferManager bm) {
		super(pageIndex, bm);
	}

	@Override
	public Split insert(int key, int value) {
		int size = getSize();
		if(size == N) {
			return null;
		}
		insert(findKey(key, true), key, value);
		return null;
	}

	@Override
	public Iterator<BxTreeEntry> find(int key, boolean includeKey) {
		int size = getSize();
		if(size == 0) {
			return Utils.emptyIterator();
		}
		return new Iterator<BxTreeEntry>() {
			private LeafNode nextNode = LeafNode.this;
			private Iterator<BxTreeEntry> currentBaseIter = null;
			private BxTreeEntry val = getNext();
			
			@Override
			public boolean hasNext() {
				
				return val != null;
			}

			private BxTreeEntry getNext() {
				while(currentBaseIter == null || !currentBaseIter.hasNext()) {
					if(nextNode == null) {
						return null;
					}
					currentBaseIter = nextNode.baseFind(key, includeKey);
					int nextNodePage = nextNode.getLink();
					nextNode = nextNodePage == 0 ? null : new LeafNode(nextNodePage, bm);
				}
				return currentBaseIter.next();
			}

			@Override
			public BxTreeEntry next() {
				if(!hasNext()) {
					throw new IllegalStateException();
				}
				BxTreeEntry old = val;
				val = getNext();
				return old;
			}
		};
	}
	
	private Iterator<BxTreeEntry> baseFind(int key, boolean includeKey) {
		return new Iterator<BxTreeEntry>() {
			private int index = findKey(key, includeKey); 
			private int size = getSize();
			@Override
			public boolean hasNext() {
				return index < size;
			}

			@Override
			public BxTreeEntry next() {
				if(!hasNext()) {
					throw new IllegalStateException();
				}
				BxTreeEntry result = new BxTreeEntry(getKey(index), getData(index));
				index ++;
				return result;
			}
		};
	}

}
