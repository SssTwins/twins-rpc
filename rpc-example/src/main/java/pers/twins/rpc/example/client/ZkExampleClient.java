package pers.twins.rpc.example.client;

import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.proxy.RpcClientProxy;
import pers.twins.rpc.common.remoting.service.RpcService;
import pers.twins.rpc.common.remoting.service.zk.ZkServiceDiscovery;
import pers.twins.rpc.common.remoting.transport.client.NettyClient;
import pers.twins.rpc.example.service.HelloService;

/**
 * @author twins
 * @date 2023-07-20 21:32:42
 */
@Slf4j
public class ZkExampleClient {

    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient(SingletonFactory.getInstance(ZkServiceDiscovery.class));
        RpcService rpcService = RpcService.builder()
                .group("test")
                .version("1")
                .build();
        final RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient, rpcService);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        final String hello = helloService.hello("twins");
        log.info(hello);
    }
}
