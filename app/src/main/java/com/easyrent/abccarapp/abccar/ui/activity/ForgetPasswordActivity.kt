package com.easyrent.abccarapp.abccar.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.ActivityForgetPasswordBinding
import com.easyrent.abccarapp.abccar.ui.activity.state.LoginMemberState
import com.easyrent.abccarapp.abccar.viewmodel.ForgetPasswordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgetPasswordActivity : BaseActivity() {

    private lateinit var binding : ActivityForgetPasswordBinding
    private var resendCount = 0
    private var apiResend = 0

    val viewModel : ForgetPasswordViewModel by viewModels{
        ForgetPasswordViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initLiveData()

    }


    fun initView(){
        binding.toolbarForgetPassword.setNavigationOnClickListener {
            //回到loginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
            startActivity(intent)
            finish()
        }
        binding.btForgetPassword.setOnClickListener {
            val phoneNumber = binding.etPhoneNum.text.toString().trim()
            if(checkphone(phoneNumber)){
                viewModel.retrivePassword(phoneNumber)
            }
            else{}
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun initLiveData() {
        viewModel.stateLiveData.observe(this) { status ->
            when (status) {
                is LoginMemberState.ForgetPassword -> {
                    if(status.provider.isNotEmpty()){
                        binding.ivResendHint.setImageDrawable(getDrawable(R.drawable.drawable_password_hintpass))
                        Toast.makeText(this, "發送成功", Toast.LENGTH_LONG).show()
                    } else {
                        binding.ivResendHint.setImageDrawable(getDrawable(R.drawable.drawable_password_hintfail))
                        Toast.makeText(this, "發送失敗", Toast.LENGTH_LONG).show()
                    }

                }
                else -> {
                    binding.ivResendHint.setImageDrawable(getDrawable(R.drawable.drawable_password_hintfail))
                    Toast.makeText(this, "發送失敗", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    fun checkphone(phone : String) : Boolean{
        if(resendCount < 1 && apiResend <1 ){
            if(isValidPhoneNumber(phone)){

                apiResend = 60
                binding.llResendHint.visibility = View.VISIBLE

                GlobalScope.launch {
                    while(apiResend >0){
                        //倒數
                        delay(1000L)
                        apiResend -= 1
                        //更新view
                        withContext(Dispatchers.Main){
                            if(apiResend<1)binding.llResendHint.visibility = View.GONE
                            else binding.tvResendHint.text = "如未收到,請稍待 ${apiResend} 秒再試一次"
                        }
                    }
                }
                return true
            }
            else{
                resendCount = 5
                binding.llResendHint.visibility = View.VISIBLE

                GlobalScope.launch {
                    while(resendCount >0){
                        //倒數
                        delay(1000L)
                        resendCount -= 1
                        //更新view
                        withContext(Dispatchers.Main) {
                            if (resendCount < 1) binding.llResendHint.visibility = View.GONE
                            else binding.tvResendHint.text = "請稍待 ${resendCount} 秒再試一次"
                        }
                    }

                }
                Toast.makeText(this,"請確認電話號碼是否正確",Toast.LENGTH_LONG).show()
                return false
            }

        }
        else {
            Toast.makeText(this,"請稍待片刻再發送",Toast.LENGTH_LONG).show()
            return false
        }
    }

    fun isValidPhoneNumber(phoneNumber: String) : Boolean {
        val regex = """(\d{2,3}-?|\(\d{2,3}\))\d{3,4}-?\d{4}|09\d{2}(\d{6}|\-\d{3}\-\d{3})""".toRegex()
        return regex.matches(phoneNumber)
    }
}