package com.konsung.netty;

import android.util.Log;

import com.konsung.R;
import com.konsung.util.UIUtils;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author ouyangfan
 * @version 0.0.1
 *          2015-01-13 10:08
 *          目前康尚程序尚未使用到此客户端类
 */
public class EchoClient {
    private final String host;      // 服务器地址
    private final int port;         // 服务器端口号
    protected Channel channel; //通道

    /**
     * 客户端构造器
     * @param host ip
     * @param port 端口
     */
    public EchoClient(String host, int port) {
        System.out.println("-------echoClient-----");
        this.host = host;
        this.port = port;
    }

    /**
     * 客户端开始连接
     * @throws Exception netty异常
     */
    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 客户端引导器
            Bootstrap bootstrap = new Bootstrap();
            // 指定事件组、客户端通道、远程服务端地址、业务处理器，发起连接
            bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(new
                    InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //需要发送数据协议,EchoClientHandler处理接收到的数据
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            //发起异步连接操作
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channel = channelFuture.channel();
            //向服务器发送数据
            sendMessage(UIUtils.getContext().getString(R.string.netty_send_msg));
//            // 连接到服务端，sync()阻塞直到连接过程结束
//            ChannelFuture future = bootstrap.connect().sync();
//            // 等待通道关闭
//            future.channel().closeFuture().sync();
        } finally {
            Log.d("Test", "EchoClient:finally");
            // 关闭引导器并释放资源，包括线程池
            group.shutdownGracefully().sync();
        }
    }

    /**
     * 连接成功后发送消息
     * @param msg 消息数据
     */
    public void sendMessage(String msg) {
        //连接成功后，通过Channel提供的接口进行IO操作
        try {
            if (channel != null && channel.isOpen()) {
                channel.writeAndFlush(msg).sync(); //发送后会在EchoClientHandler的onread回调？
            } else {
                throw new Exception("channel is null | closed");
            }
        } catch (Exception e) {
//            sendReconnectMessage();
            e.printStackTrace();
        }
    }
}
