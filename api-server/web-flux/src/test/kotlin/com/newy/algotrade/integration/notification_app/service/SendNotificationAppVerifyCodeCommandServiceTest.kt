package com.newy.algotrade.integration.notification_app.service

import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.port.`in`.model.SendNotificationAppVerifyCodeCommand
import com.newy.algotrade.notification_app.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SaveNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SendNotificationMessageOutPort
import com.newy.algotrade.notification_app.service.SendNotificationAppVerifyCodeCommandService
import com.newy.algotrade.spring.hook.useTransactionHook
import helpers.spring.BaseDataR2dbcTest
import kotlinx.coroutines.test.runTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import kotlin.test.Test
import kotlin.test.assertEquals

class NullFindNotificationAppOutPort : FindNotificationAppOutPort {
    override suspend fun findByUserId(userId: Long) = null
}

class NullSaveNotificationAppOutPort : SaveNotificationAppOutPort {
    override suspend fun save(app: NotificationApp) = true
}

class SendNotificationAppVerifyCodeCommandServiceTest(
    @Autowired private val transactionManager: ReactiveTransactionManager,
) : BaseDataR2dbcTest() {
    @Test
    fun `onAfterCommit 이후에 sendNotificationMessageOutPort 가 호출된다`() = runTest {
        var log = ""
        val mockSendNotificationMessageOutPort = SendNotificationMessageOutPort {
            log += "sendNotificationMessage "
        }
        val service = SendNotificationAppVerifyCodeCommandService(
            findNotificationAppOutPort = NullFindNotificationAppOutPort(),
            saveNotificationAppOutPort = NullSaveNotificationAppOutPort(),
            sendNotificationMessageOutPort = mockSendNotificationMessageOutPort,
        )

        TransactionalOperator.create(transactionManager).executeAndAwait {
            useTransactionHook(
                onAfterCommit = { log += "onAfterCommit " }
            )

            service.sendVerifyCode(
                command = SendNotificationAppVerifyCodeCommand(
                    userId = 1,
                    webhookType = "SLACK",
                    webhookUrl = "https://hooks.slack.com/services/1111",
                )
            )
        }

        assertEquals("onAfterCommit sendNotificationMessage ", log)
    }
}