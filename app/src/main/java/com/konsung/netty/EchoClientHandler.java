package com.konsung.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @author ouyangfan
 * @version 0.0.1
 * 2015-01-13 11:13
 * 从客户端发往服务器的报文进行处理，用来执行解码、读取客户端数据、进行业务处理
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //channelActive()方法将会在连接被建立并且准备进行通信时被调用。
        super.channelActive(ctx);
//        // 连接建立，向服务端发送数据 Channel是活跃状态（连接到某个远端），可以收发数据
        ctx.write(Unpooled.copiedBuffer("hello Netty!", CharsetUtil.UTF_8));
//        // 注意：需要调用flush将数据发送到服务端
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf)
            throws Exception {
        // 每当从客户端收到新的数据时，这个方法会在收到消息时被调用
//        byte[] req = new byte[byteBuf.readableBytes()];
//        byteBuf.readBytes(req);
//        String body = new String(req, "UTF-8");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 打印异常并关闭通道
        cause.printStackTrace();
        ctx.close();
    }
}
