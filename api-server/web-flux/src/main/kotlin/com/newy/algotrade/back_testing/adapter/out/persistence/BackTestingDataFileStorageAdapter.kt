package com.newy.algotrade.back_testing.adapter.out.persistence

import com.newy.algotrade.back_testing.domain.BackTestingDataKey
import com.newy.algotrade.back_testing.domain.BackTestingFileManager
import com.newy.algotrade.back_testing.port.out.BackTestingDataPort
import com.newy.algotrade.common.extension.ProductPrice

class BackTestingDataFileStorageAdapter(
    private val backTestingFileManager: BackTestingFileManager
) : BackTestingDataPort {
    override suspend fun findBackTestingData(key: BackTestingDataKey): List<ProductPrice> =
        backTestingFileManager.getList(key)

    override suspend fun saveBackTestingData(key: BackTestingDataKey, list: List<ProductPrice>) {
        backTestingFileManager.setList(key, list)
    }
}