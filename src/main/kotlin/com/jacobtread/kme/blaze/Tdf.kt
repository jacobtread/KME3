package com.jacobtread.kme.blaze

import com.jacobtread.kme.utils.*
import io.netty.buffer.ByteBuf

interface TdfValue<T> {
    val value: T
}

abstract class Tdf(val label: String, private val tagType: Int) {
    abstract fun write(out: ByteBuf)

    fun writeFully(out: ByteBuf) {
        val tag = Labels.toTag(label)
        out.writeByte(tag.shr(24).and(0xFF).toInt())
        out.writeByte(tag.shr(16).and(0xFF).toInt())
        out.writeByte(tag.shr(8).and(0xFF).toInt())
        out.writeByte(tagType)
        write(out)
    }

    companion object {
        const val VARINT = 0x0
        const val STRING = 0x1
        const val BLOB = 0x02
        const val STRUCT = 0x3
        const val LIST = 0x4
        const val PAIR_LIST = 0x5
        const val UNION = 0x6
        const val INT_LIST = 0x7
        const val PAIR = 0x8
        const val TRIPPLE = 0x9
        const val FLOAT = 0xA

        const val VARINT_LIST = 0x0
        const val STRING_LIST = 0x1
        const val STRUCT_LIST = 0x3
        const val TRIPPLE_LIST = 0x9
        const val FLOAT_LIST = 0xA

        private fun ByteBuf.readUint(): UInt {
            val buffer = ByteArray(4)
            readBytes(buffer)

            return ((buffer[3].toUInt() shl 24) +
                    (buffer[2].toUInt() shl 16) +
                    (buffer[1].toUInt() shl 8) +
                    buffer[0].toUInt())
        }

        fun read(input: ByteBuf): Tdf {
            val head = input.readUnsignedInt()
            val tag = (head and 0xFFFFFF00).toInt()
            val label = Labels.fromTag(tag)
            return when (val type = (head and 0xFF).toInt()) {
                VARINT -> VarIntTdf.from(label, input)
                STRING -> StringTdf.from(label, input)
                BLOB -> BlobTdf.from(label, input)
                STRUCT -> StructTdf.from(label, input)
                LIST -> ListTdf.from(label, input)
                PAIR_LIST -> PairListTdf.from(label, input)
                UNION -> UnionTdf.from(label, input)
                INT_LIST -> VarIntList.from(label, input)
                PAIR -> PairTdf.from(label, input)
                TRIPPLE -> TrippleTdf.from(label, input)
                FLOAT -> FloatTdf.from(label, input)
                else -> throw IllegalStateException("Unknown Tdf type: $type")
            }
        }
    }
}

class VarIntTdf(label: String, override val value: Long) : Tdf(label, VARINT), TdfValue<Long> {
    companion object {
        fun from(label: String, input: ByteBuf): VarIntTdf {
            return VarIntTdf(label, input.readVarInt())
        }
    }

    override fun write(out: ByteBuf) = out.writeVarInt(value)
    override fun toString(): String = "VarInt($label: $value)"
}

class StringTdf(label: String, override val value: String) : Tdf(label, STRING), TdfValue<String> {
    companion object {
        fun from(label: String, input: ByteBuf): StringTdf {
            return StringTdf(label, input.readString())
        }
    }

    override fun write(out: ByteBuf) = out.writeString(value)

    override fun toString(): String = "String($label: $value)"
}

class BlobTdf(label: String, override val value: ByteArray) : Tdf(label, BLOB), TdfValue<ByteArray> {
    companion object {
        fun from(label: String, input: ByteBuf): BlobTdf {
            val size = input.readVarInt().toInt()
            val byteArray = ByteArray(size)
            input.readBytes(byteArray)
            return BlobTdf(label, byteArray)
        }
    }

    override fun write(out: ByteBuf) {
        out.writeVarInt(value.size.toLong())
        out.writeBytes(value)
    }

    override fun toString(): String = "Blob($label: ${value.contentToString()})"
}

