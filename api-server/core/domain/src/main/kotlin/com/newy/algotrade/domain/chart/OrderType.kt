package com.newy.algotrade.domain.chart

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
