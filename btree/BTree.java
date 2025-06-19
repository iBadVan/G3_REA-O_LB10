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
    

}
