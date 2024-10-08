package com.hero.alignlab.domain.common.converter

import com.hero.alignlab.common.encrypt.EncryptData
import com.hero.alignlab.common.encrypt.Encryptor
import jakarta.persistence.AttributeConverter

abstract class AbstractEncryptConverter : AttributeConverter<EncryptData, String> {
    abstract var encryptor: Encryptor

    override fun convertToDatabaseColumn(attribute: EncryptData?): String? {
        return attribute?.encData
    }

    override fun convertToEntityAttribute(dbData: String?): EncryptData? {
        return dbData?.let { encData -> EncryptData(encData) }
    }
}
