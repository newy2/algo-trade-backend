package com.newy.algotrade.common.error

data class ErrorResponse(
    val message: String,
    val errors: List<FieldError> = emptyList()
)

data class FieldError(
    val field: String,
    val value: String,
    val reason: String
)