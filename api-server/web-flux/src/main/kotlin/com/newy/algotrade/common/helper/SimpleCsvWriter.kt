package com.newy.algotrade.common.helper

import java.io.File

object SimpleCsvWriter {
    fun write(file: File, titles: List<String>, contents: List<List<Any>>) {
        if (contents.isEmpty() || titles.size != contents[0].size) {
            throw IllegalArgumentException()
        }

        file.bufferedWriter().use {
            it.write(titles.joinToString())
            it.newLine()
            contents.forEach { eachContent ->
                it.appendLine(eachContent.joinToString())
            }
        }
    }
}