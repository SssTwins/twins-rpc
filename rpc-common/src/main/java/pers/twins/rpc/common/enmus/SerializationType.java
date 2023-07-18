package pers.twins.rpc.common.enmus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author twins
 * @date 2023-07-18 10:57:13
 */

@AllArgsConstructor
@Getter
public enum SerializationType {

    /**
     *
     */
    KRYO((byte) 0x01, "kryo");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializationType serializationType : SerializationType.values()) {
            if (serializationType.getCode() == code) {
                return serializationType.name;
            }
        }
        return null;
    }
}
