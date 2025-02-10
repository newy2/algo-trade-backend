package com.newy.algotrade.setting.adapter.`in`.web.model

import com.newy.algotrade.common.extension.toMaskedString
import com.newy.algotrade.setting.domain.MarketAccount
import com.newy.algotrade.setting.domain.NotificationApp
import com.newy.algotrade.setting.domain.UserSetting
import java.time.LocalDateTime

data class GetUserSettingResponse(
    val marketAccounts: List<MarketAccountDto>,
    val notificationApp: NotificationAppDto?,
) {
    constructor(domainModel: UserSetting, isMaskedString: Boolean) : this(
        marketAccounts = toMarketAccounts(domainModel, isMaskedString),
        notificationApp = toNotificationApp(domainModel, isMaskedString),
    )

    companion object {
        private fun toMarketAccounts(domainModel: UserSetting, isMaskedString: Boolean): List<MarketAccountDto> {
            return domainModel.marketAccounts.map {
                if (!isMaskedString) {
                    it
                } else {
                    it.copy(appKey = it.appKey.toMaskedString(), appSecret = it.appSecret.toMaskedString())
                }.let {
                    MarketAccountDto(it)
                }
            }
        }

        private fun toNotificationApp(domainModel: UserSetting, isMaskedString: Boolean): NotificationAppDto? {
            return domainModel.notificationApp?.let {
                if (!isMaskedString) {
                    it
                } else {
                    it.copy(webhookUrl = it.webhookUrl.toMaskedString())
                }.let {
                    NotificationAppDto(it)
                }
            }
        }

    }
}

data class MarketAccountDto(
    val id: Long,
    val marketCode: String,
    val marketName: String,
    val displayName: String,
    val appKey: String,
    val appSecret: String,
) {
    constructor(domainModel: MarketAccount) : this(
        id = domainModel.id,
        marketCode = domainModel.marketCode.toString(),
        marketName = domainModel.marketName,
        displayName = domainModel.displayName,
        appKey = domainModel.appKey,
        appSecret = domainModel.appSecret,
    )
}

data class NotificationAppDto(
    val id: Long,
    val webhookType: String,
    val webhookUrl: String,
    val isVerified: Boolean,
    val verifyCodeExpiredAt: LocalDateTime
) {
    constructor(domainModel: NotificationApp) : this(
        id = domainModel.id,
        webhookType = domainModel.webhookType,
        webhookUrl = domainModel.webhookUrl,
        isVerified = domainModel.isVerified,
        verifyCodeExpiredAt = domainModel.verifyCodeExpiredAt,
    )
}