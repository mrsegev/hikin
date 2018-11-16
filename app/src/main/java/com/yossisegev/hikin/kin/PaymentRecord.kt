package com.yossisegev.hikin.kin

/**
 * Created by Yossi Segev on 16/11/2018.
 */
data class PaymentRecord(
    val id: String,
    val from: String,
    val to: String,
    val amount: String,
    val assetCode: String,
    val type: String,
    val pagingToken: String,
    val createdAt: String,
    val hash: String
)