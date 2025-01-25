package com.newy.algotrade.common.exception

class HttpResponseException(val responseMessage: String) : RuntimeException(responseMessage)