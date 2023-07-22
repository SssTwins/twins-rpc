package pers.twins.rpc.common.remoting.service;

import java.net.InetSocketAddress;

/**
 * rpc服务提供类
 *
 * @author twins
 * @date 2023-07-16 22:04:38
 */
public interface ServiceProvider {

    /**
     * get a rpcService by service name
     *
     * @param rpcServiceName rpc service name
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     * publish service
     *
     * @param rpcService the service wrapper class used by rpc
     * @param serverPort server port
     */
    void publishService(RpcService rpcService, int serverPort);

    /**
     * 移除当前应用所有已注册的服务
     *
     * @param inetSocketAddress inetSocketAddress
     */
    void unregisterAllService(InetSocketAddress inetSocketAddress);
}