class StructTdf(label: String, val start2: Boolean, override val value: List<Tdf>) : Tdf(label, STRUCT), TdfValue<List<Tdf>> {
    companion object {
        fun from(label: String, input: ByteBuf): StructTdf {
            val out = ArrayList<Tdf>()
            var start2 = false
            var byte: Int
            while (true) {
                byte = input.readUnsignedByte().toInt()
                if (byte == 0) break
                if (byte == 2) {
                    start2 = true
                } else {
                    input.readerIndex(input.readerIndex() - 1)
                }
                out.add(read(input))
            }
            return StructTdf(label, start2, out)
        }
    }

    override fun write(out: ByteBuf) {
        if (start2) out.writeByte(2)
        value.forEach {
            it.writeFully(out)
        }
        out.writeByte(0)
    }

    override fun toString(): String = "Struct($label: $value)"
}

class ListTdf(label: String, override val value: List<Any>) : Tdf(label, LIST), TdfValue<List<Any>> {

    companion object {
        fun from(label: String, input: ByteBuf): ListTdf {
            val subType = input.readUnsignedByte().toInt()
            val count = input.readVarInt().toInt()
            return when (subType) {
                VARINT_LIST -> {
                    val values = ArrayList<Long>(count)
                    repeat(count) { values.add(input.readVarInt()) }
                    ListTdf(label, values)
                }
                STRING_LIST -> {
                    val values = ArrayList<String>(count)
                    repeat(count) { values.add(input.readString()) }
                    ListTdf(label, values)
                }
                STRUCT_LIST -> {
                    val values = ArrayList<StructTdf>(count)
                    repeat(count) { values.add(StructTdf.from("", input)) }
                    ListTdf(label, values)
                }
                TRIPPLE_LIST -> {
                    val values = ArrayList<VTripple>(count)
                    repeat(count) {
                        values.add(
                            VTripple(
                                input.readVarInt(),
                                input.readVarInt(),
                                input.readVarInt(),
                            )
                        )
                    }
                    ListTdf(label, values)
                }
                else -> throw IllegalStateException("Unknown list subtype $subType")
            }
        }

    }

    private val subType: Int

    init {
        require(value.isNotEmpty()) { "ListTdf contents cannot be empty" }
        subType = when (value[0]) {
            is Long -> VARINT_LIST
            is String -> STRING_LIST
            is StructTdf -> STRUCT_LIST
            is VTripple -> TRIPPLE_LIST
            else -> throw IllegalArgumentException("Don't know how to handle type \"${value[0]::class.java.simpleName}")
        }
    }

    override fun write(out: ByteBuf) {
        out.writeByte(subType)
        out.writeVarInt(value.size.toLong())
        when (subType) {
            VARINT_LIST -> value.forEach { out.writeVarInt(it as Long) }
            STRING_LIST -> value.forEach { out.writeString(it as String) }
            STRUCT_LIST -> value.forEach { (it as StructTdf).write(out) }
            TRIPPLE_LIST -> value.forEach {
                val tripple = it as VTripple
                out.writeVarInt(tripple.a)
                out.writeVarInt(tripple.b)
                out.writeVarInt(tripple.c)
            }
        }
    }

    override fun toString(): String = "List($label: $value)"
}

class PairListTdf(label: String, val a: List<Any>, val b: List<Any>) : Tdf(label, LIST) {

    companion object {
        fun from(label: String, input: ByteBuf): PairListTdf {
            val subTypeA = input.readUnsignedByte().toInt()
            val subTypeB = input.readUnsignedByte().toInt()
            val count = input.readVarInt().toInt()

            val a = ArrayList<Any>(count)
            val b = ArrayList<Any>(count)
            repeat(count) {
                when (subTypeA) {
                    VARINT_LIST -> a.add(input.readVarInt())
                    STRING_LIST -> a.add(input.readString())
                    STRUCT_LIST -> a.add(StructTdf.from("", input))
                    FLOAT_LIST -> a.add(input.readFloat())
                    else -> throw IllegalStateException("Unknown list subtype $subTypeA")
                }
                when (subTypeB) {
                    VARINT_LIST -> b.add(input.readVarInt())
                    STRING_LIST -> b.add(input.readString())
                    STRUCT_LIST -> b.add(StructTdf.from("", input))
                    FLOAT_LIST -> b.add(input.readFloat())
                    else -> throw IllegalStateException("Unknown list subtype $subTypeA")
                }
            }
            return PairListTdf(label, a, b);
        }
    }

