package com.yossisegev.hikin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.yossisegev.hikin.kin.KinAccountManager
import com.yossisegev.hikin.kin.KinCommons


// Account: GBDEG4WYSYKH4X5KK44MQNBONSWYNSAWAKP4RC42QN7AQ3GQTU73BGUM
// Secret: SD342BCD6A7IFRE2QEC3KOVDS2E3VKFQUBJXDYYZSKV7KOBJI4HEZ4XW

class MainActivity : AppCompatActivity() {
    val recipient = "GCK3G6DOVTA7S4ABLV6PPRZWGEXHTGF6QPRIIL6CLA6QU34YCF7MMKQC"
    val TAG = "HiKinMainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val accountManager = KinAccountManager(this, "YO3S")
        accountManager.getAccountOrCreate({
            Log.d(TAG, it.publicAddress)
            val commons = KinCommons(it)

            commons.getBalance({
                Log.d(TAG, it.value().toPlainString())
                commons.sendKin(recipient, 3.5, "testing 123", {
                    Log.d(TAG, "TxId: " + it.id())

                    commons.getBalance({
                        Log.d(TAG, it.value().toPlainString())
                    })
                })
            })
        })
    }
}
