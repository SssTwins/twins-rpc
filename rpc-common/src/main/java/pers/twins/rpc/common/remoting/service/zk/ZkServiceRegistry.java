package pers.twins.rpc.common.remoting.service.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import pers.twins.rpc.common.remoting.service.ServiceRegistry;
import pers.twins.rpc.common.util.CuratorUtil;

import java.net.InetSocketAddress;

/**
 * @author twins
 * @date 2023-07-20 19:09:53
 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String serviceParentPath = CuratorUtil.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        String serviceAddrPath = serviceParentPath + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        CuratorUtil.createPersistentNode(zkClient, serviceParentPath);
        CuratorUtil.createEphemeralNode(zkClient, serviceAddrPath);
    }

}
