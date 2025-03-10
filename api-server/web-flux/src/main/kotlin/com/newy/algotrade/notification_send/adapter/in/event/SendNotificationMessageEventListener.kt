package com.newy.algotrade.notification_send.adapter.`in`.event

import com.newy.algotrade.common.event.SendNotificationMessageEvent
import com.newy.algotrade.notification_send.port.`in`.SendNotificationMessageInPort
import com.newy.algotrade.notification_send.port.`in`.model.SendNotificationMessageCommand
import com.newy.algotrade.spring.annotation.InternalSystemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener

@Profile("!test")
@InternalSystemAdapter
class SendNotificationMessageEventListener(
    private val backgroundScope: CoroutineScope,
    private val sendNotificationMessageInPort: SendNotificationMessageInPort,
) {
    @EventListener
    fun onReceiveMessage(event: SendNotificationMessageEvent) {
        backgroundScope.launch {
            sendNotificationMessageInPort.sendNotificationMessage(
                SendNotificationMessageCommand(
                    userId = event.userId,
                    message = event.message,
                    isVerified = event.isVerified
                )
            )
        }
    }
}