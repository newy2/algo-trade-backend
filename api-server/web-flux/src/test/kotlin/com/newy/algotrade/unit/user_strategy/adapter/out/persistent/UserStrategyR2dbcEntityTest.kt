package com.newy.algotrade.unit.user_strategy.adapter.out.persistent

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.user_strategy.SetUserStrategy
import com.newy.algotrade.domain.user_strategy.SetUserStrategyKey
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.UserStrategyR2dbcEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UserStrategyR2dbcEntityTest {
    private val persistenceEntity = UserStrategyR2dbcEntity(
        id = 0,
        marketAccountId = 1,
        strategyId = 2,
        productType = "SPOT",
        productCategory = "USER_PICK",
        timeFrame = "M1",
    )

    @Test
    fun fromDomainModel() {
        val domainEntity = SetUserStrategy(
            setUserStrategyKey = SetUserStrategyKey(
                marketServerAccountId = 1,
                strategyClassName = "strategyClassName",
                productType = ProductType.SPOT
            ),
            productCategory = ProductCategory.USER_PICK,
            timeFrame = Candle.TimeFrame.M1,
        )

        Assertions.assertEquals(persistenceEntity, UserStrategyR2dbcEntity(domainEntity, strategyId = 2))
    }
}