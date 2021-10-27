package com.shurjopay.sdk.v2.payment

import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.shurjopay.sdk.v2.databinding.ActivityPaymentBinding
import com.shurjopay.sdk.v2.model.*
import com.shurjopay.sdk.v2.networking.ApiClient
import com.shurjopay.sdk.v2.networking.ApiInterface
import com.shurjopay.sdk.v2.utils.DATA
import com.shurjopay.sdk.v2.utils.PAYMENT_CANCELLED
import com.shurjopay.sdk.v2.utils.PAYMENT_CANCELLED_BY_USER
import com.shurjopay.sdk.v2.utils.SDK_TYPE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {

  private lateinit var binding: ActivityPaymentBinding

  private lateinit var sdkType: String
  private lateinit var data: RequiredData

  private var tokenResponse: Token? = null
  private var checkoutRequest: CheckoutRequest? = null
  private var checkoutResponse: CheckoutResponse? = null

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
    val token = Token(
      data.username, data.password, null, null, null,
      null, null, null, null
    )

    ApiClient().getApiClient(sdkType)?.create(ApiInterface::class.java)?.getToken(token)
      ?.enqueue(object : Callback<Token> {
        override fun onResponse(call: Call<Token>, response: Response<Token>) {
          Log.d(TAG, "onResponse: ${response.body()}")
          if (response.isSuccessful) {
            if (response.body()?.sp_code == 200) {
              tokenResponse = response.body()
              getExecuteUrl()
            }
          }
        }

        override fun onFailure(call: Call<Token>, t: Throwable) {
          Log.e(TAG, "onFailure: ${t.message}", t)
        }
      })
  }

  private fun getExecuteUrl() {
    checkoutRequest = CheckoutRequest(
      tokenResponse?.token.toString(),
      tokenResponse?.store_id!!,
      data.prefix,
      data.currency,
      "https://www.sandbox.shurjopayment.com/return_url",
      "https://www.sandbox.shurjopayment.com/cancel_url",
      data.amount,
      data.order_id,
      null,
      null,
      data.client_ip,
      data.customer_name,
      data.customer_phone,
      null,
      data.customer_address,
      data.customer_city,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    )

    ApiClient().getApiClient(sdkType)?.create(ApiInterface::class.java)?.checkout(
      "Bearer " + tokenResponse?.token,
      checkoutRequest!!
    )?.enqueue(object : Callback<CheckoutResponse> {
      override fun onResponse(call: Call<CheckoutResponse>, response: Response<CheckoutResponse>) {
        Log.d(TAG, "onResponse: ${response.body()}")
        if (response.isSuccessful) {
          checkoutResponse = response.body()
          setupWebView()
        }
      }

      override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
        Log.e(TAG, "onFailure: ${t.message}", t)
      }
    })
  }

  private fun setupWebView() {
    binding.webView.settings.javaScriptEnabled = true
    binding.webView.settings.loadsImagesAutomatically = true
    binding.webView.settings.domStorageEnabled = true
    binding.webView.loadUrl(checkoutResponse?.checkout_url.toString())
    binding.webView.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        Log.d(TAG, "shouldOverrideUrlLoading: url = $url")

        if (url.contains("cancel_url")) {
          ShurjoPaySDK.listener!!.onFailed(PAYMENT_CANCELLED)
          finish()
        }
        if (url.contains("return_url") && url.contains("order_id")) {
          verifyPayment()
        }

        /*previousUrl.get(0) = currentUrl.get(0)
        currentUrl.get(0) = url
        Log.d(TAG, "shouldOverrideUrlLoading: previousUrl[0] = " + previousUrl.get(0))
        Log.d(TAG, "shouldOverrideUrlLoading: currentUrl[0] = " + currentUrl.get(0))
        if (currentUrl.get(0).contains("return_url.php")) {
          //ShurjoPaySDK.listener.onFailed(SPayConstants.Exception.PAYMENT_CANCELLED_BY_USER);
          //finish();
          if (previousUrl.get(0).contains("cancel=ok")) {
            ShurjoPaySDK.listener!!.onFailed(SPayConstants.Exception.PAYMENT_CANCELLED)
            finish()
          } else {
            //getTransactionInfo()
          }
        } else {
          view.loadUrl(url)
        }*/
        return false
      }

      override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        handler?.proceed()
      }
    }
    binding.webView.webChromeClient = object : WebChromeClient() {
      override fun onProgressChanged(view: WebView?, newProgress: Int) {
        binding.progressBar.progress = newProgress
      }
    }
  }

  private fun verifyPayment() {
    val transactionInfo = TransactionInfo(
      null,
      checkoutResponse?.sp_order_id!!,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    )

    ApiClient().getApiClient(sdkType)?.create(ApiInterface::class.java)?.verify(
      "Bearer " + tokenResponse?.token,
      transactionInfo
    )?.enqueue(object : Callback<List<TransactionInfo>> {
      override fun onResponse(call: Call<List<TransactionInfo>>, response: Response<List<TransactionInfo>>) {
        Log.d(TAG, "onResponse: ${response.body()}")
        if (response.isSuccessful) {
          //checkoutResponse = response.body()
          //setupWebView()

          if (response.body()?.get(0)?.sp_code == 1000) {
            ShurjoPaySDK.listener?.onSuccess(response.body()?.get(0))
            finish()
          } else {
            ShurjoPaySDK.listener?.onFailed(response.body()?.get(0)?.sp_massage)
            finish()
          }
        }
      }

      override fun onFailure(call: Call<List<TransactionInfo>>, t: Throwable) {
        Log.e(TAG, "onFailure: ${t.message}", t)
      }
    })
  }

  override fun onBackPressed() {
    ShurjoPaySDK.listener!!.onFailed(PAYMENT_CANCELLED_BY_USER)
    super.onBackPressed()
  }

  companion object {
    private const val TAG = "PaymentActivity"
  }
}
