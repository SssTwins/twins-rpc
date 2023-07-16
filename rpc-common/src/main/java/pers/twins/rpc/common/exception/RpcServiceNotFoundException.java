package pers.twins.rpc.common.exception;

import java.io.Serial;

/**
 * @author twins
 * @date 2023-07-16 22:48:58
 */
public class RpcServiceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8139096230374891645L;

    public RpcServiceNotFoundException(String rpcServiceName) {
        super("rpc service: " + rpcServiceName + " not found");
    }
}
