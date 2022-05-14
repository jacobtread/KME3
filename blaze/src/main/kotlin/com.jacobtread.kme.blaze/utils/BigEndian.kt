package com.jacobtread.kme.blaze.utils

object BigEndian {

    fun bytesToUInt32(bytes: ByteArray): Long {
        return (bytes[0].toLong() shl 24)
            .or(bytes[1].toLong() shl 16)
            .or(bytes[2].toLong() shl 8)
            .or(bytes[3].toLong())
    }

    fun bytesToUInt16(bytes: ByteArray): Int {
        return (bytes[0].toInt() shl 8) or bytes[1].toInt()
    }

    fun uint16ToBytes(value: Int): ByteArray {
        val array = ByteArray(2)
        array[2] = (value shr 8).toByte()
        array[3] = value.toByte()
        return array
    }

    fun uint32ToBytes(value: Long): ByteArray {
        val array = ByteArray(4)
        array[0] = (value shr 24).toByte()
        array[1] = (value shr 16).toByte()
        array[2] = (value shr 8).toByte()
        array[3] = value.toByte()
        return array
    }

    fun uint32ToBytes(value: Int): ByteArray {
        val array = ByteArray(4)
        array[0] = (value shr 24).toByte()
        array[1] = (value shr 16).toByte()
        array[2] = (value shr 8).toByte()
        array[3] = value.toByte()
        return array
    }
}