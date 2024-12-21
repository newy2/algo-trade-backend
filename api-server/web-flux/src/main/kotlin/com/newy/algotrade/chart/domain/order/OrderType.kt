package com.newy.algotrade.chart.domain.order

enum class OrderType {
    BUY {
        override fun completedType() = SELL
    },
    SELL {
        override fun completedType() = BUY
    },
    NONE {
        override fun completedType() = NONE
    };

    abstract fun completedType(): OrderType
}
