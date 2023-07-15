package pers.twins.rpc.common.serialization;

/**
 * Serializer interface
 *
 * @author twins
 * @date 2023-07-15 15:28:35
 * @since 1.0-SNAPSHOT
 */
public interface Serializer {

    /**
     * serialize
     *
     * @param obj object to serialize
     * @return byte array
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes The serialized byte array
     * @param clazz target class
     * @param <T>   class type
     * @return Deserialized object
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
