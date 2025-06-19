package btree;

import java.io.IOException;

import Exceptions.ItemNoFound;

public class Main {
    public static void main(String[] args) {
        BTree<Integer> tree = new BTree<>(3);

        int[] valores = {10, 20, 5, 6, 12, 30, 7, 17, 52, 2};
        for (int val : valores) {
            tree.insert(val);
        }

        System.out.println("Contenido del árbol:");
        tree.printTree();

        System.out.println("\nBúsquedas:");
        System.out.println("¿Está el 52? → Resultado: " + tree.search(52));
        System.out.println("¿Está el 100? → Resultado: " + tree.search(100));

        try {
            BTree<Integer> tree2 = BTree.building_Btree("arbolB.txt");
            System.out.println("Árbol B construido correctamente:");
            tree2.printTree();
        } catch (ItemNoFound e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Archivo no encontrado o error de lectura.");
        }
    }
}
