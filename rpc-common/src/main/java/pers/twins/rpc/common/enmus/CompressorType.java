package pers.twins.rpc.common.enmus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author twins
 * @date 2023-07-18 10:52:03
 */
@AllArgsConstructor
@Getter
public enum CompressorType {


    /**
     * gzip 压缩
     */
    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressorType compressorType : CompressorType.values()) {
            if (compressorType.getCode() == code) {
                return compressorType.name;
            }
        }
        return null;
    }
}
