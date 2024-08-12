package com.newy.algotrade.coroutine_based_application.back_testing.port.out

import com.newy.algotrade.domain.back_testing.BackTestingDataKey
import com.newy.algotrade.domain.common.extension.ProductPrice

interface BackTestingDataPort :
    FindBackTestingDataPort,
    SaveBackTestingDataPort

fun interface FindBackTestingDataPort {
    suspend fun findBackTestingData(key: BackTestingDataKey): List<ProductPrice>
}

fun interface SaveBackTestingDataPort {
    suspend fun saveBackTestingData(key: BackTestingDataKey, list: List<ProductPrice>)
}
