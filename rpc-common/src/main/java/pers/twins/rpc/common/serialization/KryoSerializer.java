package pers.twins.rpc.common.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.exception.SerializationException;
import pers.twins.rpc.common.remoting.RpcRequest;
import pers.twins.rpc.common.remoting.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * @author twins
 * @date 2023-07-15 17:45:04
 */
@Slf4j
public class KryoSerializer implements Serializer {

    /**
     * Because Kryo is not thread safe and constructing and configuring a Kryo instance is relatively expensive,
     * in a multithreaded environment ThreadLocal or pooling might be considered.
     */
    private static final Pool<Kryo> KRYO_POOL = new Pool<>(true, false, 8) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.register(RpcRequest.class);
            kryo.register(RpcResponse.class);
            kryo.register(java.lang.Class[].class);
            kryo.register(Class.class);
            kryo.register(java.lang.Object[].class);
            return kryo;
        }
    };

    @Override
    public byte[] serialize(Object obj) {
        Kryo kryo = null;
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            kryo = KRYO_POOL.obtain();
            kryo.writeObject(output, obj);
            KRYO_POOL.free(kryo);
            return output.toBytes();
        } catch (Exception e) {
            if (Objects.nonNull(kryo)) {
                KRYO_POOL.free(kryo);
            }
            throw new SerializationException("Serialization failed");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            kryo = KRYO_POOL.obtain();
            Object object = kryo.readObject(input, clazz);
            KRYO_POOL.free(kryo);
            return clazz.cast(object);
        } catch (Exception e) {
            if (Objects.nonNull(kryo)) {
                KRYO_POOL.free(kryo);
            }
            throw new SerializationException("Deserialization failed");
        }
    }
}
