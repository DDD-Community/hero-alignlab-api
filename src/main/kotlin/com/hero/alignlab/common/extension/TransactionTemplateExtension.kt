package com.hero.alignlab.common.extension

import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.FailToExecuteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate

suspend fun <RETURN> TransactionTemplate.coExecute(
    actions: TransactionCallback<RETURN>,
): RETURN {
    val transactionTemplate: TransactionTemplate = this

    return withContext(Dispatchers.IO) {
        transactionTemplate.execute(actions)
    } ?: throw FailToExecuteException(ErrorCode.FAIL_TO_TRANSACTION_TEMPLATE_EXECUTE_ERROR)
}

suspend fun <RETURN> TransactionTemplate.coExecuteOrNull(
    actions: TransactionCallback<RETURN>,
): RETURN? {
    val transactionTemplate: TransactionTemplate = this

    return withContext(Dispatchers.IO) {
        transactionTemplate.execute(actions)
    }
}
