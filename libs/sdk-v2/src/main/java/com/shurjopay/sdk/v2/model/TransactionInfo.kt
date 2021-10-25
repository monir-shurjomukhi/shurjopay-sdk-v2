package com.shurjopay.sdk.v2.model

data class TransactionInfo(
  var txID: String?,
  var bankTxID: String,
  var bankTxStatus: String,
  var txnAmount: Double?,
  var spCode: String?,
  var gateWay: String,
  var method: String,
  var requestTime: String,
  var paymentTime: String,
  var cardHolderName: String,
  var cardNumber: String?
)