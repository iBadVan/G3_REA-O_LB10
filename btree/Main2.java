package btree;

public class Main2 {
    public static void main(String[] args) {
        BTree<RegistroEstudiante> arbol = new BTree<>(4);

        RegistroEstudiante[] estudiantes = {
            new RegistroEstudiante(103, "Ana"),
            new RegistroEstudiante(110, "Luis"),
            new RegistroEstudiante(101, "Carlos"),
            new RegistroEstudiante(120, "Lucía"),
            new RegistroEstudiante(115, "David"),
            new RegistroEstudiante(125, "Jorge"),
            new RegistroEstudiante(140, "Camila"),
            new RegistroEstudiante(108, "Rosa"),
            new RegistroEstudiante(132, "Ernesto"),
            new RegistroEstudiante(128, "Denis"),
            new RegistroEstudiante(145, "Enrique"),
            new RegistroEstudiante(122, "Karina"),
            new RegistroEstudiante(108, "Juan") // este será ignorado si está duplicado
        };

        for (RegistroEstudiante r : estudiantes) {
            arbol.insert(r);
        }

        System.out.println("Buscar 115 → " + arbol.buscarNombre(115));  // David
        System.out.println("Buscar 132 → " + arbol.buscarNombre(132));  // Ernesto
        System.out.println("Buscar 999 → " + arbol.buscarNombre(999));  // No encontrado

        arbol.remove(new RegistroEstudiante(101, ""));  // Carlos eliminado
        arbol.insert(new RegistroEstudiante(106, "Sara"));
        System.out.println("Buscar 106 → " + arbol.buscarNombre(106));  // Sara

        System.out.println("\nÁrbol final:");
        arbol.printTree();
    }
}
