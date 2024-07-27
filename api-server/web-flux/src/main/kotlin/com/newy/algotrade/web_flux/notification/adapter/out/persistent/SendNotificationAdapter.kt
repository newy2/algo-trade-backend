package com.newy.algotrade.web_flux.notification.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationPort
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.domain.common.exception.PreconditionError
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogEntity
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogRepository

@PersistenceAdapter
class SendNotificationAdapter(
    private val sendNotificationLogRepository: SendNotificationLogRepository,
) : SendNotificationPort {
    override suspend fun getSendNotification(sendNootificationLogId: Long) =
        sendNotificationLogRepository.findByIdAsSendNotification(sendNotificationId = sendNootificationLogId)
            ?: throw NotFoundRowException("notificationLogId 를 찾을 수 없습니다. (id: ${sendNootificationLogId})")

    override suspend fun setStatusRequested(notificationAppId: Long, requestMessage: String): Long =
        sendNotificationLogRepository.save(
            SendNotificationLogEntity(
                notificationAppId = notificationAppId,
                requestMessage = requestMessage,
                status = SendNotificationLogEntity.Status.REQUESTED.name
            )
        ).id

    override suspend fun putStatusProcessing(sendNotificationLogId: Long): Boolean {
        val oldData =
            (sendNotificationLogRepository.findById(sendNotificationLogId)
                ?: throw NotFoundRowException("데이터를 찾을 수 없습니다. (id: $sendNotificationLogId)")).also {
                val preconditionStatuses = listOf(
                    SendNotificationLogEntity.Status.REQUESTED.name,
                    SendNotificationLogEntity.Status.FAILED.name
                )
                if (!preconditionStatuses.contains(it.status)) {
                    throw PreconditionError("REQUESTED, FAILED 상태만 변경 가능합니다. (status: ${it.status})")
                }
            }

        val newData = sendNotificationLogRepository.save(
            oldData.copy(
                status = SendNotificationLogEntity.Status.PROCESSING.name
            )
        )

        return oldData.updatedAt!! < newData.updatedAt
    }

    override suspend fun putResponseMessage(sendNotificationLogId: Long, responseMessage: String): Boolean {
        val oldData =
            (sendNotificationLogRepository.findById(sendNotificationLogId)
                ?: throw NotFoundRowException("데이터를 찾을 수 없습니다. (id: $sendNotificationLogId)")).also {
                val preconditionStatus = SendNotificationLogEntity.Status.PROCESSING.name
                if (it.status != preconditionStatus) {
                    throw PreconditionError("PROCESSING 상태만 변경 가능합니다. (status: ${it.status})")
                }
            }

        val newData = sendNotificationLogRepository.save(
            oldData.copy(
                responseMessage = responseMessage,
                status = if (responseMessage.lowercase() == "ok") SendNotificationLogEntity.Status.SUCCEED.name else SendNotificationLogEntity.Status.FAILED.name
            )
        )

        return oldData.updatedAt!! < newData.updatedAt
    }
}