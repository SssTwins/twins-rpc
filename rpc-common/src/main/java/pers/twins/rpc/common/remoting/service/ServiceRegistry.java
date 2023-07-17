package pers.twins.rpc.common.remoting.service;

import java.net.InetSocketAddress;

/**
 * service registration
 *
 * @author twins
 * @date 2023-07-16 22:17:52
 */
public interface ServiceRegistry {

    /**
     * register service
     *
     * @param rpcServiceName    rpc service name
     * @param inetSocketAddress service address
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

    /**
     * unregister service
     *
     * @param rpcServiceName    rpc service name
     * @param inetSocketAddress service address
     */
    void unregisterService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
