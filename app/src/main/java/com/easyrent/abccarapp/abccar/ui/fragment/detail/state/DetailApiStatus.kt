package com.easyrent.abccarapp.abccar.ui.fragment.detail.state

import com.easyrent.abccarapp.abccar.remote.response.GetEditCarInfoResp

sealed class DetailApiStatus {
    class InitCarInfo(
        val resp: GetEditCarInfoResp
    ) : DetailApiStatus()

    class InitMedia(
        val resp: GetEditCarInfoResp
    ) : DetailApiStatus()

    class InitSpec(
        val resp: GetEditCarInfoResp
    ) : DetailApiStatus()

    class InitCert(
        val resp: GetEditCarInfoResp
    ) : DetailApiStatus()

    class InitAsse(
        val resp: GetEditCarInfoResp
    ) : DetailApiStatus()

    object InitFail: DetailApiStatus()
}