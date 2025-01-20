package helpers.spring

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@DataR2dbcTest
@ComponentScan(
    basePackages = ["com.newy.algotrade"],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ["com.newy.algotrade.spring.auth.*"]
        )
    ]
)
open class BaseDataR2dbcTest : RdbTestContainer() {
    @Autowired
    private lateinit var reactiveTransactionManager: ReactiveTransactionManager

    protected fun runTransactional(block: suspend () -> Unit) = runBlocking {
        TransactionalOperator.create(reactiveTransactionManager).executeAndAwait {
            it.setRollbackOnly()
            block()
        }
    }
}