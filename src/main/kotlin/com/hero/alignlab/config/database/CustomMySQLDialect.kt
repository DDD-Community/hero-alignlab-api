package com.hero.alignlab.config.database

import org.hibernate.dialect.MySQLDialect

class CustomMySQLDialect : MySQLDialect() {
    override fun getTableTypeString(): String {
        return "${super.getTableTypeString()} charset = utf8mb4"
    }
}
