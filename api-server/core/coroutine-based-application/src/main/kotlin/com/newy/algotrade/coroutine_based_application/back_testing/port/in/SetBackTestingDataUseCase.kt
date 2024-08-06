package com.newy.algotrade.coroutine_based_application.back_testing.port.`in`

import com.newy.algotrade.domain.back_testing.BackTestingDataKey
import com.newy.algotrade.domain.common.extension.ProductPrice

interface SetBackTestingDataUseCase {
    suspend fun setBackTestingData(key: BackTestingDataKey, list: List<ProductPrice>): Boolean
}
