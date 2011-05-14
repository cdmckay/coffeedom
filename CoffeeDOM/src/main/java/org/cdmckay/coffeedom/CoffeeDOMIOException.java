package org.cdmckay.coffeedom;

import java.io.IOException;

/**
 * This is an unchecked version of the Java {@see java.io.IOException} class.
 */
public class CoffeeDOMIOException extends CoffeeDOMException {

    public CoffeeDOMIOException(String message, IOException exception) {
        super(message, exception);
    }

    public CoffeeDOMIOException(IOException exception) {
        this("An I/O error occurred in the CoffeeDOM application.", exception);
    }

}
