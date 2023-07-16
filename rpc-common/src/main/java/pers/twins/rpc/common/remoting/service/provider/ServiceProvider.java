package pers.twins.rpc.common.remoting.service.provider;

import pers.twins.rpc.common.remoting.service.RpcService;

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
     */
    void publishService(RpcService rpcService);
}
