package com.hero.alignlab.client.kakao.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoOAuthUserInfoResponse(
    val id: String,
    val hasSignedUp: Boolean?,
    val connectedAt: LocalDateTime?,
    val synchedAt: LocalDateTime?,
    val kakaoAccount: KakaoAccount?,
) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class KakaoAccount(
        val profileNeedsAgreement: Boolean?,
        val profile: Profile?,
        val nameNeedsAgreement: Boolean?,
        val name: String?,
        val emailNeedsAgreement: Boolean?,
        val isEmailValid: Boolean?,
        val isEmailVerified: Boolean?,
        val email: String?,
        val ageRangeNeedsAgreement: Boolean?,
        val ageRange: String?,
        val birthyearNeedsAgreement: Boolean?,
        val birthyear: String?,
        val birthdayNeedsAgreement: Boolean?,
        val birthday: String?,
        val birthdayType: String?,
        val genderNeedsAgreement: Boolean?,
        val gender: String?,
        val phoneNumberNeedsAgreement: Boolean?,
        val phoneNumber: String?,
        val ciNeedsAgreement: Boolean?,
        val ci: String?,
        val ciAuthenticatedAt: LocalDateTime?,
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class Profile(
        val nickname: String?,
        val thumbnailImageUrl: String?,
        val profileImageUrl: String?,
        val isDefaultImage: Boolean?,
        val isDefaultNickname: Boolean?,
    )
}
