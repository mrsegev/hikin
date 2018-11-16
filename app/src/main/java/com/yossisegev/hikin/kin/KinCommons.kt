package com.yossisegev.hikin.kin

import kin.core.*
import java.math.BigDecimal

class KinCommons(kinAccount: KinAccount) {

    private val tag = "KinCommons"

    private var kinAccount: KinAccount = kinAccount

    fun getBalance(onSuccess: (Balance) -> (Unit), onFailure: ((java.lang.Exception) -> (Unit))? = null) {

        kinAccount.balance.run(object : ResultCallback<Balance> {
            override fun onResult(result: Balance?) {
                result?.let {
                    onSuccess(it)
                }.run {
                    onFailure?.invoke(Exception("Balance returned null"))
                }
            }

            override fun onError(e: java.lang.Exception?) {
                e.let {
                    onFailure?.invoke(Exception(it))
                }.run {
                    onFailure?.invoke(Exception("Unknown error"))
                }
            }
        })
    }


    fun buildTransaction(
        recipientPublicAddress: String,
        amount: Double,
        memo: String? = null,
        onSuccess: (Transaction) -> (Unit),
        onFailure: ((java.lang.Exception) -> (Unit))? = null
    ) {
        val transaction = kinAccount.buildTransaction(recipientPublicAddress, BigDecimal(amount.toString()), memo)
        transaction.run(object : ResultCallback<Transaction> {
            override fun onResult(result: Transaction?) {
                result?.let(onSuccess).run {
                    onFailure?.invoke(Exception("Result transaction is null"))
                }

            }

            override fun onError(e: java.lang.Exception?) {
                e.let { onFailure?.invoke(Exception(it)) }
                    .run { onFailure?.invoke(Exception("Unknown error")) }
            }

        })
    }

    fun sendTransaction(
        transaction: Transaction, onSuccess: (TransactionId) -> (Unit),
        onFailure: ((java.lang.Exception) -> (Unit))? = null
    ) {
        val sendTransactionRequest = kinAccount.sendTransaction(transaction)
        sendTransactionRequest.run(object : ResultCallback<TransactionId> {
            override fun onResult(result: TransactionId?) {
                result?.let(onSuccess).run {
                    onFailure?.invoke(Exception("TransactionId returned null"))
                }
            }

            override fun onError(e: java.lang.Exception?) {
                e.let { onFailure?.invoke(Exception(it)) }
                    .run { onFailure?.invoke(Exception("Unknown error")) }
            }

        })
    }

    fun sendKin(
        recipientPublicAddress: String,
        amount: Double,
        memo: String? = null,
        onSuccess: (TransactionId) -> (Unit),
        onFailure: ((java.lang.Exception) -> (Unit))? = null
    ) {

        buildTransaction(recipientPublicAddress, amount, memo, {
            val sendTransactionRequest = kinAccount.sendTransaction(it)
            sendTransactionRequest.run(object : ResultCallback<TransactionId> {
                override fun onResult(result: TransactionId?) {
                    result?.let(onSuccess).run {
                        onFailure?.invoke(Exception("TransactionId returned null"))
                    }
                }

                override fun onError(e: java.lang.Exception?) {
                    e.let { onFailure?.invoke(Exception(it)) }
                        .run { onFailure?.invoke(Exception("Unknown error")) }
                }

            })

        }, {
            onFailure?.invoke(Exception(it))
        })
    }

}