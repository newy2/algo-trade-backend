package com.newy.algotrade.coroutine_based_application.auth.adpter.out.internal_system

import com.newy.algotrade.coroutine_based_application.auth.port.out.GetAccessTokenPort
import com.newy.algotrade.domain.auth.ByBitPrivateApiInfo

class ByBitAccessTokenCalculator : GetAccessTokenPort<ByBitPrivateApiInfo> {
    override suspend fun accessToken(info: ByBitPrivateApiInfo) = info.accessToken()
}