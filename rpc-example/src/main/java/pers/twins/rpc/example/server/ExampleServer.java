package pers.twins.rpc.example.server;

import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.remoting.service.RpcService;
import pers.twins.rpc.common.remoting.transport.server.NettyServer;
import pers.twins.rpc.example.service.HelloService;
import pers.twins.rpc.example.service.HelloServiceImpl;

/**
 * @author twins
 * @date 2023-07-15 16:10:35
 */
@Slf4j
public class ExampleServer {

    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
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