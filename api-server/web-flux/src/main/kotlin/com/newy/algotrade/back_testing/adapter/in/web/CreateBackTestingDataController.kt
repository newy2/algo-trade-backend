package com.newy.algotrade.back_testing.adapter.`in`.web

import com.newy.algotrade.back_testing.domain.BackTestingDataKey
import com.newy.algotrade.back_testing.port.`in`.CreateBackTestingDataUseCase
import com.newy.algotrade.back_testing.port.`in`.SetBackTestingDataUseCase

class CreateBackTestingDataController(
    private val createBackTestingDataUseCase: CreateBackTestingDataUseCase,
    private val setBackTestingDataUseCase: SetBackTestingDataUseCase
) {
    suspend fun createBackTestingData(key: BackTestingDataKey): Boolean =
        createBackTestingDataUseCase.createData(key).let {
            setBackTestingDataUseCase.setBackTestingData(key, it)
        }
}