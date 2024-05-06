package com.newy.algotrade.coroutine_based_application.auth.port.out

interface GetAccessTokenPort<T> {
    suspend fun accessToken(info: T): String
}