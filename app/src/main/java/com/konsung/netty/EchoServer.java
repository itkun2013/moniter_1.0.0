package com.konsung.netty;

import android.os.Handler;

import com.konsung.activity.MyApplication;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author ouyangfan
 * @version 0.0.1
 *          使用netty 框架的服务器类  本地
 */
public class EchoServer {
    // 端口号.final一旦赋值不能更改
    private final int port;
    private Handler handler;
    // 单例模式
    private static EchoServer echoServerInstance;

    /**
     * 构造器，带端口号和handler数据处理
     * @param port 端口
     * @param handler 处理器
     */
    private EchoServer(int port, Handler handler) {
        this.port = port;
        this.handler = handler;
    }

    /**
     * @param port 端口号
     * @param handler 数据处理
     * @return EchoServer类实例
     */
    public static EchoServer getEchoServerInstance(int port, Handler handler) {
        if (echoServerInstance == null) {
            echoServerInstance = new EchoServer(port, handler);
        }
        return echoServerInstance;
    }

    /**
     * 启动方法
     * @throws Exception 异常
     */
    public void start() throws Exception {
        //处理NIO操作的多线程事件循环器，netty默认2个线程池，处理channel消息
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
//            引导程序，作为管理Channel的一个辅助类,创建Channel并发起请求
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    // 当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
                    .option(ChannelOption.SO_BACKLOG, 1024).localAddress(new
                    InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // 进行数据分包处理,这里的参数是根据具体协议来指定的
                            channel.pipeline().addLast("decoder", new
                                    LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer
                                    .MAX_VALUE, 1, 2, -3, 0, false));
                            channel.pipeline().addLast(new EchoServerDecoder(handler));
                            channel.pipeline().addLast("encoder", new EchoServerEncoder());
                        }
                    });
            ChannelFuture future = bootstrap.bind(6613).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
        } finally {
            //如果发生异常，走这里
            MyApplication.application.startAppDevice();
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}
