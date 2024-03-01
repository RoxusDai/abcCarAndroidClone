package com.easyrent.abccarapp.abccar.remote.request

import com.google.gson.annotations.SerializedName

class EditPasswordReq (
    @SerializedName("OldPassword") val oldPassword: String,
    @SerializedName("NewPassword") val newPassword: String
)