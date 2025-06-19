package btree;

public class BTree<E extends Comparable<E>> {
    private BNode<E> root;
    private int orden;
    private boolean up;
    private BNode<E> nDes;

    public BTree(int orden) {
        this.orden = orden;
        this.root = null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void insert(E cl) {
        up = false;
        E mediana;
        BNode<E> pnew;
        mediana = push(this.root, cl);
        if (up) {
            pnew = new BNode<E>(this.orden);
            pnew.count = 1;
            pnew.keys.set(0, mediana);
            pnew.childs.set(0, this.root);
            pnew.childs.set(1, nDes);
            this.root = pnew;
        }
    }

    private E push(BNode<E> current, E cl) {
        int[] pos = new int[1];
        E mediana;

        if (current == null) {
            up = true;
            nDes = null;
            return cl;
        } else {
            BNode<E>.SearchResult result = current.searchNode(cl);
            pos[0] = result.position;

            if (result.found) {
                System.out.println("Item duplicado");
                up = false;
                return null;
            }

            mediana = push(current.childs.get(pos[0]), cl);

            if (up) {
                if (current.nodeFull(this.orden - 1)) {
                    mediana = dividedNode(current, mediana, pos[0]);
                } else {
                    putNode(current, mediana, nDes, pos[0]);
                    up = false;
                }
            }

            return mediana;
        }
    }

    private void putNode(BNode<E> current, E cl, BNode<E> rd, int k) {
        for (int i = current.count - 1; i >= k; i--) {
            current.keys.set(i + 1, current.keys.get(i));
            current.childs.set(i + 2, current.childs.get(i + 1));
        }
        current.keys.set(k, cl);
        current.childs.set(k + 1, rd);
        current.count++;
    }

    private E dividedNode(BNode<E> current, E cl, int k) {
        BNode<E> rd = nDes;
        int i, posMdna;
        E mediana;

        nDes = new BNode<E>(this.orden);
        posMdna = (k <= this.orden / 2) ? this.orden / 2 : this.orden / 2 + 1;

        for (i = posMdna; i < this.orden - 1; i++) {
            nDes.keys.set(i - posMdna, current.keys.get(i));
            nDes.childs.set(i - posMdna + 1, current.childs.get(i + 1));
        }

        nDes.count = (this.orden - 1) - posMdna;
        current.count = posMdna;

        if (k <= this.orden / 2) {
            putNode(current, cl, rd, k);
        } else {
            putNode(nDes, cl, rd, k - posMdna);
        }

        mediana = current.keys.get(current.count - 1);
        nDes.childs.set(0, current.childs.get(current.count));
        current.count--;

        up = true;
        return mediana;
    }

    public void printTree() {
        printTree(this.root, 0);
    }

    private void printTree(BNode<E> node, int level) {
        if (node == null) return;
        System.out.println("Nivel " + level + ": " + node);
        for (int i = 0; i <= node.count; i++) {
            printTree(node.childs.get(i), level + 1);
        }
    }

    @Override
    public String toString() {
        String s = "";
        if (isEmpty())
            s += "BTree is empty...";
        else
            s = writeTree(this.root, null);
        return s;
    }

    private String writeTree(BNode<E> current, BNode<E> parent) {
        if (current == null) return "";

        StringBuilder sb = new StringBuilder();

        sb.append("Id.Nodo: ").append(current.idNode).append("\n");

        sb.append("Claves Nodo: [");
        for (int i = 0; i < current.count; i++) {
            sb.append(current.keys.get(i));
            if (i < current.count - 1) sb.append(", ");
        }
        sb.append("]\n");

        sb.append("Id.Padre: ");
        sb.append(parent == null ? "--" : "[" + parent.idNode + "]");
        sb.append("\n");

        sb.append("Id.Hijos: ");
        boolean hasChildren = false;
        StringBuilder hijos = new StringBuilder("[");
        for (int i = 0; i <= current.count; i++) {
            BNode<E> child = current.childs.get(i);
            if (child != null) {
                if (hasChildren) hijos.append(", ");
                hijos.append(child.idNode);
                hasChildren = true;
            }
        }
        sb.append(hasChildren ? hijos.append("]") : "--");
        sb.append("\n\n");

        for (int i = 0; i <= current.count; i++) {
            BNode<E> child = current.childs.get(i);
            if (child != null) {
                sb.append(writeTree(child, current));
            }
        }

        return sb.toString();
    }

    public boolean search(E cl) {
        return search(this.root, cl);
    }

    private boolean search(BNode<E> node, E cl) {
        if (node == null) {
            return false;
        }

        BNode<E>.SearchResult result = node.searchNode(cl);

        if (result.found) {
            System.out.println(cl + " se encuentra en el nodo " + node.idNode + " en la posición " + result.position);
            return true;
        } else {
            return search(node.childs.get(result.position), cl);
        }
    }

    public void remove(E cl) {
        if (root != null) {
            remove(root, cl);
            if (root.count == 0) {
                if (!root.nodeEmpty() && root.childs.get(0) != null) {
                    root = root.childs.get(0);
                } else {
                    root = null;
                }
            }
        }
    }

    private void remove(BNode<E> node, E key) {
        BNode<E>.SearchResult result = node.searchNode(key);
        int pos = result.position;

        if (result.found) {
            if (node.childs.get(pos) == null) {
                removeFromLeaf(node, pos);
            } else {
                removeFromInternal(node, pos);
            }
        } else {
            if (node.childs.get(pos) == null) {
                System.out.println("Clave " + key + " no encontrada.");
                return;
            }

            BNode<E> child = node.childs.get(pos);
            if (child.count < (orden / 2)) {
                fill(node, pos);
            }

            if (pos > node.count) {
                remove(node.childs.get(pos - 1), key);
            } else {
                remove(node.childs.get(pos), key);
            }
        }
    }

}
