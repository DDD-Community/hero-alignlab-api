package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.Group
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GroupQRepository {
    fun findByTagNameAndPage(tagName: String?, pageable: Pageable): Page<Group>
}