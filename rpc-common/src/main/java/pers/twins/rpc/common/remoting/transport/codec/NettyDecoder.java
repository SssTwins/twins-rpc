package pers.twins.rpc.common.remoting.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.compress.GzipCompressor;
import pers.twins.rpc.common.exception.RpcByteVersionException;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.RpcMessage;
import pers.twins.rpc.common.remoting.RpcRequest;
import pers.twins.rpc.common.remoting.RpcResponse;
import pers.twins.rpc.common.remoting.transport.RpcProtocolConstants;
import pers.twins.rpc.common.serialization.KryoSerializer;
import pers.twins.rpc.common.serialization.Serializer;

import java.util.Arrays;

/**
 * 自定义netty解码器
 *
 * @author twins
 * @date 2023-07-17 17:38:42
 */
@Slf4j
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    public NettyDecoder() {
        // 自定义的最大帧长
        // 长度偏移为魔数长度4+版本长度1
        // 长度的字节数为4
        // 数据长度修正9个字节为长度偏移+长度
        // 0表示接收header+body的所有数据，只想接收body数据，需要跳过header的字节数16
        this(RpcProtocolConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    public NettyDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf frame && (frame.readableBytes() >= RpcProtocolConstants.HEAD_LENGTH)) {
            try {
                return doDecode(frame);
            } catch (Exception e) {
                log.error("Decode frame error!", e);
                throw e;
            } finally {
                frame.release();
            }
        }
        return decoded;
    }

    /**
     * 实际的数据解析方法
     *
     * @param in ByteBuf
     * @return 解析完成的rpc消息
     */
    private Object doDecode(ByteBuf in) {
        // 严格按照顺序解析ByteBuf
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        //byte compressType =
        in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType)
                .build();
        if (messageType == RpcProtocolConstants.TYPE_HEARTBEAT_REQUEST) {
            rpcMessage.setData(RpcProtocolConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcProtocolConstants.TYPE_HEARTBEAT_RESPONSE) {
            rpcMessage.setData(RpcProtocolConstants.PONG);
            return rpcMessage;
        }
        int bodyLength = fullLength - RpcProtocolConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            // 暂时使用默认的gzip解压数据
            bs = SingletonFactory.getInstance(GzipCompressor.class).decompress(bs);
            // 暂时使用默认的KryoSerializer
            Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
            if (messageType == RpcProtocolConstants.TYPE_REQUEST) {
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmpValue);
            } else {
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;

    }

    /**
     * 检查rpc消息版本
     *
     * @param in ByteBuf
     */
    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != RpcProtocolConstants.VERSION) {
            throw new RpcByteVersionException("version isn't compatible" + version);
        }
    }

    /**
     * 读取前四个字节检查魔数特征签名
     *
     * @param in ByteBuf
     */
    private void checkMagicNumber(ByteBuf in) {
        int len = RpcProtocolConstants.MAGIC_NUMBER.length;
        byte[] mn = new byte[len];
        in.readBytes(mn);
        for (int i = 0; i < len; i++) {
            if (mn[i] != RpcProtocolConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(mn));
            }
        }
    }
}
