package com.newy.algotrade.web_flux.notification.adapter.`in`.web

import com.newy.algotrade.notification.port.`in`.SetNotificationAppUseCase
import com.newy.algotrade.web_flux.common.annotation.WebAdapter
import com.newy.algotrade.web_flux.common.web.BooleanResponse
import com.newy.algotrade.web_flux.notification.adapter.`in`.web.model.SetNotificationAppRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@WebAdapter
@RestController
@RequestMapping("/notification")
class SetNotificationAppController(
    private val setNotificationAppUseCase: SetNotificationAppUseCase,
) {
    @PostMapping
    suspend fun setNotificationApp(@RequestBody request: SetNotificationAppRequest): ResponseEntity<BooleanResponse> {
        val isSaved = setNotificationAppUseCase.setNotificationApp(request.toIncomingPortModel())

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(BooleanResponse(isSaved))
    }
}