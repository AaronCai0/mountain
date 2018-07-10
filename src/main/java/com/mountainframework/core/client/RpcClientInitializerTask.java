package com.mountainframework.core.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

import com.mountainframework.rpc.serialize.RpcSerializeProtocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Rpc客户端初始化任务类，多线程任务
 * 
 * @author yafeng.cai {@link}https://github.com/AaronCai0
 * @date 2018年6月30日
 * @since 1.0
 */
public class RpcClientInitializerTask implements Callable<Boolean> {

	private InetSocketAddress inetSocketAddress;
	private EventLoopGroup eventLoopGroup;
	private final RpcSerializeProtocol PROTOCOL = RpcSerializeProtocol.HESSIAN;

	public RpcClientInitializerTask(InetSocketAddress inetSocketAddress, EventLoopGroup eventLoopGroup) {
		this.inetSocketAddress = inetSocketAddress;
		this.eventLoopGroup = eventLoopGroup;
	}

	@Override
	public Boolean call() throws Exception {
		Bootstrap bootstrap = new Bootstrap();
		ChannelFuture channelFuture = bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true).handler(RpcClientChannelInitializer.create(PROTOCOL))
				.connect(inetSocketAddress);

		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					RpcClientChannelHandler rpcClientHanlder = future.channel().pipeline()
							.get(RpcClientChannelHandler.class);
					RpcClientLoader.getInstance().setRpcClientHandler(rpcClientHanlder);
				}
			}
		});
		return Boolean.TRUE;
	}

}