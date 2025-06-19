package btree;

import java.util.ArrayList;

public class BNode<E extends Comparable<E>> {
    protected ArrayList<E> claves;
    protected ArrayList<BNode<E>> hijos;
    protected int cantidad;
    protected final int idNodo;
    private static int contadorId = 0;

    public BNode(int n) {
        this.claves = new ArrayList<E>(n);
        this.hijos = new ArrayList<BNode<E>>(n + 1);
        this.cantidad = 0;
        this.idNodo = ++contadorId; 

        for (int i = 0; i < n; i++) {
            this.claves.add(null);
            this.hijos.add(null);
        }
        this.hijos.add(null);
    }

    public boolean nodoLleno(int n) {
        return cantidad == n;
    }

    public boolean nodoVacio() {
        return cantidad == 0;
    }

    public ResultadoBusqueda buscarNodo(E clave) {
        int i = 0;
        while (i < cantidad && clave.compareTo(claves.get(i)) > 0) {
            i++;
        }
        if (i < cantidad && clave.compareTo(claves.get(i)) == 0) {
            return new ResultadoBusqueda(true, i);
        } else {
            return new ResultadoBusqueda(false, i); 
        }
    }

    public class ResultadoBusqueda {
        public boolean encontrado;
        public int posicion;

        public ResultadoBusqueda(boolean encontrado, int posicion) {
            this.encontrado = encontrado;
            this.posicion = posicion;
        }

        @Override
        public String toString() {
            return encontrado ? "Clave encontrada en la posición " + posicion : "Clave no encontrada, descender al hijo " + posicion;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodo ID ").append(idNodo).append(": [");
        for (int i = 0; i < cantidad; i++) {
            sb.append(claves.get(i));
            if (i < cantidad - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
