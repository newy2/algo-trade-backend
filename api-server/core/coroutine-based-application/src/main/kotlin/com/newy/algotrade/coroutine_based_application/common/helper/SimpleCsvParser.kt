package com.newy.algotrade.coroutine_based_application.common.helper

import java.util.stream.Stream

object SimpleCsvParser {
    fun parse(stream: Stream<String>): Array<Array<String>> = stream
        .skip(1)
        .map { it.split(",").map { eachText -> eachText.trim() }.toTypedArray() }
        .toArray { length -> arrayOfNulls(length) }

    fun parseFromResource(resourceName: String) = javaClass.getResourceAsStream(resourceName)
        ?.bufferedReader()
        ?.lines()
        ?.let {
            parse(it)
        } ?: emptyArray<Array<String>>()
}