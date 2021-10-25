 package com.shurjopay.sdk.v2.payment

import com.shurjopay.sdk.v2.utils.PermissionsManager.doesUserHaveNetworkStatePermission
import com.shurjopay.sdk.v2.utils.PermissionsManager.doesUserHaveInternetPermission
import com.shurjopay.sdk.v2.utils.NetworkManager.isInternetAvailable
import android.app.Activity
import com.shurjopay.sdk.v2.model.RequiredData
import android.widget.Toast
import android.text.TextUtils
import android.content.Intent
import com.shurjopay.sdk.v2.utils.*
import kotlin.jvm.Synchronized

class ShurjoPaySDK private constructor() {

  fun makePayment(
    activity: Activity?, sdkType: String?, data: RequiredData?,
    resultListener: PaymentResultListener?
  ) {
    if (resultListener == null) {
      Toast.makeText(activity, "Listener is null!", Toast.LENGTH_SHORT).show()
      return
    }
    listener = resultListener
    if (activity == null) {
      listener!!.onFailed(USER_INPUT_ERROR)
      return
    }
    if (data == null) {
      listener!!.onFailed(USER_INPUT_ERROR)
      return
    }
    if (TextUtils.isEmpty(sdkType)) {
      listener!!.onFailed(USER_INPUT_ERROR)
      return
    }

    // Check minimum and maximum amount check
    if (data.totalAmount <= 0) {
      listener!!.onFailed(INVALID_AMOUNT)
      return
    }

    // Network state permission check
    if (!doesUserHaveNetworkStatePermission(activity)) {
      listener!!.onFailed(NO_NETWORK_STATE_PERMISSION)
      return
    }

    // Internet permission check
    if (!doesUserHaveInternetPermission(activity)) {
      listener!!.onFailed(NO_INTERNET_PERMISSION)
      return
    }

    // check is internet is available
    if (!isInternetAvailable(activity)) {
      listener!!.onFailed(NO_INTERNET_MESSAGE)
      return
    }
    val intent = Intent(activity, PaymentActivity::class.java)
    intent.putExtra(DATA, data)
    intent.putExtra(SDK_TYPE, sdkType)
    activity.startActivity(intent)
  }

  companion object {
    private var mInstance: ShurjoPaySDK? = ShurjoPaySDK()
    var listener: PaymentResultListener? = null

    /**
     * Get a singleton instance for payment
     *
     * @return a singleton instance
     */
    @get:Synchronized
    val instance: ShurjoPaySDK?
      get() {
        if (mInstance == null) {
          mInstance = ShurjoPaySDK()
        }
        return mInstance
      }
  }
}