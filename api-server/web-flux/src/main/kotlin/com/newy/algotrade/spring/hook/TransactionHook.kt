package com.newy.algotrade.spring.hook

import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.transaction.reactive.TransactionSynchronization
import org.springframework.transaction.reactive.TransactionSynchronizationManager
import reactor.core.publisher.Mono

@Deprecated("Facade Service 패턴 사용으로 인한 미사용 처리")
suspend fun useTransactionHook(
    onAfterCommit: suspend () -> Unit = {},
    onAfterCompletion: suspend (Int) -> Unit = {}
) {
    TransactionSynchronizationManager
        .forCurrentTransaction()
        .onErrorResume {
            // 유닛 테스트 예외 처리 로직
            mono {
                onAfterCommit()
                onAfterCompletion(TransactionSynchronization.STATUS_UNKNOWN)
            }.then(Mono.empty())
        }
        .awaitSingleOrNull()
        ?.registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit(): Mono<Void> = mono {
                onAfterCommit()
                return@mono null
            }

            override fun afterCompletion(status: Int): Mono<Void> = mono {
                onAfterCompletion(status)
                return@mono null
            }
        })
}
