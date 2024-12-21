package com.newy.algotrade.auth.adpter.out.internal_system

import com.newy.algotrade.auth.port.out.AccessTokenPort
import com.newy.algotrade.domain.auth.ByBitPrivateApiInfo

class ByBitAccessTokenCalculator : AccessTokenPort<ByBitPrivateApiInfo> {
    override suspend fun findAccessToken(info: ByBitPrivateApiInfo) = info.accessToken()
}