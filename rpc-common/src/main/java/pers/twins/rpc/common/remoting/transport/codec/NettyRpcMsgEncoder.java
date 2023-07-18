package pers.twins.rpc.common.remoting.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.compress.Compressor;
import pers.twins.rpc.common.compress.GzipCompressor;
import pers.twins.rpc.common.enmus.CompressorType;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.RpcMessage;
import pers.twins.rpc.common.remoting.transport.RpcProtocolConstants;
import pers.twins.rpc.common.serialization.KryoSerializer;
import pers.twins.rpc.common.serialization.Serializer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author twins
 * @date 2023-07-17 17:39:41
 */
@Slf4j
public class NettyRpcMsgEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) {
        try {
            byteBuf.writeBytes(RpcProtocolConstants.MAGIC_NUMBER);
            byteBuf.writeByte(RpcProtocolConstants.VERSION);
            // 流出4字节记录数据总长度
            byteBuf.writerIndex(byteBuf.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            byteBuf.writeByte(messageType);
            byteBuf.writeByte(rpcMessage.getCodec());
            byteBuf.writeByte(CompressorType.GZIP.getCode());
            byteBuf.writeInt(ATOMIC_INTEGER.getAndIncrement());
            byte[] bodyBytes = null;
            // 计算消息总长
            int fullLength = RpcProtocolConstants.HEAD_LENGTH;
            // 非心跳消息的长度是 请求头长度+请求体长度
            if (messageType != RpcProtocolConstants.TYPE_HEARTBEAT_REQUEST
                    && messageType != RpcProtocolConstants.TYPE_HEARTBEAT_RESPONSE) {
                // 暂时使用默认的KryoSerializer
                Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                // 暂时使用默认的gzip压缩数据
                Compressor compress = SingletonFactory.getInstance(GzipCompressor.class);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }
            if (bodyBytes != null) {
                byteBuf.writeBytes(bodyBytes);
            }
            // 在上文留下的空位写入数据长度
            int writeIndex = byteBuf.writerIndex();
            byteBuf.writerIndex(writeIndex - fullLength + RpcProtocolConstants.MAGIC_NUMBER.length + 1);
            byteBuf.writeInt(fullLength);
            byteBuf.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("Encode request error!", e);
        }
    }
}
