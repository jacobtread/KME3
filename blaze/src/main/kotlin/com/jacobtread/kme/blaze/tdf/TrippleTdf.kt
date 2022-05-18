package com.jacobtread.kme.blaze.tdf

import com.jacobtread.kme.blaze.utils.readVarInt
import com.jacobtread.kme.blaze.utils.writeVarInt
import com.jacobtread.kme.utils.VTripple
import io.netty.buffer.ByteBuf

class TrippleTdf(label: String, override val value: VTripple) : Tdf(label, TRIPPLE), TdfValue<VTripple> {
    companion object {
        fun from(label: String, input: ByteBuf): TrippleTdf {
            val a = input.readVarInt()
            val b = input.readVarInt()
            val c = input.readVarInt()
            return TrippleTdf(label, VTripple(a, b, c))
        }
    }

    override fun write(out: ByteBuf) {
        out.writeVarInt(value.a)
        out.writeVarInt(value.b)
        out.writeVarInt(value.c)
    }

    override fun toString(): String = "Tripple($label: $value)"
}