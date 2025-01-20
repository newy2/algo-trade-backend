package com.newy.algotrade.auth.adpter.out.internal_system

import com.newy.algotrade.auth.domain.ByBitPrivateApiInfo
import com.newy.algotrade.auth.port.out.AccessTokenPort

class ByBitAccessTokenCalculator : AccessTokenPort<ByBitPrivateApiInfo> {
    override suspend fun findAccessToken(info: ByBitPrivateApiInfo) = info.accessToken()
}