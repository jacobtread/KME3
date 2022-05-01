package com.jacobtread.kme.servers

import com.jacobtread.kme.Config
import com.jacobtread.kme.LOGGER
import com.jacobtread.kme.blaze.PacketCommand
import com.jacobtread.kme.blaze.PacketComponent
import com.jacobtread.kme.blaze.PacketDecoder
import com.jacobtread.kme.blaze.RawPacket
import com.jacobtread.kme.blaze.builder.Packet
import com.jacobtread.kme.utils.NULL_CHAR
import com.jacobtread.kme.utils.createContext
import com.jacobtread.kme.utils.customThreadFactory
import com.jacobtread.kme.utils.getIp
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.io.IOException

/**
 * startRedirector Starts the Redirector server in a new thread
 *
 * @param config The server configuration
 */
fun startRedirector(config: Config) {
    Thread {
        LOGGER.info("Starting Redirector Server (${config.host}:${config.ports.redirector})")
        LOGGER.info("===== Redirection Configuration =====")
        LOGGER.info("Host: ${config.redirectorPacket.host}")
        LOGGER.info("IP:   ${config.redirectorPacket.ip}")
        LOGGER.info("Port: ${config.redirectorPacket.port}")
        LOGGER.info("=====================================")
        val context = createContext() // Create a SSL context
        val bossGroup = NioEventLoopGroup(customThreadFactory("Redirector Boss #{ID}"))
        val workerGroup = NioEventLoopGroup(customThreadFactory("Redirector Worker #{ID}"))
        val bootstrap = ServerBootstrap() // Create a server bootstrap
        try {
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<Channel>() {
                    override fun initChannel(ch: Channel) {
                        ch.pipeline()
                            // Add handler for decoding SSLv3
                            .addLast(context.newHandler(ch.alloc()))
                            // Add handler for decoding packet
                            .addLast(PacketDecoder())
                            // Add handler for processing packets
                            .addLast(RedirectClient(config.redirectorPacket))
                    }
                })
                // Bind the server to the host and port
                .bind(config.host, config.ports.redirector)
                // Wait for the channel to bind
                .sync()
                .channel()
                // Get the closing future
                .closeFuture()
                // Wait for the closing
                .sync()
        } catch (e: IOException) {
            LOGGER.error("Exception in redirector server", e)
        }
    }.apply {
        // Name the redirector thread
        name = "Redirector"
        // Close this thread when the JVM requests close
        isDaemon = true
        // Start the redirector thread
        start()
    }
}

/**
 * RedirectClient Creates a client that handles the redirect handshake
 * to direct the client to the desired main server
 *
 * @property config The config for redirection packets
 * @constructor Create empty RedirectClient
 */
class RedirectClient(private val config: Config.RedirectorPacket) : SimpleChannelInboundHandler<RawPacket>() {

    /**
     * channelRead0 Handles incoming RawPackets and sends back a redirect packet
     * when the REDIRECTOR + REQUEST_REDIRECT packet is received
     *
     * @param ctx
     * @param msg
     */
    override fun channelRead0(ctx: ChannelHandlerContext, msg: RawPacket) {
        if (msg.component == PacketComponent.REDIRECTOR
            && msg.command == PacketCommand.REQUEST_REDIRECT
        ) {
            val channel = ctx.channel()
            val remoteAddress = channel.remoteAddress()
            LOGGER.info("Sending redirection to client -> $remoteAddress")
            // Create a packet to redirect the client to the target server
            val packet = Packet(msg.component, msg.command, 0, 0x1000) {
                Union(
                    "ADDR", config.addr,
                    StructInline("VALU") {
                        Text("HOST", config.host)
                        VarInt("IP$NULL_CHAR$NULL_CHAR", config.ip.getIp())
                        VarInt("PORT", config.port)
                    }
                )
                VarInt("SECU", config.secu)
                VarInt("XDNS", config.xdns)
            }
            // Write the packet, flush and then close the channel
            channel.writeAndFlush(packet)
            channel.close()
            LOGGER.info("Terminating connection to $remoteAddress (Finished redirect)")
        }
    }
}