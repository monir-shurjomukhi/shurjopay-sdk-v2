package com.shurjopay.sdk.v2.networking

import com.shurjopay.sdk.v2.model.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

  //////////////////// POST ///////////////////

  @POST("get_token")
  fun getToken(
    @Body token: Token
  ): Call<Token>
}