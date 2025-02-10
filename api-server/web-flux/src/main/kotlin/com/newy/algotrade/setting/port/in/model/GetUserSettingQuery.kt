package com.newy.algotrade.setting.port.`in`.model

import com.newy.algotrade.common.helper.SelfValidating
import jakarta.validation.constraints.Min

data class GetUserSettingQuery(
    @field:Min(1) val userId: Long,
) : SelfValidating() {
    init {
        validate()
    }
}