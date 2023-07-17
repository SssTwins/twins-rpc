package pers.twins.rpc.common.remoting.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.RpcRequest;
import pers.twins.rpc.common.remoting.service.ServiceDiscovery;
import pers.twins.rpc.common.remoting.service.defaultlocal.LocalServiceProvider;
import pers.twins.rpc.common.remoting.transport.codec.NettyDecoder;
import pers.twins.rpc.common.remoting.transport.codec.NettyRpcMsgEncoder;

import java.util.concurrent.TimeUnit;

/**
 * @author twins
 * @date 2023-07-17 16:42:18
 */

public class NettyClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyClient() {
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        serviceDiscovery = SingletonFactory.getInstance(LocalServiceProvider.class);
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                //  The timeout period of the connection.
                //  If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        // If no data is sent to the server within 15 seconds, a heartbeat request is sent
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new NettyRpcMsgEncoder());
                        p.addLast(new NettyDecoder());
                        p.addLast(new NettyClientHandler(channelProvider));
                    }
                });
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        // todo sendRequest
        return null;
    }
}
