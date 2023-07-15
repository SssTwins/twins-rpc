package pers.twins.rpc.common.exception;

/**
 * SerializationException
 *
 * @author twins
 * @date 2023-07-15 15:38:05
 * @since 1.0-SNAPSHOT
 */
public class SerializationException extends RuntimeException {

    public SerializationException(String message) {
        super(message);
    }
}
