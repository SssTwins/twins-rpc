package pers.twins.rpc.common.remoting.service.registry;

import pers.twins.rpc.common.exception.RpcServiceNotFoundException;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author twins
 * @date 2023-07-16 22:38:16
 */
public class LocalServiceRegistry implements ServiceRegistry {

    private final Map<String, InetSocketAddress> serviceMap;

    public LocalServiceRegistry() {
        serviceMap = new ConcurrentHashMap<>(16);
    }

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        serviceMap.put(rpcServiceName, inetSocketAddress);
    }

    public InetSocketAddress getServiceByName(String rpcServiceName) {
        return Optional.ofNullable(serviceMap.get(rpcServiceName))
                .orElseThrow(() -> new RpcServiceNotFoundException(rpcServiceName));
    }
}
