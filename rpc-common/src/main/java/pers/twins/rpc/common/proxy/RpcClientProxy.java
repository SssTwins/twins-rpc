package pers.twins.rpc.common.proxy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.enmus.RpcResCode;
import pers.twins.rpc.common.exception.RpcException;
import pers.twins.rpc.common.remoting.RpcRequest;
import pers.twins.rpc.common.remoting.RpcResponse;
import pers.twins.rpc.common.remoting.service.RpcService;
import pers.twins.rpc.common.remoting.transport.client.NettyClient;
import pers.twins.rpc.common.remoting.transport.client.RpcRequestTransport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * rpc客户端服务请求代理类，使用jdk动态代理
 *
 * @author twins
 * @date 2023-07-18 17:06:42
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    /**
     * 请求发送器
     */
    private final RpcRequestTransport rpcRequestTransport;

    private final RpcService rpcService;

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, RpcService rpcService) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcService = rpcService;
    }

    /**
     * 获取代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * 代理类实际调用的方法
     *
     * @return 方法执行结果
     */
    @SneakyThrows({InterruptedException.class, ExecutionException.class})
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .params(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcService.getGroup())
                .version(rpcService.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof NettyClient) {
            final Object res = rpcRequestTransport.sendRequest(rpcRequest);
            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) res;
            rpcResponse = completableFuture.get();
        }
        this.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException("服务调用失败: " + rpcRequest.getInterfaceName());
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException("返回结果错误！请求和返回的相应不匹配: " + rpcRequest.getInterfaceName());
        }
        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResCode.SUCCESS.getCode())) {
            throw new RpcException("服务调用失败::" + rpcRequest.getInterfaceName());
        }
    }
}
