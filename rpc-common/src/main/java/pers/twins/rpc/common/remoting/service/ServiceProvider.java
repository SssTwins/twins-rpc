package pers.twins.rpc.common.remoting.service;

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
}
