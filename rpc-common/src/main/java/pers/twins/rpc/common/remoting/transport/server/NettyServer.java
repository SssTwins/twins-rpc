package pers.twins.rpc.common.remoting.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.service.defaultlocal.LocalServiceProvider;
import pers.twins.rpc.common.remoting.service.ServiceProvider;

import java.net.InetAddress;

/**
 * @author twins
 * @date 2023-07-16 11:12:46
 */
@Slf4j
public class NettyServer {

    public static final int DEFAULT_PORT = 9091;

    private final ServiceProvider serviceProvider;

    public NettyServer() {
        serviceProvider = SingletonFactory.getInstance(LocalServiceProvider.class);
    }

    @SneakyThrows
    public void start() {
        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup);
    }
}
