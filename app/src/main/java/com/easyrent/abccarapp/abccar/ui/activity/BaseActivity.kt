package com.easyrent.abccarapp.abccar.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.easyrent.abccarapp.abccar.manager.AccountInfoManager
import com.easyrent.abccarapp.abccar.source.preferences.PreferencesSourceImp
import com.easyrent.abccarapp.abccar.ui.dialog.LoadingDialog
import com.easyrent.abccarapp.abccar.ui.dialog.WarningDescDialog
import com.mixpanel.android.mpmetrics.MixpanelAPI
import kotlinx.coroutines.launch

/**
 *  BaseActivity
 *   - 處理LoadingDialog顯示
 */

fun AppCompatActivity.checkLogoutErrorCode(
    errorCode: String,
    message: String
) {



    if (
        errorCode == "2970" || // Token不存在
        errorCode == "2960" || // Token逾時
        errorCode == "2910" // OriginalToken 為空
    ) {
        logout()
        WarningDescDialog(
            title = "憑證無效或過期，請重新登入",
            message = "$errorCode / $message",
        )
    } else {

        when(errorCode) {
            "-1" -> { // 裝置網路失效或 404 Not Found
                WarningDescDialog(
                    title = "錯誤",
                    message = "請檢查您的裝置是否連上網路",
                ).show(supportFragmentManager, "WarningDescDialog")
            }
            else -> {
                WarningDescDialog(
                    title = "錯誤",
                    message = "$errorCode / $message",
                ).show(supportFragmentManager, "WarningDescDialog")
            }
        }
    }
}

fun AppCompatActivity.logout() {
    val preferences = PreferencesSourceImp(applicationContext)
    AccountInfoManager.clear()

    lifecycleScope.launch {
        preferences.clearPreference()
    }

    val intent = Intent(this, LoginActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    startActivity(intent)
}

open class BaseActivity : AppCompatActivity() {

    private var loadingDialog: LoadingDialog? = null

    fun initialMixpanel() : MixpanelAPI {
        val trackAutomaticEvents = false
        return MixpanelAPI.getInstance(this, "9a1e8b23925fedeee330db6b396404d0", trackAutomaticEvents)
    }


    fun showLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = LoadingDialog()
        loadingDialog?.show(supportFragmentManager, "LoadingDialog")
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

}