package com.newy.algotrade.back_testing.domain

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.common.domain.helper.SimpleCsvParser
import com.newy.algotrade.common.domain.helper.SimpleCsvWriter
import java.io.File
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class BackTestingFileManager {
    companion object {
        private const val FOLDER_NAME = "/back-testing-source-data"
    }

    fun folderPath(): String {
        return javaClass.getResource(FOLDER_NAME)?.path ?: ""
    }

    private fun getFile(key: BackTestingDataKey): File {
        return File(folderPath(), fileName(key))
    }

    fun hasFile(key: BackTestingDataKey): Boolean {
        return getFile(key).exists()
    }

    fun getList(key: BackTestingDataKey): List<ProductPrice> {
        if (!hasFile(key)) {
            return emptyList()
        }

        return SimpleCsvParser.parseFromFile(getFile(key)).map {
            Candle.TimeFrame.from(key.productPriceKey.interval)!!(
                Instant.ofEpochMilli(it[0].toLong()).atOffset(ZoneOffset.ofHours(9)),
                it[1].toBigDecimal(),
                it[2].toBigDecimal(),
                it[3].toBigDecimal(),
                it[4].toBigDecimal(),
                it[5].toBigDecimal(),
            )
        }
    }

    fun setList(key: BackTestingDataKey, list: List<ProductPrice>) {
        SimpleCsvWriter.write(
            getFile(key),
            listOf("startTime", "openPrice", "highPrice", "lowPrice", "closePrice", "volume"),
            list.map {
                listOf(
                    it.time.begin.toInstant().toEpochMilli(),
                    it.price.open,
                    it.price.high,
                    it.price.low,
                    it.price.close,
                    it.volume,
                )
            }
        )
    }

    fun fileName(key: BackTestingDataKey): String {
        return key.run {
            val beginTime = searchBeginTime.withOffsetSameInstant(ZoneOffset.ofHours(9))
            val endTime = searchEndTime.withOffsetSameInstant(ZoneOffset.ofHours(9))
            val formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH_mmZ")
                .withZone(ZoneOffset.ofHours(9))

            listOf(
                productPriceKey.market,
                productPriceKey.productType,
                productPriceKey.productCode,
                Candle.TimeFrame.from(productPriceKey.interval)!!,
                "${formatter.format(beginTime)} - ${formatter.format(endTime)}"
            ).joinToString(
                separator = "",
                transform = { "[$it]" },
                postfix = ".csv",
            )
        }
    }
}