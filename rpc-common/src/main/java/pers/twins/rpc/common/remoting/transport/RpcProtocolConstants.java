package pers.twins.rpc.common.remoting.transport;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author twins
 * @date 2023-07-17 20:34:18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcProtocolConstants {


    public static final byte[] MAGIC_NUMBER = {(byte) 't', (byte) 'w', (byte) 'i', (byte) 'n'};

    public static final byte VERSION = 1;

    public static final byte TYPE_REQUEST = 1;

    public static final byte TYPE_RESPONSE = 2;

    /**
     * ping type
     */
    public static final byte TYPE_HEARTBEAT_REQUEST = 3;

    /**
     * pong type
     */
    public static final byte TYPE_HEARTBEAT_RESPONSE = 4;

    public static final int HEAD_LENGTH = 16;

    public static final String PING = "ping";

    public static final String PONG = "pong";

    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}
