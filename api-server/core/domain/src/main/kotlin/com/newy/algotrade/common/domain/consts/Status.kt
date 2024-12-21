package com.newy.algotrade.common.domain.consts

enum class SendNotificationLogStatus(displayName: String) {
    REQUESTED("요청완료"),
    PROCESSING("처리중"),
    SUCCEED("전송완료"),
    FAILED("전송실패");
}