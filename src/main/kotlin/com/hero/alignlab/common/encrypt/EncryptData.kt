package com.hero.alignlab.common.encrypt

data class EncryptData(
    val encData: String,
) {
    fun dec(encryptor: Encryptor): String {
        return encryptor.decrypt(this.encData)
    }

    companion object {
        fun enc(plainData: String, encryptor: Encryptor): EncryptData {
            return EncryptData(
                encData = encryptor.encrypt(plainData)
            )
        }
    }
}
