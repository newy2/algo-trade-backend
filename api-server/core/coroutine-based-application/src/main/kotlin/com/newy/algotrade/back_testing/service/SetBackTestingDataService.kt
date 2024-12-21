package com.newy.algotrade.back_testing.service

import com.newy.algotrade.back_testing.port.`in`.SetBackTestingDataUseCase
import com.newy.algotrade.back_testing.port.out.SaveBackTestingDataPort
import com.newy.algotrade.domain.back_testing.BackTestingDataKey
import com.newy.algotrade.domain.common.extension.ProductPrice

class SetBackTestingDataService(
    private val backTestingDataPort: SaveBackTestingDataPort
) : SetBackTestingDataUseCase {
    override suspend fun setBackTestingData(key: BackTestingDataKey, list: List<ProductPrice>): Boolean {
        return try {
            backTestingDataPort.saveBackTestingData(key, list)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}