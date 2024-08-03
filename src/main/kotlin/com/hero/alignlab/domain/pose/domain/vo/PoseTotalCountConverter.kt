package com.hero.alignlab.domain.pose.domain.vo

import com.fasterxml.jackson.module.kotlin.readValue
import com.hero.alignlab.common.extension.mapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class PoseTotalCountConverter : AttributeConverter<PoseTotalCount, String> {
    override fun convertToDatabaseColumn(attribute: PoseTotalCount): String {
        return mapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String): PoseTotalCount {
        return mapper.readValue(dbData)
    }
}
