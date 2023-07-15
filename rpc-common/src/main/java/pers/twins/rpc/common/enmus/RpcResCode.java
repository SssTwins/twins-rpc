package pers.twins.rpc.common.enmus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author twins
 * @date 2023-07-15 16:48:51
 * @since 1.0-SNAPSHOT
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcResCode {

    /**
     * success
     */
    SUCCESS(200, "The remote call is successful"),

    /**
     * FAIL
     */
    FAIL(500, "The remote call is fail");

    private final int code;

    private final String message;
}
