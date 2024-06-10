package com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.price2.domain.BackTestingFileManager
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.BackTestingDataPort
import com.newy.algotrade.domain.common.extension.ProductPrice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class FileBackTestingDataStore(
    private val backTestingFileManager: BackTestingFileManager
) : BackTestingDataPort {
    override suspend fun getBackTestingData(key: BackTestingDataKey): List<ProductPrice> {
        return CoroutineScope(Dispatchers.IO).async {
            backTestingFileManager.getList(key)
        }.await()
    }

    override suspend fun setBackTestingData(key: BackTestingDataKey, list: List<ProductPrice>) {
        CoroutineScope(Dispatchers.IO).async {
            backTestingFileManager.setList(key, list)
        }.await()
    }
}