package pers.twins.rpc.common.remoting;

import lombok.*;
import pers.twins.rpc.common.enmus.RpcResCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author twins
 * @date 2023-07-15 16:36:40
 * @since 1.0-SNAPSHOT
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
@Setter
public class RpcResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -5957212031057646557L;

    private String requestId;

    private Integer code;

    private String message;

    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResCode.SUCCESS.getCode());
        response.setMessage(RpcResCode.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (Objects.nonNull(data)) {
            response.setData(data);
        }
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcResCode resCode) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(resCode.getCode());
        response.setMessage(resCode.getMessage());
        return response;
    }
}
