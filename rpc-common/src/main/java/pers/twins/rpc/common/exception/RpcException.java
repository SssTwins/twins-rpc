package pers.twins.rpc.common.exception;

/**
 * @author twins
 * @date 2023-07-17 16:21:17
 */
public class RpcException extends RuntimeException {

    public RpcException(String msg) {
        super(msg);
    }

    public RpcException(Exception e) {
        super(e.getMessage(), e);
    }

}
