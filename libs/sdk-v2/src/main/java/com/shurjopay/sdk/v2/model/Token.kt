package com.shurjopay.sdk.v2.model


data class Token(
    var username: String?,
    var password: String?,
) {
    constructor(
        username: String,
        password: String,
        token: String,
        store_id: Int,
        execute_url: String,
        token_type: String,
        sp_code: Int,
        massage: String,
        expires_in: String
    ) : this(username, password)
}
