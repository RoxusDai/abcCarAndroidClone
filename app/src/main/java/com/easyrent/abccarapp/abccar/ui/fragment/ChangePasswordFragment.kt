package com.easyrent.abccarapp.abccar.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.easyrent.abccarapp.abccar.databinding.FragmentChangePasswordBinding
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.EditPasswordReq
import com.easyrent.abccarapp.abccar.viewmodel.ChangePasswordViewModel
import org.json.JSONObject

class ChangePasswordFragment : BaseFragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChangePasswordViewModel by viewModels {
        ChangePasswordViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        initView()
        initLiveData()

        return binding.root
    }

    private fun initLiveData() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Success -> {
//                    //取得authtoken,更新token
//                    val origintoken = it.data.originalToken
//                    val authtoken = it.data.authorizationToken
                    Toast.makeText(requireContext(),it.data.message, Toast.LENGTH_LONG).show()
                }
                is ApiResponse.ApiFailure->{
                    Toast.makeText(requireContext(), "密碼修改失敗:"+it.message, Toast.LENGTH_LONG).show()
                }
                is ApiResponse.NetworkError->{
                    Toast.makeText(requireContext(), "密碼修改失敗:"+it.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "密碼修改失敗", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun isValidPassword(oldPassword: String, newPassword: String): Boolean {
        if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {

            return true
        } else {
            Toast.makeText(requireContext(), "新密碼與舊密碼不能為空", Toast.LENGTH_LONG).show()
            return false
        }
    }

    private fun initView() {
        binding.btSubmitPassword.setOnClickListener {
            val oldPassword = binding.etOldPassword.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            if (isValidPassword(oldPassword, newPassword)) {
                val req = EditPasswordReq(
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )
                val mixpanel = initialMixpanel()
                mixpanel.track("get_new_password_action")//變更密碼api送出
                viewModel.changePassword(req)
            }
        }
    }
}