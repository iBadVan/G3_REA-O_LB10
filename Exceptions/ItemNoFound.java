package Exceptions;

public class ItemNoFound extends Exception {
    public ItemNoFound(String msg) {
        super(msg);
    }

    public ItemNoFound() {
        super("El elemento no se encuentra en la estructura de datos.");
    }
}