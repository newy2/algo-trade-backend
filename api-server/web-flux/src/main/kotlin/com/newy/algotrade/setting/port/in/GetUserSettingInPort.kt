package com.newy.algotrade.setting.port.`in`

import com.newy.algotrade.setting.domain.UserSetting
import com.newy.algotrade.setting.port.`in`.model.GetUserSettingQuery

fun interface GetUserSettingInPort {
    suspend fun getUserSetting(query: GetUserSettingQuery): UserSetting
}