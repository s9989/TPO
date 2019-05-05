package zad1.server;

import java.nio.channels.SelectionKey;

public class SelectionKeyException extends Exception {

    private SelectionKey key;

    public SelectionKey getKey() {
        return key;
    }

    public SelectionKeyException(String message, Throwable cause, SelectionKey key) {
        super(message, cause);
        this.key = key;
    }
}
