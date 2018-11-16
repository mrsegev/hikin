package com.yossisegev.hikin.kin

import android.content.Context
import android.util.Log
import kin.core.Environment
import kin.core.KinAccount
import kin.core.KinClient
import okhttp3.*
import java.io.IOException

class KinAccountManager(private val context: Context, val applicationId: String) {

    private val tag = "KinAccountManager"
    private lateinit var account: KinAccount
    private lateinit var onSuccess: (KinAccount) -> Unit
    private var onFailure: ((java.lang.Exception) -> Unit)? = null

    fun getAccountOrCreate(onSuccess: (KinAccount) -> (Unit), onFailure: ((java.lang.Exception) -> (Unit))? = null) {
        this.onSuccess = onSuccess
        this.onFailure = onFailure
        val client = KinClient(context.applicationContext, Environment.TEST, applicationId)

        if (!client.hasAccount()) {
            Log.d(tag, "Creating new account locally")
            account = client.addAccount()
            xlmForAccount(account.publicAddress!!)
        } else {
            Log.d(tag, "Using existing account")
            account = client.getAccount(0)
            Log.i(tag, "Your public address: " + account.getPublicAddress())
            onSuccess(account)
        }
    }

    fun createAccount() {

    }
    private fun xlmForAccount(publicAddress: String) {
        KinPlaygroundTools.xlmForAccount(publicAddress, {
            activateAccount(account)
        }, {
            Log.e(tag, it.localizedMessage)
            onFailure?.invoke(it)
        })
    }

    private fun activateAccount(account: KinAccount) {
        Log.d(tag, "Activating blockchain account")
        try {
            account.activateSync()
            Log.d(tag, "Account activated.")
            Log.i(tag, "Your public address: " + account.getPublicAddress())
            onSuccess(account)
        } catch (e: Exception) {
            Log.e(tag, e.localizedMessage)
            onFailure?.invoke(e)
        }
    }
}