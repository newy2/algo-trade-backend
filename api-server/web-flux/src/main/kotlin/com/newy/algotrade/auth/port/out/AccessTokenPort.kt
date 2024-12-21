package com.newy.algotrade.auth.port.out

interface AccessTokenPort<T>
    : FindAccessTokenPort<T>

fun interface FindAccessTokenPort<T> {
    suspend fun findAccessToken(info: T): String
}