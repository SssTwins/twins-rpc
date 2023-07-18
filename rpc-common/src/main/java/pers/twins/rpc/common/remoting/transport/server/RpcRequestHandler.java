package pers.twins.rpc.common.remoting.transport.server;

import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.exception.RpcException;
import pers.twins.rpc.common.remoting.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author twins
 * @date 2023-07-17 16:20:31
 */
@Slf4j
public class RpcRequestHandler {

    /**
     * 处理rpcRequest，调用相应的方法并返回
     */
    public Object handle(RpcRequest rpcRequest, Object service) {
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 获取方法执行结果
     *
     * @param rpcRequest 客户端 request
     * @param service    需要调用的service
     * @return 目标方法的执行结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParams());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RpcException(e);
        }
        return result;
    }
}
