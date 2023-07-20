package pers.twins.rpc.common.remoting.service;

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
     * 解析服务地址生成InetSocketAddress对象
     *
     * @param targetServiceUrl 目标服务地址
     * @return InetSocketAddress
     */
    default InetSocketAddress serviceUrlParseToAddr(String targetServiceUrl) {
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }

    /**
     * gets a service by rpcRequest properties address to invoke
     *
     * @param rpcRequest rpcRequest
     * @return service address
     */
    InetSocketAddress lookupToInvoke(RpcRequest rpcRequest);
}
