package com.newy.algotrade.web_flux.notification.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationCommandPort
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.domain.common.exception.PreconditionError
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogEntity
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogEntity.Status
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogRepository

@PersistenceAdapter
class SendNotificationCommandAdapter(
    private val sendNotificationLogRepository: SendNotificationLogRepository,
) : SendNotificationCommandPort {
    override suspend fun setStatusRequested(notificationAppId: Long, requestMessage: String): Long =
        sendNotificationLogRepository.save(
            SendNotificationLogEntity(
                notificationAppId = notificationAppId,
                requestMessage = requestMessage,
                status = Status.REQUESTED.name
            )
        ).id

    override suspend fun putStatusProcessing(sendNotificationLogId: Long): Boolean {
        val oldData =
            (sendNotificationLogRepository.findById(sendNotificationLogId)
                ?: throw NotFoundRowException("데이터를 찾을 수 없습니다. (id: $sendNotificationLogId)")).also {
                val preconditionStatuses = listOf(Status.REQUESTED.name, Status.FAILED.name)
                if (!preconditionStatuses.contains(it.status)) {
                    throw PreconditionError("REQUESTED, FAILED 상태만 변경 가능합니다. (status: ${it.status})")
                }
            }

        val newData = sendNotificationLogRepository.save(
            oldData.copy(
                status = Status.PROCESSING.name
            )
        )

        return oldData.updatedAt!! < newData.updatedAt
    }

    override suspend fun putResponseMessage(sendNotificationLogId: Long, responseMessage: String): Boolean {
        val oldData =
            (sendNotificationLogRepository.findById(sendNotificationLogId)
                ?: throw NotFoundRowException("데이터를 찾을 수 없습니다. (id: $sendNotificationLogId)")).also {
                val preconditionStatus = Status.PROCESSING.name
                if (it.status != preconditionStatus) {
                    throw PreconditionError("PROCESSING 상태만 변경 가능합니다. (status: ${it.status})")
                }
            }

        val newData = sendNotificationLogRepository.save(
            oldData.copy(
                responseMessage = responseMessage,
                status = if (responseMessage.lowercase() == "ok") Status.SUCCEED.name else Status.FAILED.name
            )
        )

        return oldData.updatedAt!! < newData.updatedAt
    }
}
