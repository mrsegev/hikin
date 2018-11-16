package com.yossisegev.hikin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.yossisegev.hikin.kin.KinAccountManager
import com.yossisegev.hikin.kin.KinCommons
import com.yossisegev.hikin.kin.PaymentsAdapter

/**
 * Created by Yossi Segev on 16/11/2018.
 */
class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val accountManager = KinAccountManager(activity!!, "YO3S")

        accountManager.getAccountOrCreate({ it ->
            val commons = KinCommons(it)
            commons.getPayments({

            })
            commons.getBalance({ balance ->
                view.findViewById<TextView>(R.id.balance).text = balance.value(2)
            })

            commons.getPayments({payments ->
                val recyclerView = view.findViewById<RecyclerView>(R.id.payments)
                val adapter = PaymentsAdapter()
                recyclerView.setAdapter(adapter)
                recyclerView.layoutManager = LinearLayoutManager(activity!!)
                adapter.add(payments)

            })
        })
    }
}