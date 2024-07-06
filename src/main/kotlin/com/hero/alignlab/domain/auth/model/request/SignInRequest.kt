package com.hero.alignlab.domain.auth.model.request

data class SignInRequest(
    val username: String,
    val password: String,
)
