package com.hero.alignlab.domain.group.model.request

data class UpdateGroupRequest(
    /** 그룹명, 중복 불가능 */
    val name: String,
    /** 그룹 설명 */
    val description: String?,
    /** 숨김 여부 */
    val isHidden: Boolean,
    /** 참여 코드, 미입력시 자동 생성 */
    val joinCode: String?,
    /** 정원, 기본값 30 */
    val userCapacity: Int?,
    /** 수정할 태그 리스트 */
    val tagNames: List<String>?,
)
