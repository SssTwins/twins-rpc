package pers.twins.rpc.common.remoting.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.enmus.CompressorType;
import pers.twins.rpc.common.enmus.SerializationType;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.RpcMessage;
import pers.twins.rpc.common.remoting.RpcRequest;
import pers.twins.rpc.common.remoting.RpcResponse;
import pers.twins.rpc.common.remoting.service.ServiceDiscovery;
import pers.twins.rpc.common.remoting.service.defaultlocal.LocalServiceProvider;
import pers.twins.rpc.common.remoting.transport.RpcProtocolConstants;
import pers.twins.rpc.common.remoting.transport.codec.NettyDecoder;
import pers.twins.rpc.common.remoting.transport.codec.NettyRpcMsgEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author twins
 * @date 2023-07-17 16:42:18
 */
@Slf4j
public class NettyClient implements RpcRequestTransport {

    private final EventLoopGroup eventLoopGroup;

    private final Bootstrap bootstrap;

    private final ServiceDiscovery serviceDiscovery;

    private final ChannelProvider channelProvider;

    private final UnprocessedRequestProvider unprocessedRequestProvider;

    public NettyClient() {
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        serviceDiscovery = SingletonFactory.getInstance(LocalServiceProvider.class);
        unprocessedRequestProvider = SingletonFactory.getInstance(UnprocessedRequestProvider.class);
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) {
                        ChannelPipeline p = sc.pipeline();
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new NettyRpcMsgEncoder());
                        p.addLast(new NettyDecoder());
                        p.addLast(new NettyClientHandler(unprocessedRequestProvider));
                    }
                });
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        // 预定义需要的Future返回值
        CompletableFuture<RpcResponse<Object>> res = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupToInvoke(rpcRequest);
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            unprocessedRequestProvider.put(rpcRequest.getRequestId(), res);
            RpcMessage rpcMessage = RpcMessage.builder()
                    .data(rpcRequest)
                    .codec(SerializationType.KRYO.getCode())
                    .compress(CompressorType.GZIP.getCode())
                    .messageType(RpcProtocolConstants.TYPE_REQUEST)
                    .build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    res.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return res;
    }

    /**
     * 获取一个channel，不存在则建立连接
     *
     * @param inetSocketAddress 服务地址
     * @return channel
     */
    private Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = connect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    /**
     * 连接服务获取channel
     *
     * @param inetSocketAddress 服务地址
     * @return the channel
     */
    @SneakyThrows({InterruptedException.class, ExecutionException.class})
    private Channel connect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
