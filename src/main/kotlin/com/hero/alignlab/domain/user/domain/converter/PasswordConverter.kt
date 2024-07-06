package com.hero.alignlab.domain.user.domain.converter

import com.hero.alignlab.common.encrypt.Encryptor
import com.hero.alignlab.domain.common.converter.AbstractEncryptConverter
import jakarta.persistence.Convert

@Convert
class PasswordConverter(
    override var encryptor: Encryptor
) : AbstractEncryptConverter()
