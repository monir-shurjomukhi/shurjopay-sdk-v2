package com.shurjopay.sdk.v2.networking

import com.google.gson.GsonBuilder
import com.shurjopay.sdk.v2.utils.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
  private var retrofit: Retrofit? = null

  fun getApiClient(sdkType: String): Retrofit? {
    if (retrofit == null) {
      val interceptor = HttpLoggingInterceptor()
      interceptor.level = HttpLoggingInterceptor.Level.BODY

      val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .build()

      val gson = GsonBuilder().setLenient().create()

      var baseUrl = BASE_URL_SANDBOX
      when {
        sdkType.equals(SDK_TYPE_SANDBOX, ignoreCase = true) -> {
          baseUrl = BASE_URL_SANDBOX
        }
        sdkType.equals(SDK_TYPE_LIVE, ignoreCase = true) -> {
          baseUrl = BASE_URL_LIVE
        }
        sdkType.equals(SDK_TYPE_IPN_SANDBOX, ignoreCase = true) -> {
          baseUrl = BASE_URL_IPN_SANDBOX
        }
        sdkType.equals(SDK_TYPE_IPN_LIVE, ignoreCase = true) -> {
          baseUrl = BASE_URL_IPN_LIVE
        }
      }

      retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()
    }
    return retrofit
  }
}
