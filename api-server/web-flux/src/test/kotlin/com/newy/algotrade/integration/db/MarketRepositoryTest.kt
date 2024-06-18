package com.newy.algotrade.integration.db

import com.newy.algotrade.web_flux.price.Market
import com.newy.algotrade.web_flux.price.MarketRepository
import helpers.BaseDbTest
import helpers.TestConfig
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@DataR2dbcTest
@ContextConfiguration(classes = [TestConfig::class])
open class MarketRepositoryTest(
    @Autowired private val marketRepository: MarketRepository,
) : BaseDbTest() {
    @Test
    fun `롤벡 테스트`() = runTransactional {
        val saved = marketRepository.save(
            Market(
                id = 0,
                nameKo = "테스트",
                nameEn = "Test"
            )
        )
        val count = marketRepository.findAll().count()
        assertEquals(5, count)
    }

    @Test
    fun `데이터 조회 테스트`() = runBlocking {
        val count = marketRepository.findAll().count()
        assertEquals(4, count)
    }
}