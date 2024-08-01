package com.newy.algotrade.coroutine_based_application.back_testing.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.CreateBackTestingDataUseCase
import com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.SetBackTestingDataUseCase
import com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.model.BackTestingDataKey

class CreateBackTestingDataController(
    private val createBackTestingDataUseCase: CreateBackTestingDataUseCase,
    private val setBackTestingDataUseCase: SetBackTestingDataUseCase
) {
    suspend fun createBackTestingData(key: BackTestingDataKey): Boolean {
        val list = createBackTestingDataUseCase.createData(key)
        return setBackTestingDataUseCase.setBackTestingData(key, list)
    }

}