package com.jacobtread.kme.blaze

import io.netty.channel.Channel

const val INCOMING = 0x0000 // This packet is coming from the client
const val RESPONSE = 0x1000 // This packet is a response to an existing packet
const val UNIQUE = 0x2000 // This packet is not responding to a packet
const val ERROR = 0x3000 // This packet contains an error

const val NO_ERROR = 0

@Suppress("NOTHING_TO_INLINE")
inline fun Channel.send(packet: Packet) {
    write(packet)
    flush()
}

inline fun Channel.respond(
    responding: Packet,
    error: Int = NO_ERROR,
    populate: TdfBuilder.() -> Unit = {},
) = send(createPacket(responding.rawComponent, responding.rawCommand, RESPONSE, responding.id, error, populate))

@Suppress("NOTHING_TO_INLINE")
inline fun Channel.respond(
    responding: Packet,
    content: ByteArray,
    error: Int = NO_ERROR,
) = send(Packet(responding.rawComponent, responding.rawCommand, error, RESPONSE, responding.id, content))

inline fun Channel.error(
    responding: Packet,
    error: Int,
    populate: TdfBuilder.() -> Unit = {},
) = send(createPacket(responding.rawComponent, responding.rawCommand, ERROR, responding.id, error, populate))

inline fun respond(
    responding: Packet,
    error: Int = NO_ERROR,
    populate: TdfBuilder.() -> Unit = {},
): Packet = createPacket(
    responding.rawComponent,
    responding.rawCommand,
    RESPONSE,
    responding.id,
    error,
    populate
)

inline fun Channel.unique(
    component: Component,
    command: Command,
    id: Int = 0x0,
    error: Int = NO_ERROR,
    populate: TdfBuilder.() -> Unit = {},
) = send(createPacket(component.id, command.value, UNIQUE, id, error, populate))

inline fun unique(
    component: Component,
    command: Command,
    id: Int = 0x0,
    error: Int = NO_ERROR,
    populate: TdfBuilder.() -> Unit = {},
): Packet = createPacket(component.id, command.value, UNIQUE, id, error, populate)

inline fun Channel.packet(
    component: Int,
    command: Int,
    qtype: Int,
    id: Int = 0x0,
    error: Int = NO_ERROR,
    populate: TdfBuilder.() -> Unit = {},
) = send(createPacket(component, command, qtype, id, error, populate))

inline fun createPacket(
    component: Int,
    command: Int,
    qtype: Int,
    id: Int = 0x0,
    error: Int = NO_ERROR,
    populate: TdfBuilder.() -> Unit = {},
): Packet {
    val contentBuilder = TdfBuilder()
    contentBuilder.populate()
    return Packet(
        component,
        command,
        error,
        qtype,
        id,
        contentBuilder.createByteArray()
    )
}


