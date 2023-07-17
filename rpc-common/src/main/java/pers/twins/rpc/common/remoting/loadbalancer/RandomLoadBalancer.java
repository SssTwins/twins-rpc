package pers.twins.rpc.common.remoting.loadbalancer;

import pers.twins.rpc.common.remoting.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * @author twins
 * @date 2023-07-17 15:03:51
 */
public class RandomLoadBalancer implements LoadBalancer{

    @Override
    public String select(List<String> serviceUrlList, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceUrlList.get(random.nextInt(serviceUrlList.size()));
    }
}
