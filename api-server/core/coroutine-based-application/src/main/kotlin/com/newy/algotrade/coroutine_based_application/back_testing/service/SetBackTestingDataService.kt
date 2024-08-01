package com.newy.algotrade.coroutine_based_application.back_testing.service

import com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.SetBackTestingDataUseCase
import com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.coroutine_based_application.back_testing.port.out.SetBackTestingDataPort
import com.newy.algotrade.domain.common.extension.ProductPrice

class SetBackTestingDataService(
    private val backTestingDataPort: SetBackTestingDataPort
) : SetBackTestingDataUseCase {
    override suspend fun setBackTestingData(key: BackTestingDataKey, list: List<ProductPrice>): Boolean {
        return try {
            backTestingDataPort.setBackTestingData(key, list)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}