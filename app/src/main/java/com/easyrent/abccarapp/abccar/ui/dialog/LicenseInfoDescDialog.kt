package com.easyrent.abccarapp.abccar.ui.dialog


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.DialogFragment
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.DialogLicenseInfoDescLayoutBinding

class LicenseInfoDescDialog : DialogFragment() {

    private var _binding: DialogLicenseInfoDescLayoutBinding? = null
    private val binding: DialogLicenseInfoDescLayoutBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLicenseInfoDescLayoutBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivCloseButton.setOnClickListener {
            dismiss()
        }
    }


    override fun onPause() {
        super.onPause()
        dismiss()
    }

}