package com.newy.algotrade.coroutine_based_application.price2.domain.back_test

import com.newy.algotrade.coroutine_based_application.common.helper.SimpleCsvParser
import com.newy.algotrade.domain.chart.Candle
import java.io.File
import java.time.Instant

private const val FOLDER_NAME = "/back-testing-source-data"

class FileCache {
    fun insert(key: BackTestDataLoader.Key, list: List<Candle>) {
        file(key).bufferedWriter().use { file ->
            file.write("startTime, openPrice, highPrice, lowPrice, closePrice, volume")
            file.newLine()
            list.forEach { eachCandle ->
                file.appendLine(
                    listOf(
                        eachCandle.time.begin.toInstant().toEpochMilli(),
                        eachCandle.price.open,
                        eachCandle.price.high,
                        eachCandle.price.low,
                        eachCandle.price.close,
                        eachCandle.volume,
                    ).joinToString()
                )
            }
        }
    }

    fun load(key: BackTestDataLoader.Key): List<Candle> {
        return loadCsvFile(key).map {
            Candle.TimeFrame.from(key.productPriceKey.interval)!!(
                Instant.ofEpochMilli(it[0].toLong()).atOffset(key.startDateTime.offset),
                it[1].toBigDecimal(),
                it[2].toBigDecimal(),
                it[3].toBigDecimal(),
                it[4].toBigDecimal(),
                it[5].toBigDecimal(),
            )
        }
    }

    private fun file(key: BackTestDataLoader.Key): File {
        return File(filePath(key)).also {
            if (!it.exists()) {
                it.createNewFile()
            }
        }
    }

    private fun filePath(key: BackTestDataLoader.Key): String {
        val parentPath = javaClass.getResource(FOLDER_NAME).path
        val fileName = cacheFileName(key)
        return "$parentPath/$fileName"
    }

    private fun loadCsvFile(key: BackTestDataLoader.Key): Array<Array<String>> {
        return SimpleCsvParser.parseFromResource("$FOLDER_NAME/${cacheFileName(key)}")
    }

    private fun cacheFileName(key: BackTestDataLoader.Key): String {
        return key.run {
            listOf(
                productPriceKey.market,
                productPriceKey.productType,
                productPriceKey.productCode,
                Candle.TimeFrame.from(productPriceKey.interval)!!,
                "$startDateTime - $endDateTime"
            ).joinToString(
                separator = "",
                transform = { "[$it]" },
                postfix = ".csv",
            )
        }
    }
}