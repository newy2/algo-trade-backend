package com.newy.algotrade.setting.adapter.`in`.web

import com.newy.algotrade.setting.adapter.`in`.web.model.GetUserSettingResponse
import com.newy.algotrade.setting.port.`in`.GetUserSettingInPort
import com.newy.algotrade.setting.port.`in`.model.GetUserSettingQuery
import com.newy.algotrade.spring.auth.annotation.LoginUserInfo
import com.newy.algotrade.spring.auth.model.LoginUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GetUserSettingController(
    @Autowired private val getUserSettingInPort: GetUserSettingInPort,
) {
    @GetMapping("/setting")
    suspend fun getUserSetting(@LoginUserInfo loginUser: LoginUser): ResponseEntity<GetUserSettingResponse> {
        val userSetting = getUserSettingInPort.getUserSetting(GetUserSettingQuery(loginUser.id))

        return return ResponseEntity.ok(
            GetUserSettingResponse(
                domainModel = userSetting,
                isMaskedString = !loginUser.isAdminUser()
            )
        )
    }
}