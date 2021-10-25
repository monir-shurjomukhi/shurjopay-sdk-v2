package com.shurjopay.sdk

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shurjopay.sdk.v2.model.RequiredData
import com.shurjopay.sdk.v2.model.TransactionInfo
import com.shurjopay.sdk.v2.payment.PaymentResultListener
import com.shurjopay.sdk.v2.payment.ShurjoPaySDK
import com.shurjopay.sdk.v2.utils.SDK_TYPE_SANDBOX
import java.util.*

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

      val data = RequiredData(
          "sp_sandbox",
          "pyyk97hu&6u6",
          100.0
      )

      ShurjoPaySDK.instance?.makePayment(
          this,
          SDK_TYPE_SANDBOX,
          data,
          object : PaymentResultListener {
              override fun onSuccess(transactionInfo: TransactionInfo?) {
                  Log.d(TAG, "onSuccess: transactionInfo = $transactionInfo")
                  Toast.makeText(
                      this@MainActivity, "onSuccess: transactionInfo = " +
                          transactionInfo, Toast.LENGTH_SHORT
                  ).show()
              }

              override fun onFailed(message: String?) {
                  Log.d(TAG, "onFailed: message = $message")
                  Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
              }
          })
  }

  companion object {
    private const val TAG = "MainActivity"
  }
}
