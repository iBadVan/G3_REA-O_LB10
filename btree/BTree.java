package btree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Exceptions.ItemNoFound;

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

    private void removeFromLeaf(BNode<E> node, int idx) {
        for (int i = idx + 1; i < node.count; i++) {
            node.keys.set(i - 1, node.keys.get(i));
        }
        node.keys.set(node.count - 1, null);
        node.count--;
    }

    private void removeFromInternal(BNode<E> node, int idx) {
        E key = node.keys.get(idx);
        BNode<E> predChild = node.childs.get(idx);
        BNode<E> succChild = node.childs.get(idx + 1);

        if (predChild.count >= (orden / 2)) {
            E pred = getPredecessor(predChild);
            node.keys.set(idx, pred);
            remove(predChild, pred);
        } else if (succChild.count >= (orden / 2)) {
            E succ = getSuccessor(succChild);
            node.keys.set(idx, succ);
            remove(succChild, succ);
        } else {
            merge(node, idx);
            remove(predChild, key);
        }
    }

    private E getPredecessor(BNode<E> node) {
        while (node.childs.get(node.count) != null)
            node = node.childs.get(node.count);
        return node.keys.get(node.count - 1);
    }

    private E getSuccessor(BNode<E> node) {
        while (node.childs.get(0) != null)
            node = node.childs.get(0);
        return node.keys.get(0);
    }

    private void fill(BNode<E> node, int idx) {
        if (idx != 0 && node.childs.get(idx - 1).count >= (orden / 2)) {
            borrowFromPrev(node, idx);
        } else if (idx != node.count && node.childs.get(idx + 1).count >= (orden / 2)) {
            borrowFromNext(node, idx);
        } else {
            if (idx != node.count) {
                merge(node, idx);
            } else {
                merge(node, idx - 1);
            }
        }
    }

    private void borrowFromPrev(BNode<E> node, int idx) {
        BNode<E> child = node.childs.get(idx);
        BNode<E> sibling = node.childs.get(idx - 1);

        for (int i = child.count - 1; i >= 0; i--)
            child.keys.set(i + 1, child.keys.get(i));
        if (!child.nodeEmpty()) {
            for (int i = child.count; i >= 0; i--)
                child.childs.set(i + 1, child.childs.get(i));
        }

        child.keys.set(0, node.keys.get(idx - 1));
        if (!sibling.nodeEmpty())
            child.childs.set(0, sibling.childs.get(sibling.count));

        node.keys.set(idx - 1, sibling.keys.get(sibling.count - 1));

        child.count++;
        sibling.count--;
    }

    private void borrowFromNext(BNode<E> node, int idx) {
        BNode<E> child = node.childs.get(idx);
        BNode<E> sibling = node.childs.get(idx + 1);

        child.keys.set(child.count, node.keys.get(idx));
        if (!sibling.nodeEmpty())
            child.childs.set(child.count + 1, sibling.childs.get(0));

        node.keys.set(idx, sibling.keys.get(0));

        for (int i = 1; i < sibling.count; i++)
            sibling.keys.set(i - 1, sibling.keys.get(i));
        if (!sibling.nodeEmpty()) {
            for (int i = 1; i <= sibling.count; i++)
                sibling.childs.set(i - 1, sibling.childs.get(i));
        }

        child.count++;
        sibling.count--;
    }

    private void merge(BNode<E> node, int idx) {
        BNode<E> child = node.childs.get(idx);
        BNode<E> sibling = node.childs.get(idx + 1);

        child.keys.set(orden / 2 - 1, node.keys.get(idx));

        for (int i = 0; i < sibling.count; i++) {
            child.keys.set(i + (orden / 2), sibling.keys.get(i));
        }

        for (int i = 0; i <= sibling.count; i++) {
            child.childs.set(i + (orden / 2), sibling.childs.get(i));
        }

        for (int i = idx + 1; i < node.count; i++) {
            node.keys.set(i - 1, node.keys.get(i));
            node.childs.set(i, node.childs.get(i + 1));
        }

        child.count += sibling.count + 1;
        node.count--;
    }

    public static BTree<Integer> building_Btree(String filename) throws IOException, ItemNoFound {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;

        int orden = Integer.parseInt(br.readLine().trim());
        BTree<Integer> btree = new BTree<>(orden);

        Map<Integer, BNode<Integer>> nodeMap = new HashMap<>();
        Map<Integer, Integer> nodeLevel = new HashMap<>();
        Map<Integer, List<Integer>> nodeChildren = new HashMap<>();

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            int nivel = Integer.parseInt(parts[0].trim());
            int id = Integer.parseInt(parts[1].trim());

            BNode<Integer> node = new BNode<>(orden);
            node.count = parts.length - 2;

            for (int i = 2; i < parts.length; i++) {
                node.keys.set(i - 2, Integer.parseInt(parts[i].trim()));
            }

            nodeMap.put(id, node);
            nodeLevel.put(id, nivel);
        }
        br.close();

        for (Map.Entry<Integer, BNode<Integer>> entry : nodeMap.entrySet()) {
            int parentLevel = nodeLevel.get(entry.getKey());
            BNode<Integer> parent = entry.getValue();

            for (Map.Entry<Integer, BNode<Integer>> childEntry : nodeMap.entrySet()) {
                int childLevel = nodeLevel.get(childEntry.getKey());

                if (childLevel == parentLevel + 1) {
                    BNode<Integer> child = childEntry.getValue();
                    for (int i = 0; i <= parent.count; i++) {
                        if (parent.childs.get(i) == null) {
                            parent.childs.set(i, child);
                            break;
                        }
                    }
                }
            }
        }

        for (Map.Entry<Integer, Integer> entry : nodeLevel.entrySet()) {
            if (entry.getValue() == 0) {
                btree.root = nodeMap.get(entry.getKey());
                break;
            }
        }

        if (!btree.validateBTree()) {
            throw new ItemNoFound("El árbol no cumple con las propiedades de un árbol B.");
        }

        return btree;
    }

    private boolean validateBTree() {
        return validateNode(this.root, 0) != -1;
    }

    private int validateNode(BNode<E> node, int depth) {
        if (node == null) return depth;

        if (node.count < 1 || node.count >= orden) return -1;

        int expectedLeafDepth = -1;
        for (int i = 0; i <= node.count; i++) {
            BNode<E> child = node.childs.get(i);
            int result = validateNode(child, depth + 1);
            if (child != null) {
                if (expectedLeafDepth == -1) {
                    expectedLeafDepth = result;
                } else if (expectedLeafDepth != result) {
                    return -1; 
                }
            }
        }
        return expectedLeafDepth == -1 ? depth : expectedLeafDepth;
    }

    public String buscarNombre(int codigo) {
        return buscarNombreRecursivo(this.root, codigo);
    }

    private String buscarNombreRecursivo(BNode<E> root2, int codigo) {
        if (root2 == null) return "No encontrado";

        for (int i = 0; i < root2.count; i++) {
            RegistroEstudiante estudiante = (RegistroEstudiante) root2.keys.get(i);
            if (estudiante.getCodigo() == codigo) {
                return estudiante.getNombre();
            } else if (codigo < estudiante.getCodigo()) {
                return buscarNombreRecursivo(root2.childs.get(i), codigo);
            }
        }
        return buscarNombreRecursivo(root2.childs.get(root2.count), codigo);
    }


}
