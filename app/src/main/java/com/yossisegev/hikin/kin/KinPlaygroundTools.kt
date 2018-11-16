package com.yossisegev.hikin.kin

import android.util.Log
import okhttp3.*
import java.io.IOException

/**
 * Created by Yossi Segev on 16/11/2018.
 */
class KinPlaygroundTools {

    companion object {
        private val TAG = "KinPlaygroundTools"
        private val okHttpClient = OkHttpClient.Builder().build()

        fun fundAccountWithKin(
            publicAddress: String,
            amount: Int,
            onSuccess: () -> (Unit),
            onFailure: ((java.lang.Exception) -> (Unit))? = null
        ) {
            okHttpClient.newCall(
                okhttp3.Request.Builder()
                    .url("http://faucet-playground.kininfrastructure.com/fund?account=" + publicAddress + "&amount=" + amount)
                    .get()
                    .build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, e.message)
                    onFailure?.invoke(e)

                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d(TAG, "Funding response:" + response)
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure?.invoke(Exception(response.message()))
                    }
                }
            })
        }

        fun xlmForAccount(
            publicAddress: String, onSuccess: () -> (Unit),
            onFailure: ((java.lang.Exception) -> (Unit))? = null
        ) {
            Log.d(TAG, "Creating new account on the blockchain")
            val request = Request.Builder()
                .url("http://friendbot-playground.kininfrastructure.com/?addr=" + publicAddress)
                .get()
                .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, e.localizedMessage)
                    onFailure?.invoke(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d(TAG, "Response: " + response.message())
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure?.invoke(Exception(response.message()))
                    }
                }

            })
        }
    }

}