package exceptions;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message, IOException cause) {
        super(message, cause);
    }

    public ManagerSaveException(String message) {
        super(message);
    }
}
