package com.jacobtread.kme.blaze

import com.jacobtread.kme.blaze.tdf.*
import com.jacobtread.kme.blaze.utils.VarTripple

object TdfDumper {

    private const val STRUCT_INDENT = 2

    fun dump(values: List<Tdf>, indent: Int = 0, inline: Boolean = false): String {
        val out = StringBuilder()
        for (value in values) {
            out.append(dump(value, indent, inline))
            if (inline) {
                out.append(" ")
            } else {
                out.append('\n')
            }
        }
        return out.toString()
    }

    private fun dump(value: Tdf, indent: Int = 0, inline: Boolean): String {
        val label = value.label
        return " ".repeat(indent) + when (value) {
            is VarIntTdf -> "$label: 0x${value.value.toString(16)}"
            is StringTdf -> "$label: \"${value.value}\""
            is BlobTdf -> "$label: BLOB(" + value.value.joinToString(", ") { it.toInt().and(0xFF).toString() } + ")"
            is StructTdf -> dumpStruct(value, indent, inline)
            is ListTdf -> "$label: [" + value.value.joinToString(", ") { dumpListValue(it, indent, inline) } + "]"
            is UnionTdf -> dumpUnion(value, indent, inline)
            is VarIntList -> "$label: [" + value.value.joinToString(", ") { it.toString() } + "]"
            is MapTdf -> dumpMap(value, indent, inline)
            is PairTdf -> "(0x${value.value.a.toString(16)}, 0x${value.value.b.toString(16)})"
            is TrippleTdf -> "(0x${value.value.a.toString(16)}, 0x${value.value.b.toString(16)}, 0x${value.value.c.toString(16)})"
            is FloatTdf -> "${value.value}"
            else -> value.toString()
        }
    }

    private fun dumpStruct(value: StructTdf, indent: Int, inline: Boolean): String {
        val builder = StringBuilder()
        val newIndent = indent + STRUCT_INDENT
        builder.append(value.label)
            .append(" {")
        if (inline) {
            builder.append(' ')
            value.value.forEach {
                builder.append(dump(it, newIndent, true))
                builder.append(", ")
            }
            builder.append('}')
        } else {
            builder.append('\n')
            value.value.forEach {
                builder.append(" ".repeat(newIndent))
                builder.append(dump(it, newIndent, false))
                builder.append('\n')
            }
            builder.append(" ".repeat(indent) + "}")
        }
        return builder.toString()
    }

    private fun dumpUnion(value: UnionTdf, indent: Int, inline: Boolean): String {
        val builder = StringBuilder()
        builder.append(value.label)
            .append(" (0x")
            .append(value.type.toString(16))
            .append("): ")
        if (value.type == 0x7F) {
            builder.append("Empty")
        } else {
            val v = value.value
            if (v != null) builder.append(dump(v, indent, inline))
        }
        return builder.toString()
    }

    private fun dumpListValue(value: Any, indent: Int, inline: Boolean): String {
        return when (value) {
            is Long -> "0x${value.toString(16)}"
            is String -> "\"$value\""
            is StructTdf -> dumpStruct(value, indent, inline)
            is VarTripple -> "(0x${value.a.toString(16)}, 0x${value.b.toString(16)}, 0x${value.c.toString(16)})"
            else -> value.toString()
        }
    }

    private fun dumpMap(value: MapTdf, indent: Int, inline: Boolean): String {
        val builder = StringBuilder()
        val newIndent = indent + STRUCT_INDENT
        builder.append(value.label)
            .append(": [")
        if (inline) {
            builder.append(' ')
            for ((k, v) in value.map) {
                builder.append('(')
                    .append(dumpListValue(k, indent, true))
                    .append(',')
                    .append(dumpListValue(v, indent, true))
                    .append("), ")
            }
            builder.append("]")
        } else {
            builder.append('\n')
            for ((k, v) in value.map) {
                builder
                    .append(" ".repeat(newIndent))
                    .append('(')
                    .append(dumpListValue(k, newIndent, false))
                    .append(',')
                    .append(dumpListValue(v, newIndent, false))
                    .append("),\n")
            }
            builder.append(" ".repeat(indent) + "]")
        }
        return builder.toString()
    }

}