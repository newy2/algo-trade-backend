package com.newy.algotrade.setting.domain

data class UserSetting(
    val marketAccounts: List<MarketAccount>,
    val notificationApp: NotificationApp?,
)