    private val subTypeA: Int
    private val subTypeB: Int

    init {
        require(a.isNotEmpty()) { "PairListTdf contents cannot be empty" }
        require(b.isNotEmpty()) { "PairListTdf contents cannot be empty" }
        require(a.size == b.size) { "PairListTdf both lists must have equal length" }
        subTypeA = when (a[0]) {
            is Long -> VARINT_LIST
            is String -> STRING_LIST
            is StructTdf -> STRUCT_LIST
            is Float -> FLOAT_LIST
            else -> throw IllegalArgumentException("Don't know how to handle type \"${a[0]::class.java.simpleName}")
        }
        subTypeB = when (b[0]) {
            is Long -> VARINT_LIST
            is String -> STRING_LIST
            is StructTdf -> STRUCT_LIST
            is Float -> FLOAT_LIST
            else -> throw IllegalArgumentException("Don't know how to handle type \"${b[0]::class.java.simpleName}")
        }
    }

    override fun write(out: ByteBuf) {
        out.writeByte(subTypeA)
        out.writeByte(subTypeB)
        out.writeVarInt(a.size.toLong())
        val length = a.size
        repeat(length) {
            val aValue = a[it]
            val bValue = b[it]
            when (subTypeA) {
                VARINT_LIST -> out.writeVarInt(aValue as Long)
                STRING_LIST -> out.writeString(aValue as String)
                STRUCT_LIST -> (aValue as StructTdf).write(out)
                FLOAT_LIST -> out.writeFloat(aValue as Float)
            }
            when (subTypeB) {
                VARINT_LIST -> out.writeVarInt(bValue as Long)
                STRING_LIST -> out.writeString(bValue as String)
                STRUCT_LIST -> (bValue as StructTdf).write(out)
                FLOAT_LIST -> out.writeFloat(bValue as Float)
            }
        }
    }


    override fun toString(): String = "PairList($label: $a, $b)"

}

class UnionTdf(label: String, val type: Int = 0x7F, val value: Tdf? = null) : Tdf(label, UNION) {
    companion object {
        fun from(label: String, input: ByteBuf): UnionTdf {
            val type = input.readUnsignedByte().toInt()
            val value = if (type != 0x7F) {
                read(input)
            } else null
            return UnionTdf(label, type, value)
        }
    }

    override fun write(out: ByteBuf) {
        out.writeByte(type)
        if (type != 0x7F) {
            value?.writeFully(out)
        }
    }

    override fun toString(): String = "Union($label: $type, $value)"
}

class VarIntList(label: String, override val value: List<Long>) : Tdf(label, INT_LIST), TdfValue<List<Long>> {
    companion object {
        fun from(label: String, input: ByteBuf): VarIntList {
            val count = input.readVarInt().toInt()
            val values = ArrayList<Long>(count)
            repeat(count) { values.add(input.readVarInt()) }
            return VarIntList(label, values)
        }
    }

    override fun write(out: ByteBuf) {
        out.writeVarInt(value.size.toLong())
        value.forEach { out.writeVarInt(it) }
    }

    override fun toString(): String = "VarIntList($label: $value)"
}

class PairTdf(label: String, override val value: VPair) : Tdf(label, PAIR), TdfValue<VPair> {
    companion object {
        fun from(label: String, input: ByteBuf): PairTdf {
            val a = input.readVarInt()
            val b = input.readVarInt()
            return PairTdf(label, VPair(a, b))
        }
    }

    override fun write(out: ByteBuf) {
        out.writeVarInt(value.a)
        out.writeVarInt(value.b)
    }

    override fun toString(): String = "Pair($label: $value)"
}

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

class FloatTdf(label: String, override val value: Float) : Tdf(label, FLOAT), TdfValue<Float> {
    companion object {
        fun from(label: String, input: ByteBuf): FloatTdf {
            val value = input.readFloat()
            return FloatTdf(label, value)
        }
    }

    override fun write(out: ByteBuf) {
        out.writeFloat(value)
    }

    override fun toString(): String = "Float($label: $value)"
}