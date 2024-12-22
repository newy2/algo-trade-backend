package com.newy.algotrade.common.helper

import java.io.BufferedReader
import java.io.File
import java.util.stream.Stream

object SimpleCsvParser {
    fun parse(stream: Stream<String>): Array<Array<String>> = stream
        .skip(1)
        .map { it.split(",").map { eachText -> eachText.trim() }.toTypedArray() }
        .toArray { length -> arrayOfNulls(length) }

    fun parseFromResource(resourceName: String) =
        parseFromReader(javaClass.getResourceAsStream(resourceName)?.bufferedReader())

    fun parseFromFile(file: File) =
        parseFromReader(file.bufferedReader())

    private fun parseFromReader(reader: BufferedReader?) = reader
        ?.lines()
        ?.let {
            parse(it)
        } ?: emptyArray<Array<String>>()
}