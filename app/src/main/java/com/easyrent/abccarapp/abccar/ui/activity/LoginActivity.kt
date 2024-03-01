package com.easyrent.abccarapp.abccar.ui.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.easyrent.abccarapp.abccar.databinding.ActivityLoginBinding
import com.easyrent.abccarapp.abccar.ui.activity.state.LoginMemberState
import com.easyrent.abccarapp.abccar.viewmodel.LoginViewModel
import org.json.JSONObject


/**
 *  登入畫面
 *   - 登入
 *   - 忘記密碼 ( 預計 )
 *   - 初始化Token
 *   - 檢查Token
 *   - 刷新Token
 */
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initLiveDate()
    }


    private fun initLiveDate() {
        viewModel.stateLiveData.observe(this) { state ->
            if (state !is LoginMemberState.Login) {
                hideLoadingDialog()
            }

            when(state) {
                is LoginMemberState.Login -> {
                    val mixpanel = initialMixpanel()
                    val properties = JSONObject()
                    mixpanel.track("login_success", properties.put("memberId",state.id))//正常登入

                    startToMainActivity(state.id)
                }

                is LoginMemberState.Error -> {
                    val mixpanel = initialMixpanel()
                    mixpanel.track("login_failed")//登入失敗
                    checkLogoutErrorCode(
                        errorCode = state.errorCode,
                        message = state.message
                    )
                }
                LoginMemberState.Loading -> {
                    showLoadingDialog()
                }

                else -> {}
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun initView() {
        binding.btLogin.setOnClickListener {
            viewModel.login(
                binding.etAccountInput.text.toString(),
                binding.etPassInput.text.toString()
            )
        }
        binding.tvForgetPassword.setOnClickListener {
            //開啟忘記密碼的Activity
            val mixpanel = initialMixpanel()
            mixpanel.track("forgetPassword_button_tapped")
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }
    }


    private fun startToMainActivity(id:Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("memberId",id)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        viewModel.initToken()
    }
}