package com.newy.algotrade.setting.service

import com.newy.algotrade.setting.domain.UserSetting
import com.newy.algotrade.setting.port.`in`.GetUserSettingInPort
import com.newy.algotrade.setting.port.`in`.model.GetUserSettingQuery
import com.newy.algotrade.setting.port.out.GetMarketAccountsOutPort
import com.newy.algotrade.setting.port.out.GetNotificationAppOutPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUserSettingQueryService(
    private val getMarketAccountsOutPort: GetMarketAccountsOutPort,
    private val getNotificationAppOutPort: GetNotificationAppOutPort,
) : GetUserSettingInPort {
    @Transactional(readOnly = true)
    override suspend fun getUserSetting(query: GetUserSettingQuery) = UserSetting(
        marketAccounts = getMarketAccountsOutPort.getMarketAccounts(query.userId),
        notificationApp = getNotificationAppOutPort.getNotificationApp(query.userId),
    )
}