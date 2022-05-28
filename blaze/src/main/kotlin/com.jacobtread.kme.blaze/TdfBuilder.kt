package com.jacobtread.kme.blaze

import com.jacobtread.kme.blaze.tdf.*
import com.jacobtread.kme.utils.VarPair
import com.jacobtread.kme.utils.VarTripple
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

/**
 * TdfBuilder Builder class used to create Tdf structures easily
 * rather than having to create all the objects manually the helper
 * methods on this class can be used instead which automatically adds
 * the correct Tdf value to the values list which is later written
 * as the packet content or used as struct values depending on context
 *
 * @constructor Create empty TdfBuilder
 */
class TdfBuilder {

    companion object {
        val EMPTY_BYTE_ARRAY = ByteArray(0)
    }

    val values = ArrayList<Tdf<*>>()

    /**
     * text Adds a new text value to the builder. This
     * becomes a StringTdf when created
     *
     * @param label The label of the Tdf
     * @param value The string value of the Tdf
     */
    fun text(label: String, value: String = "") {
        values.add(StringTdf(label, value))
    }

    /**
     * number Adds a new number value to the builder.
     * This becomes a VarInt when created
     *
     * @param label The label of the Tdf
     * @param value The long value of the Tdf
     */
    fun number(label: String, value: Long) {
        values.add(VarIntTdf(label, value))
    }

    /**
     * number Adds a new number value to the builder.
     * This becomes a VarInt when created
     *
     * @param label The label of the Tdf
     * @param value The int value of the Tdf
     */
    fun number(label: String, value: Int) {
        values.add(VarIntTdf(label, value.toLong()))
    }

    /**
     * bool Adds a numerical representation of the boolean value (0x0 or 0x1) as
     * a var int
     *
     * @param label The label of the Tdf
     * @param value The boolean value of the Tdf true = 0x1 false = 0x0
     */
    fun bool(label: String, value: Boolean) {
        values.add(VarIntTdf(label, if (value) 0x1 else 0x0))
    }


    /**
     * blob Adds a new blob value to the builder.
     * This becomes a BlobTdf when created
     *
     * @param label The label of the Tdf
     * @param value The byte array to be used as the blob
     */
    fun blob(label: String, value: ByteArray = EMPTY_BYTE_ARRAY) {
        values.add(BlobTdf(label, value))
    }

    /**
     * tripple Adds a new tripple value to the builder.
     * This becomes a TrippleTdf when created
     *
     * @param label The label of the Tdf
     * @param a The first value of the tripple
     * @param b The second value of the tripple
     * @param c The third value of the tripple
     */
    fun tripple(label: String, a: Long, b: Long, c: Long) {
        values.add(TrippleTdf(label, VarTripple(a, b, c)))
    }


    /**
     * tripple Adds a new tripple value to the builder.
     * This becomes a TrippleTdf when created
     *
     * @param label The label of the Tdf
     * @param value The tripple value
     */
    fun tripple(label: String, value: VarTripple) {
        values.add(TrippleTdf(label, value))
    }

    /**
     * pair Adds a new pair of values to the builder.
     * This becomes a PairTdf when created
     *
     * @param label The label of the Tdf
     * @param a The first value of the pair
     * @param b The second value of the pair
     */
    fun pair(label: String, a: Long, b: Long) {
        values.add(PairTdf(label, VarPair(a, b)))
    }

    /**
     * pair Adds a new pair of values to the builder.
     * This becomes a PairTdf when created
     *
     * @param label The label of the Tdf
     * @param value The pair of values
     */
    fun pair(label: String, value: VarPair) {
        values.add(PairTdf(label, value))
    }


    /**
     * float Adds a new float value to the builder.
     * This becomes a FloatTdf when created
     *
     * @param label The label of the Tdf
     * @param value The float value
     */
    fun float(label: String, value: Float) {
        values.add(FloatTdf(label, value))
    }

    /**
     * list Adds a new list value to the builder.
     * This becomes a ListTdf when created
     *
     * @param label The label of the Tdf
     * @param value The list value
     */
    inline fun <reified A : Any> list(label: String, value: List<A>) {
        list(label, Tdf.getTypeFromClass(A::class.java), value)
    }

    fun list(label: String, type: Int, value: List<Any>) {
        values.add(ListTdf(label, type, value))
    }

    /**
     * list Adds a new list value to the builder.
     * This becomes a ListTdf when created
     *
     * @param label The label of the Tdf
     * @param values The values to create the list from
     */
    inline fun <reified A : Any> list(label: String, vararg values: A) {
        val type = Tdf.getTypeFromClass(A::class.java)
        this.values.add(ListTdf(label, type, values.toList()))
    }


    /**
     * map Adds a new map value to the builder.
     * This becomes a MapTdf when created
     *
     * @param label The label of the Tdf
     * @param value The map value
     */
    inline fun <reified A : Any, reified B : Any> map(label: String, value: Map<A, B>) {
        map(label, Tdf.getTypeFromClass(A::class.java), Tdf.getTypeFromClass(B::class.java), value)
    }

    fun map(label: String, keyType: Int, valueType: Int, value: Map<*, *>) {
        values.add(MapTdf(label, keyType, valueType, value))
    }


    /**
     * varList Adds a new var int list value to the builder.
     * This becomes VarListTdf when created
     *
     * @param label The label of the Tdf
     * @param value The list value
     */
    fun varList(label: String, value: List<Long> = emptyList()) {
        values.add(VarIntList(label, value))
    }

    /**
     * union Adds a new union value to the builder.
     * This becomes a UnionTdf when created
     *
     * @param label The label of the Tdf
     * @param type The type of union
     * @param value The value of the union
     */
    fun optional(label: String, type: Int = 0x7F, value: Tdf<*>? = null) {
        values.add(OptionalTdf(label, type, value))
    }

    /**
     * union Adds a new union value to the builder.
     * This becomes a UnionTdf when created
     *
     * @param label The label of the Tdf
     * @param value The value of the union
     * @param type The type of union
     */
    fun optional(label: String, value: Tdf<*>, type: Int = 0x0) {
        values.add(OptionalTdf(label, type, value))
    }

    /**
     * unaryPlus Overriding the + modifier so that structs can
     * be added to the values using
     * ```
     * +struct("LABEL") {}
     * ```
     */
    @Suppress("NOTHING_TO_INLINE")
    inline operator fun Tdf<*>.unaryPlus() {
        values.add(this)
    }

    /**
     * createBuffer Writes the contents of this builder
     * to a ByteBuf and returns the created buffer
     *
     * @return The ByteArray of contents
     */
    fun createBuffer(): ByteBuf {
        val buffer = Unpooled.buffer()
        values.forEach { it.writeFully(buffer) }
        return buffer
    }

    /**
     * writeToBuffer Writes the contents of this
     * builder to the provided buffer
     *
     * @param buf The buffer to write to
     */
    fun writeToBuffer(buf: ByteBuf) {
        values.forEach { it.writeFully(buf) }
    }
}

/**
 * struct Creates a new struct tdf element
 *
 * @param label The label of this struct
 * @param start2 Whether the encoded data should start with a byte value of 2
 * @param init Initializer function for setting up this struct
 * @receiver
 * @return The newly created struct
 */
inline fun group(label: String = "", start2: Boolean = false, init: TdfBuilder.() -> Unit): GroupTdf {
    val context = TdfBuilder()
    context.init()
    return GroupTdf(label, start2, context.values)
}

