package pers.twins.rpc.common.remoting.transport.client;

import pers.twins.rpc.common.remoting.RpcRequest;

/**
 * rpc request
 *
 * @author twins
 * @date 2023-07-17 16:42:57
 */
public interface RpcRequestTransport {

    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRequest(RpcRequest rpcRequest);
}
