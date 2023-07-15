package pers.twins.rpc.common.compress;

/**
 * @author twins
 * @date 2023-07-15 18:21:06
 */
public interface Compressor {

    /**
     * compress
     * @param bytes An array of bytes to be compressed
     * @return byte array
     */
    byte[] compress(byte[] bytes);


    /**
     * decompress
     * @param bytes An array of bytes to be decompressed
     * @return byte array
     */
    byte[] decompress(byte[] bytes);
}
