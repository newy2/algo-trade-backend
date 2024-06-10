package com.newy.algotrade.coroutine_based_application.product.port.`in`

import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.domain.chart.DEFAULT_CANDLE_SIZE
import com.newy.algotrade.domain.common.extension.ProductPrice

interface CreateBackTestingDataUseCase {
    suspend fun createData(key: BackTestingDataKey, seedSize: Int = DEFAULT_CANDLE_SIZE): List<ProductPrice>
}
