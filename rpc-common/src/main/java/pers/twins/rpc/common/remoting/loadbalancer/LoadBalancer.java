package pers.twins.rpc.common.remoting.loadbalancer;

import pers.twins.rpc.common.remoting.RpcRequest;

import java.util.List;

/**
 * 负载均衡器
 *
 * @author twins
 * @date 2023-07-17 15:03:04
 */
public interface LoadBalancer {

    /**
     * Choose one from the list of existing service addresses list
     *
     * @param serviceUrlList Service address list
     * @param rpcRequest
     * @return target service address
     */
    String select(List<String> serviceUrlList, RpcRequest rpcRequest);
}
