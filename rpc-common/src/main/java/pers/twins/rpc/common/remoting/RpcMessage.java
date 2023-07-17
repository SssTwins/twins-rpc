package pers.twins.rpc.common.remoting;

import lombok.*;

/**
 * @author twins
 * @date 2023-07-17 16:26:47
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    /**
     * rpc message type
     */
    private byte messageType;

    /**
     * serialization type
     */
    private byte codec;

    /**
     * compress type
     */
    private byte compress;

    /**
     * request id
     */
    private int requestId;

    /**
     * request data
     */
    private Object data;

}
