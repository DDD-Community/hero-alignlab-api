package com.hero.alignlab.ws.model

data class WsGroupUserModel(
    val groupId: Long,
    val uids: Set<Long>
)
