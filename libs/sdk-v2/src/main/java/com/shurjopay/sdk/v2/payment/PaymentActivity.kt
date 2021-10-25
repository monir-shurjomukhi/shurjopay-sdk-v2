package com.shurjopay.sdk.v2.payment

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.shurjopay.sdk.v2.databinding.ActivityPaymentBinding
import com.shurjopay.sdk.v2.model.RequiredData
import com.shurjopay.sdk.v2.model.Token
import com.shurjopay.sdk.v2.networking.ApiClient
import com.shurjopay.sdk.v2.networking.ApiInterface
import com.shurjopay.sdk.v2.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {

  private lateinit var sdkType: String
  private lateinit var data: RequiredData

  private lateinit var binding: ActivityPaymentBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityPaymentBinding.inflate(layoutInflater)
    setContentView(binding.root)


    sdkType = intent.getStringExtra(SDK_TYPE).toString()
    data = intent.getParcelableExtra(DATA)!!
    Log.d(TAG, "onCreate: requiredDataModel = $data")

    getToken()
  }

  private fun getToken() {
    val token = Token(data.username, data.password)

    ApiClient().getApiClient(sdkType)?.create(ApiInterface::class.java)?.getToken(token)
      ?.enqueue(object : Callback<Token> {
        override fun onResponse(call: Call<Token>, response: Response<Token>) {
          Log.d(TAG, "onResponse: ${response.body()}")
        }

        override fun onFailure(call: Call<Token>, t: Throwable) {
          Log.e(TAG, "onFailure: ${t.message}", t)
        }
      })
  }

  companion object {
    private const val TAG = "PaymentActivity"
  }
}