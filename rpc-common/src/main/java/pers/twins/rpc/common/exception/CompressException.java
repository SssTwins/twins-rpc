package pers.twins.rpc.common.exception;

import java.io.Serial;

/**
 * @author twins
 * @date 2023-07-15 18:24:38
 */
public class CompressException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1225908983076353398L;

    public CompressException(String message) {
        super(message);
    }

    public CompressException(String message, Throwable cause) {
        super(message, cause);
    }
}
