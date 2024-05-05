package com.newy.algotrade.coroutine_based_application.auth

interface AccessTokenApi<T> {
    suspend fun accessToken(info: T): String
}