package pers.twins.rpc.common.remoting.service.discovery;

import pers.twins.rpc.common.remoting.RpcRequest;

import java.net.InetSocketAddress;

/**
 * gets a service address to invoke
 *
 * @author twins
 * @date 2023-07-16 22:17:52
 */
public interface ServiceDiscovery {

    /**
     * gets a service by rpcRequest properties address to invoke
     *
     * @param rpcRequest rpcRequest
     * @return service address
     */
    InetSocketAddress lookupToInvoke(RpcRequest rpcRequest);
}
