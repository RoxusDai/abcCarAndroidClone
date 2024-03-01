package com.easyrent.abccarapp.abccar.remote.request

import com.google.gson.annotations.SerializedName

data class BatchUpdateCarAdvertisingBoardReq(
    @SerializedName("MemberID")var MemberID: Int,
    @SerializedName("ActionStatus")val ActionStatus:Int,
    @SerializedName("Status")val  status :Int,
    @SerializedName("CarID")val carID: List<Int>,
    @SerializedName("PullReason")val PullReason:Int,
    @SerializedName("ZoneReason")val  ZoneReason:Int,
    @SerializedName("OnlineDescription")val OnlineDescription : Int
)