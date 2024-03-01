package com.easyrent.abccarapp.abccar.ui.fragment

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.easyrent.abccarapp.abccar.ui.activity.checkLogoutErrorCode
import com.easyrent.abccarapp.abccar.ui.activity.logout
import com.easyrent.abccarapp.abccar.ui.dialog.LoadingDialog
import com.mixpanel.android.mpmetrics.MixpanelAPI

open class BaseFragment : Fragment() {

    private var loadingDialog: LoadingDialog? = null

    fun showLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = LoadingDialog()
        loadingDialog?.show(
            requireActivity().supportFragmentManager,
            "LoadingDialog"
        )
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    fun checkErrorCode(
        errorCode: String,
        message: String
    ) {
        (requireActivity() as AppCompatActivity).checkLogoutErrorCode(
            errorCode, message
        )
    }

    fun logout() {
        (requireActivity() as AppCompatActivity).logout()
    }
    fun initialMixpanel() : MixpanelAPI{
        val trackAutomaticEvents = false
        return MixpanelAPI.getInstance(requireContext(), "9a1e8b23925fedeee330db6b396404d0", trackAutomaticEvents)
    }

}