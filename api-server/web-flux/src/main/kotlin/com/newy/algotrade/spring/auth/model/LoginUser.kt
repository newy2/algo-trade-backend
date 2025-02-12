package com.newy.algotrade.spring.auth.model

data class LoginUser(
    val id: Long,
    val role: UserRole = UserRole.GUEST
) {
    fun isAdminUser() = role == UserRole.ADMIN
}

enum class UserRole {
    ADMIN,
    GUEST
}