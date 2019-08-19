package demo.fabric.exception;

import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
public class FabricClientCreateException extends Exception {

    public FabricClientCreateException(String message, Throwable parent) {
        super(message, parent);
    }

    public FabricClientCreateException(String message) {
        super(message);
    }

    public FabricClientCreateException(Throwable parent) {
        super(parent);
    }
}
