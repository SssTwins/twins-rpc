package pers.twins.rpc.common.exception;

import java.io.Serial;

/**
 * SerializationException
 *
 * @author twins
 * @date 2023-07-15 15:38:05
 * @since 1.0-SNAPSHOT
 */
public class SerializationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3049476568357970524L;

    public SerializationException(String message) {
        super(message);
    }
}
