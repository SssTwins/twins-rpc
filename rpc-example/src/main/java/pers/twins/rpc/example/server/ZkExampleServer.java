package pers.twins.rpc.example.server;

import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.service.RpcService;
import pers.twins.rpc.common.remoting.service.zk.ZkServiceProvider;
import pers.twins.rpc.common.remoting.transport.server.NettyServer;
import pers.twins.rpc.example.service.HelloService;
import pers.twins.rpc.example.service.HelloServiceImpl;

/**
 * @author twins
 * @date 2023-07-20 21:32:35
 */
@Slf4j
public class ZkExampleServer {

    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer(SingletonFactory.getInstance(ZkServiceProvider.class));
        HelloService helloService = new HelloServiceImpl();
        RpcService rpcService = RpcService.builder()
                .group("test")
                .version("1")
                .service(helloService)
                .build();
        nettyServer.registerService(rpcService);
        nettyServer.start();
    }
}