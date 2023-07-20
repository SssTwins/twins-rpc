package pers.twins.rpc.common.remoting.service.zk;

import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.exception.RpcServiceNotFoundException;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.service.RpcService;
import pers.twins.rpc.common.remoting.service.ServiceProvider;
import pers.twins.rpc.common.remoting.service.ServiceRegistry;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author twins
 * @date 2023-07-20 19:42:29
 */
@Slf4j
public class ZkServiceProvider implements ServiceProvider {

    private final Map<String, Object> serviceMap;

    private final ServiceRegistry serviceRegistry;

    public ZkServiceProvider() {
        serviceMap = new ConcurrentHashMap<>();
        serviceRegistry = SingletonFactory.getInstance(ZkServiceRegistry.class);
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcServiceNotFoundException(rpcServiceName);
        }
        return service;
    }

    @Override
    public void publishService(RpcService rpcService, int serverPort) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            serviceRegistry.registerService(rpcService.getRpcServiceName(), new InetSocketAddress(host, serverPort));
            serviceMap.put(rpcService.getRpcServiceName(), rpcService.getService());
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }
}
