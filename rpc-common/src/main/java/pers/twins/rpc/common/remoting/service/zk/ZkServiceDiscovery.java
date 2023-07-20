package pers.twins.rpc.common.remoting.service.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import pers.twins.rpc.common.exception.RpcServiceNotFoundException;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.RpcRequest;
import pers.twins.rpc.common.remoting.loadbalancer.LoadBalancer;
import pers.twins.rpc.common.remoting.loadbalancer.RandomLoadBalancer;
import pers.twins.rpc.common.remoting.service.ServiceDiscovery;
import pers.twins.rpc.common.util.CollectionUtil;
import pers.twins.rpc.common.util.CuratorUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author twins
 * @date 2023-07-20 19:30:34
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalancer loadBalancer;

    public ZkServiceDiscovery() {
        this.loadBalancer = SingletonFactory.getInstance(RandomLoadBalancer.class);
    }


    @Override
    public InetSocketAddress lookupToInvoke(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        List<String> serviceUrlList = CuratorUtil.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcServiceNotFoundException(rpcServiceName);
        }
        String targetServiceUrl = loadBalancer.select(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        return serviceUrlParseToAddr(targetServiceUrl);
    }
}
