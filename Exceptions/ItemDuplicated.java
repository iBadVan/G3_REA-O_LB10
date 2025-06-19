package Exceptions;

public class ItemDuplicated extends Exception {
    public ItemDuplicated(String msg) {
        super(msg);
    }

    public ItemDuplicated() {
        super("El elemento ya existe en la estructura de datos.");
    }
}
