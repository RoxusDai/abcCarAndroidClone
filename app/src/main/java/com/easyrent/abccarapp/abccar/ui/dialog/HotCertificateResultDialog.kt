package com.easyrent.abccarapp.abccar.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.easyrent.abccarapp.abccar.databinding.DialogHotCertificateResultLayoutBinding

class HotCertificateResultDialog(
    private val result: Boolean,
    private val message: String
) : DialogFragment() {

    private var _binding: DialogHotCertificateResultLayoutBinding? = null
    private val binding: DialogHotCertificateResultLayoutBinding get() = _binding!!

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
        _binding = DialogHotCertificateResultLayoutBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {

        if (result) {
            binding.tvSuccessResult.visibility = View.VISIBLE
            binding.ivSuccessIcon.visibility = View.VISIBLE
            binding.tvSuccessDesc.visibility = View.VISIBLE

            binding.tvFailedResult.visibility = View.GONE
            binding.ivFailedIcon.visibility = View.GONE
            binding.tvFailedDesc.visibility = View.GONE

        } else {
            binding.tvSuccessResult.visibility = View.GONE
            binding.ivSuccessIcon.visibility = View.GONE
            binding.tvSuccessDesc.visibility = View.GONE

            binding.tvMessage.text = message

            binding.tvFailedResult.visibility = View.VISIBLE
            binding.ivFailedIcon.visibility = View.VISIBLE
            binding.tvFailedDesc.visibility = View.VISIBLE
        }

        binding.btDismiss.setOnClickListener {
            dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

}