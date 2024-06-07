package com.newy.algotrade.coroutine_based_application.price2.port.`in`

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.domain.common.extension.ProductPrice

interface SetBackTestingDataUseCase {
    suspend fun setBackTestingData(key: BackTestingDataKey, list: List<ProductPrice>): Boolean
}
