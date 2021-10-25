package com.shurjopay.sdk.v2.networking

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
  private var retrofit: Retrofit? = null

  fun getApiClient(baseUrl: String): Retrofit? {
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

      retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()
    }
    return retrofit
  }
}
