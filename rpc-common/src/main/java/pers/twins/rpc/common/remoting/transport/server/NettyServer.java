package pers.twins.rpc.common.remoting.transport.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.service.RpcService;
import pers.twins.rpc.common.remoting.service.ServiceProvider;
import pers.twins.rpc.common.remoting.service.defaultlocal.LocalServiceProvider;
import pers.twins.rpc.common.remoting.transport.codec.NettyDecoder;
import pers.twins.rpc.common.remoting.transport.codec.NettyRpcMsgEncoder;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @author twins
 * @date 2023-07-16 19:12:46
 */
@Slf4j
public class NettyServer {

    public static final int DEFAULT_PORT = 9091;

    private final ServiceProvider serviceProvider;

    public NettyServer() {
        this(SingletonFactory.getInstance(LocalServiceProvider.class));
    }

    public NettyServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), DEFAULT_PORT);
                serviceProvider.unregisterAllService(inetSocketAddress);
            } catch (UnknownHostException e) {
                log.warn("occur exception when getHostAddress", e);
            }
        }));
    }

    @SneakyThrows(UnknownHostException.class)
    public void start() {
        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final DefaultEventExecutorGroup executorGroup = getExecutorGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 当客户端第一次进行请求的时候才会进行初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 30 秒之内没有收到客户端请求的话就关闭连接
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new NettyRpcMsgEncoder());
                            p.addLast(new NettyDecoder());
                            p.addLast(executorGroup, new NettyServerHandler(serviceProvider));
                        }
                    });
            // 绑定端口，同步等待绑定成功
            ChannelFuture f = serverBootstrap.bind(host, DEFAULT_PORT).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("occur exception when start server:", e);
            Thread.currentThread().interrupt();
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            executorGroup.shutdownGracefully();
        }
    }

    private DefaultEventExecutorGroup getExecutorGroup() {
        return new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                new ThreadFactoryBuilder()
                        .setNameFormat("rpc-service-handler-%d")
                        .setDaemon(false)
                        .build()
        );
    }

    /**
     * 发布服务
     *
     * @param rpcService rpc服务配置
     */
    public void registerService(RpcService rpcService) {
        serviceProvider.publishService(rpcService, DEFAULT_PORT);
    }
}
