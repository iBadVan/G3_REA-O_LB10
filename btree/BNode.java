package btree;

import java.util.ArrayList;

import javax.naming.directory.SearchResult;

public class BNode<E extends Comparable<E>> {
    protected ArrayList<E> keys;
    protected ArrayList<BNode<E>> childs;
    protected int count;

    public BNode(int n) {
        this.keys = new ArrayList<E>(n);
        this.childs = new ArrayList<BNode<E>>(n + 1);
        this.count = 0;
        for (int i = 0; i < n; i++) {
            this.keys.add(null);
            this.childs.add(null);
        }
        this.childs.add(null);
    }

    public boolean nodeFull(int n) {
        return count == n;
    }

    public boolean nodeEmpty() {
        return count == 0;
    }

    public SearchResult searchNode(E key) {
        int i = 0;
        while (i < count && key.compareTo(keys.get(i)) > 0) {
            i++;
        }
        if (i < count && key.compareTo(keys.get(i)) == 0) {
            return new SearchResult(true, i);
        } else {
            return new SearchResult(false, i); 
        }
    }

    public class SearchResult {
        public boolean found;
        public int position;

        public SearchResult(boolean found, int position) {
            this.found = found;
            this.position = position;
        }

        @Override
        public String toString() {
            return found ? "Key found at position " + position : "Key not found, descend to child " + position;
        }
    }

}