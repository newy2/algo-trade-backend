package com.newy.algotrade.back_testing.port.`in`

import com.newy.algotrade.domain.back_testing.BackTestingDataKey
import com.newy.algotrade.domain.chart.DEFAULT_CANDLE_SIZE
import com.newy.algotrade.domain.common.extension.ProductPrice

interface CreateBackTestingDataUseCase {
    suspend fun createData(key: BackTestingDataKey, seedSize: Int = DEFAULT_CANDLE_SIZE): List<ProductPrice>
}

interface SetBackTestingDataUseCase {
    suspend fun setBackTestingData(key: BackTestingDataKey, list: List<ProductPrice>): Boolean
}