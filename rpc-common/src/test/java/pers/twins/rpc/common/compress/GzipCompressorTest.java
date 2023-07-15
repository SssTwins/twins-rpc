package pers.twins.rpc.common.compress;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pers.twins.rpc.common.serialization.KryoSerializer;
import pers.twins.rpc.common.serialization.Serializer;

class GzipCompressorTest {


    @Test
    void testGzipCompressor() {
        Compressor gzipCompress = new GzipCompressor();
        Serializer kryoSerializer = new KryoSerializer();
        var content = "GzipCompressorTest";
        byte[] contentBytes = kryoSerializer.serialize(content);
        byte[] compressContentBytes = gzipCompress.compress(contentBytes);
        byte[] decompressContentBytes = gzipCompress.decompress(compressContentBytes);
        Assertions.assertArrayEquals(contentBytes, decompressContentBytes);
    }

}
