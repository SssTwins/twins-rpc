package pers.twins.rpc.common.exception;

import java.io.Serial;

/**
 * @author twins
 * @date 2023-07-17 16:21:17
 */
public class RpcException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5403536054310018246L;

    public RpcException(String msg) {
        super(msg);
    }

    public RpcException(Exception e) {
        super(e.getMessage(), e);
    }

}
