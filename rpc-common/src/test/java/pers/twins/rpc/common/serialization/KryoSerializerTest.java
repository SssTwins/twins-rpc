package pers.twins.rpc.common.serialization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KryoSerializerTest {

    @Test
    void kryoSerializerTest() {
        var content = "KryoSerializerTest";
        Serializer kryoSerializer = new KryoSerializer();
        byte[] bytes = kryoSerializer.serialize(content);
        String contentDeserialize = kryoSerializer.deserialize(bytes, String.class);
        Assertions.assertEquals(contentDeserialize, content);
    }
}
