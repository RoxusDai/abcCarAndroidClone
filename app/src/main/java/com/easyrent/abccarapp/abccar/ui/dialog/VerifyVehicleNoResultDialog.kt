package com.easyrent.abccarapp.abccar.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.easyrent.abccarapp.abccar.databinding.DialogVerifyVehicleNoNormalLayoutBinding

class VerifyVehicleNoResultDialog(
    private val errorMessage: String,
    private val isShowUploadDesc: Boolean,
) : DialogFragment() {

    private var _binding: DialogVerifyVehicleNoNormalLayoutBinding? = null
    private val binding: DialogVerifyVehicleNoNormalLayoutBinding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogVerifyVehicleNoNormalLayoutBinding.inflate(layoutInflater, container, false)

        initView()

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    private fun initView() {
        binding.tvUploadMessage.visibility = if (isShowUploadDesc) View.VISIBLE else View.GONE
        binding.btDismiss.setOnClickListener {
            dismiss()
        }

        binding.tvErrorCode.text = errorMessage
    }

}