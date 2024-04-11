package com.newy.algotrade.domain.chart

import java.math.BigDecimal

data class Order(val type: OrderType, val price: BigDecimal, val quantity: Double)
