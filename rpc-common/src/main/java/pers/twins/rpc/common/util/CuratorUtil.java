package pers.twins.rpc.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author twins
 * @date 2023-07-20 19:54:58
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CuratorUtil {

    private static CuratorFramework zkClient;

    public static final String ZK_REGISTER_ROOT_PATH = "/twins-rpc";

    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "172.31.134.126:2181";

    private static final int SLEEP_TIME = 1000;

    private static final int MAX_RETRIES = 3;

    private static final int MAX_CONNECTING_TIME = 30;

    static {
        // 初始化zkClient
        getZkClient();
    }

    /**
     * 双重锁校验获取唯一的ZK_CLIENT
     *
     * @return ZK_CLIENT
     */
    public static CuratorFramework getZkClient() {
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        synchronized (CuratorUtil.class) {
            if (zkClient == null || zkClient.getState() != CuratorFrameworkState.STARTED) {
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(SLEEP_TIME, MAX_RETRIES);
                zkClient = CuratorFrameworkFactory.builder()
                        .connectString(DEFAULT_ZOOKEEPER_ADDRESS)
                        .retryPolicy(retryPolicy)
                        .build();
                zkClient.start();
                try {
                    if (!zkClient.blockUntilConnected(MAX_CONNECTING_TIME, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Time out waiting to connect to ZK!");
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
        return zkClient;
    }

    /**
     * 创建持久化节点
     *
     * @param path 节点路径
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (zkClient.checkExists().forPath(path) != null) {
                log.info("The persistent node already exists. The persistent node is:[{}]", path);
            } else {
                final String s = zkClient.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(path);
                log.info("The persistent node was created successfully. The persistent node is:[{}]", s);
            }
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
        }
    }

    /**
     * 创建临时节点
     *
     * @param path 节点路径
     */
    public static void createEphemeralNode(CuratorFramework zkClient, String path) {
        try {
            if (zkClient.checkExists().forPath(path) != null) {
                log.info("The ephemeral node already exists. The ephemeral node is:[{}]", path);
            } else {
                final String s = zkClient.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(path);
                log.info("The ephemeral node was created successfully. The ephemeral node is:[{}]", s);
            }
        } catch (Exception e) {
            log.error("create ephemeral node for path [{}] fail", path);
        }
    }

    /**
     * 获取目标节点名下的子节点
     *
     * @param rpcServiceName rpc服务名称
     * @return 所有的子节点，即rpc服务的实际地址
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return result;
    }

    /**
     * 移除已注册的节点
     *
     * @param zkClient zkClient
     * @param path     节点路径
     */
    public static void removeNode(CuratorFramework zkClient, String path) {
        try {
            zkClient.delete().forPath(path);
            log.info("remove registry for path [{}] successfully", path);
        } catch (Exception e) {
            log.error("remove registry for path [{}] fail", path);
        }
    }
}
