package com.shurjopay.sdk.v2.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class RequiredData(
  var username: String,
  var password: String,
  var totalAmount: Double
) : Parcelable
