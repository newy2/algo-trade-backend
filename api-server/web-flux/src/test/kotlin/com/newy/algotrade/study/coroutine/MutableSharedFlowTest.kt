package com.newy.algotrade.study.coroutine

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MutableSharedFlowTest {
    @Test
    fun `브로드캐스트 이벤트 발행 구독 테스트`() = runTest {
        val broadcastEventSender = MutableSharedFlow<String>()
        val broadcastEventReceiver = broadcastEventSender.asSharedFlow()

        val list1 = mutableListOf<String>()
        val list2 = mutableListOf<String>()

        broadcastEventSender.emit("0")

        launch {
            broadcastEventReceiver.collect {
                list1.add(it)
            }
        }

        delay(1000)
        broadcastEventSender.emit("1")
        broadcastEventSender.emit("2")

        launch {
            broadcastEventReceiver.collect {
                list2.add(it)
            }
        }

        delay(1000)
        broadcastEventSender.emit("3")
        broadcastEventSender.emit("4")

        coroutineContext.cancelChildren()

        assertEquals(listOf("1", "2", "3", "4"), list1)
        assertEquals(listOf("3", "4"), list2)
    }
}