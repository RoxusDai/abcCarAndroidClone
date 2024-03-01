package com.easyrent.abccarapp.abccar.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.easyrent.abccarapp.abccar.databinding.DialogWarningDescLayoutBinding

class WarningDescDialog(
    val title: String,
    val message: String,
) : DialogFragment() {

    private var _binding: DialogWarningDescLayoutBinding? = null
    private val binding: DialogWarningDescLayoutBinding get() = _binding!!

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
    ): View? {
        _binding = DialogWarningDescLayoutBinding.inflate(inflater, container, false)

        binding.btDismiss.setOnClickListener {
            dismiss()
        }

        binding.tvTitle.text = title
        binding.tvDesc.text = message

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

}