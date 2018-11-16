package com.yossisegev.hikin.kin

import android.os.Handler
import android.os.Looper
import android.util.Log
import kin.core.*
import okhttp3.*
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal

class KinCommons(private var kinAccount: KinAccount) {

    private val TAG = "KinCommons"
    private val okHttpClient = OkHttpClient.Builder().build()
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    fun getBalance(onSuccess: (Balance) -> (Unit), onFailure: ((java.lang.Exception) -> (Unit))? = null) {

        kinAccount.balance.run(object : ResultCallback<Balance> {
            override fun onResult(result: Balance?) {
                result?.let {
                    mainThreadHandler.post { onSuccess(it) }
                }.run {
                    mainThreadHandler.post { onFailure?.invoke(Exception("Balance returned null")) }
                }
            }

            override fun onError(e: java.lang.Exception?) {
                e.let {
                    mainThreadHandler.post { onFailure?.invoke(Exception(it)) }
                }.run {
                    mainThreadHandler.post { onFailure?.invoke(Exception("Unknown error")) }
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
                result?.let {
                    mainThreadHandler.post { onSuccess(it) }
                }.run {
                    mainThreadHandler.post { onFailure?.invoke(Exception("Result transaction is null")) }
                }
            }

            override fun onError(e: java.lang.Exception?) {
                e.let {
                    mainThreadHandler.post { onFailure?.invoke(Exception(it)) }
                }.run {
                    mainThreadHandler.post { onFailure?.invoke(Exception("Unknown error")) }
                }
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
                result?.let {
                    mainThreadHandler.post { onSuccess(it) }
                }.run {
                    onFailure?.invoke(Exception("TransactionId returned null"))
                }
            }

            override fun onError(e: java.lang.Exception?) {
                e.let {
                    mainThreadHandler.post { onFailure?.invoke(Exception(it)) }
                }.run {
                    mainThreadHandler.post { onFailure?.invoke(Exception("Unknown error")) }
                }
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
                    result?.let {
                        mainThreadHandler.post { onSuccess(it) }
                    }.run {
                        mainThreadHandler.post { onFailure?.invoke(Exception("TransactionId returned null")) }
                    }
                }

                override fun onError(e: java.lang.Exception?) {
                    e.let {
                        mainThreadHandler.post { onFailure?.invoke(Exception(it)) }
                    }.run {
                        mainThreadHandler.post { onFailure?.invoke(Exception("Unknown error")) }
                    }
                }

            })

        },
            {
                mainThreadHandler.post { onFailure?.invoke(Exception(it)) }
            })
    }

    fun getPayments(
        onSuccess: (List<PaymentRecord>) -> (Unit),
        onFailure: ((java.lang.Exception) -> (Unit))? = null
    ) {

        val request = Request.Builder()
            .url("https://horizon-playground.kininfrastructure.com/accounts/" + kinAccount.publicAddress + "/payments?order=desc")
            .get()
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, e.localizedMessage)
                mainThreadHandler.post { onFailure?.invoke(e) }
            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful) {
                    val payments = mutableListOf<PaymentRecord>()
                    val jsonResponse = JSONObject(response.body()!!.string())
                    val embeddedJson = jsonResponse.getJSONObject("_embedded")
                    val paymentsArray = embeddedJson.getJSONArray("records")
                    for (i in 0 until paymentsArray.length()) {
                        val json = paymentsArray.getJSONObject(i)
                        if (json.getString("type") == "payment") {
                            val amount = json.getString("amount")
                            val to = json.getString("to")
                            val from = json.getString("from")
                            val assetCode = json.getString("asset_code")
                            val createdAt = json.getString("created_at")
                            val id = json.getString("id")
                            val hash = json.getString("transaction_hash")
                            val pagingToken = json.getString("paging_token")

                            payments.add(
                                PaymentRecord(
                                    from = from,
                                    to = to,
                                    amount = amount,
                                    assetCode = assetCode,
                                    createdAt = createdAt,
                                    pagingToken = pagingToken,
                                    id = id,
                                    hash = hash,
                                    type = "payment"
                                )
                            )
                        }
                    }
                    mainThreadHandler.post { onSuccess(payments) }
                } else {
                    mainThreadHandler.post { onFailure?.invoke(Exception(response.message())) }
                }
            }

        })
    }


}

