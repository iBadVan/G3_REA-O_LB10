package Exceptions;

public class ExceptionIsEmpty extends Exception {
    public ExceptionIsEmpty(String msg) {
        super(msg);
    }

    public ExceptionIsEmpty() {
        super("La estructura de datos está vacía.");
    }
}