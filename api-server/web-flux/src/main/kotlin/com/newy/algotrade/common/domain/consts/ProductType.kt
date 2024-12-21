package com.newy.algotrade.common.domain.consts

enum class ProductType(val displayName: String) {
    SPOT("현물"),
    SPOT_MARGIN("현물 마진"),
    FUTURE("선물"),
    PERPETUAL_FUTURE("무기한 선물");
}