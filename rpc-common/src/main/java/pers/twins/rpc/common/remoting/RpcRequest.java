package pers.twins.rpc.common.remoting;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author twins
 * @date 2023-07-15 16:12:12
 * @since 1.0-SNAPSHOT
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -7013285216065354753L;

    private String requestId;

    private String interfaceName;

    private String methodName;

    private Object[] params;

    private Class<?>[] paramTypes;

    private String version;

    private String group;
}
