package com.hero.alignlab.domain.group.model.response

import com.hero.alignlab.domain.group.domain.Group

data class MyGroupResponse(
    /** group id */
    val id: Long,
    /** 그룹명 */
    val name: String,
    /** 그룹 설명 */
    val description: String?,
    /** owner uid */
    val ownerUid: Long,
    /** 숨김 여부 */
    val isHidden: Boolean,
    /** 참여 코드, 미입력시 자동 생성 */
    val joinCode: String?,
    /** 그룹원 수 */
    val userCount: Int,
    /** 그룹 정원 */
    val userCapacity: Int,
    /** 그룹장 명 */
    val ownerNickname: String,
    /** 태그 리스트, 최대 3개 */
    val tagNames: List<String>?,
) {
    companion object {
        fun of(group: Group, userCount: Int, ownerNickname: String, tagNames: List<String>?): MyGroupResponse {
            return MyGroupResponse(
                id = group.id,
                name = group.name,
                description = group.description,
                ownerUid = group.ownerUid,
                isHidden = group.isHidden,
                joinCode = group.joinCode,
                userCount = userCount,
                userCapacity = group.userCapacity,
                ownerNickname = ownerNickname,
                tagNames = tagNames ?: emptyList(),
            )
        }
    }
}
