package com.newy.algotrade.coroutine_based_application.back_testing.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.back_testing.port.out.BackTestingDataPort
import com.newy.algotrade.domain.back_testing.BackTestingDataKey
import com.newy.algotrade.domain.back_testing.BackTestingFileManager
import com.newy.algotrade.domain.common.extension.ProductPrice

class BackTestingDataFileStorageAdapter(
    private val backTestingFileManager: BackTestingFileManager
) : BackTestingDataPort {
    override suspend fun getBackTestingData(key: BackTestingDataKey): List<ProductPrice> =
        backTestingFileManager.getList(key)

    override suspend fun setBackTestingData(key: BackTestingDataKey, list: List<ProductPrice>) {
        backTestingFileManager.setList(key, list)
    }
}