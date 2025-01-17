package com.newy.algotrade.domain.common.consts

enum class ProductType(val displayName: String) {
    SPOT("현물"),
    SPOT_MARGIN("현물 마진"),
    FUTURE("선물"),
    PERPETUAL_FUTURE("무기한 선물");
}