package com.newy.algotrade.notification_app.domain

open class CodeGenerator {
    companion object {
        private val NUMBERS = (0..9).map { '0' + it }
        private val ALPHABETS = (0..25).map { 'A' + it }
        val CODES = NUMBERS + ALPHABETS

        val INSTANCE by lazy { CodeGenerator() }
    }

    open fun generate(excludeCode: String = ""): String {
        var result: String

        do {
            result = CODES.shuffled().take(5).joinToString(separator = "")
        } while (result == excludeCode)

        return result
    }
}
