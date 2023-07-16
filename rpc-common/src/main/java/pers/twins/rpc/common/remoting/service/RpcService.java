package pers.twins.rpc.common.remoting.service;

import lombok.*;

/**
 * the service wrapper class used by rpc
 *
 * @author twins
 * @date 2023-07-16 22:03:04
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcService {

    /**
     * service version
     */
    private String version = "";

    /**
     * when the interface has multiple implementation classes, distinguish by group
     */
    private String group = "";

    /**
     * target service
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
