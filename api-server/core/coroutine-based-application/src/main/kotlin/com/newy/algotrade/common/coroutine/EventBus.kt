package com.newy.algotrade.common.coroutine

import com.newy.algotrade.domain.common.annotation.ForTesting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

// TODO CoroutineEventBus 로 변경할까?
open class EventBus<T> {
    private val events = MutableSharedFlow<T>()

    open suspend fun publishEvent(event: T) {
        // TODO 코루틴 스코프에서 호출할까?
        events.emit(event)
    }

    fun addListener(
        @ForTesting coroutineContext: CoroutineContext = Dispatchers.IO,
        callback: suspend (T) -> Unit
    ) = CoroutineScope(coroutineContext).launch {
        events.asSharedFlow().collect(callback)
    }
}