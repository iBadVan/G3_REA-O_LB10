package btree;

import java.util.ArrayList;

public class BNode<E extends Comparable<E>> {
    protected ArrayList<E> keys;
    protected ArrayList<BNode<E>> childs;
    protected int count;
    protected final int idNode;
    private static int idCounter = 0;

    public BNode(int n) {
        this.keys = new ArrayList<E>(n);
        this.childs = new ArrayList<BNode<E>>(n + 1);
        this.count = 0;
        this.idNode = ++idCounter; 

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
            return found ? "Clave encontrada en la posición " + position : "Clave no encontrada, descienda al hijo " + position;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodo ID ").append(idNode).append(": [");
        for (int i = 0; i < count; i++) {
            sb.append(keys.get(i));
            if (i < count - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

}
