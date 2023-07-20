package pers.twins.rpc.common.remoting.service.defaultlocal;

import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.exception.RpcServiceNotFoundException;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.RpcRequest;
import pers.twins.rpc.common.remoting.loadbalancer.LoadBalancer;
import pers.twins.rpc.common.remoting.loadbalancer.RandomLoadBalancer;
import pers.twins.rpc.common.remoting.service.RpcService;
import pers.twins.rpc.common.remoting.service.ServiceProvider;
import pers.twins.rpc.common.remoting.service.ServiceDiscovery;
import pers.twins.rpc.common.util.CollectionUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author twins
 * @date 2023-7-17 19:23:56
 */
@Slf4j
public class LocalServiceProvider implements ServiceProvider, ServiceDiscovery {

    /**
     * 作为服务注册方使用的容器
     */
    private final Map<String, Object> serviceMap;

    /**
     * 作为服务调用方使用的容器
     */
    private final Map<String, List<String>> remoteServiceMap;

    private final LoadBalancer loadBalancer;

    public LocalServiceProvider() {
        serviceMap = new ConcurrentHashMap<>();
        remoteServiceMap = new ConcurrentHashMap<>();
        loadBalancer = SingletonFactory.getInstance(RandomLoadBalancer.class);
    }

    public LocalServiceProvider(LoadBalancer loadBalancer) {
        serviceMap = new ConcurrentHashMap<>();
        remoteServiceMap = new ConcurrentHashMap<>();
        this.loadBalancer = loadBalancer;
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (Objects.isNull(service)) {
            throw new RpcServiceNotFoundException(rpcServiceName);
        }
        return service;
    }

    @Override
    public void publishService(RpcService rpcService, int serverPort) {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
            return;
        }
        final String rpcServiceName = rpcService.getRpcServiceName();
        if (serviceMap.containsKey(rpcServiceName)) {
            return;
        }
        final List<String> remoteServiceUrlList = remoteServiceMap.getOrDefault(rpcServiceName, new ArrayList<>());
        remoteServiceUrlList.add(host + ":" + serverPort);
        remoteServiceMap.put(rpcServiceName, remoteServiceUrlList);
        serviceMap.put(rpcServiceName, rpcService.getService());
        log.info("Add service: {} and interfaces:{}", rpcServiceName, rpcService.getService().getClass().getInterfaces());
    }

    @Override
    public InetSocketAddress lookupToInvoke(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        final List<String> serviceUrlList = remoteServiceMap.get(rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcServiceNotFoundException(rpcServiceName);
        }
        String targetServiceUrl = loadBalancer.select(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        return serviceUrlParseToAddr(targetServiceUrl);
    }

}
