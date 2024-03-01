package com.easyrent.abccarapp.abccar.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.easyrent.abccarapp.abccar.databinding.DialogVerifyVehicleNoSpecLayoutBinding

class EditorVehicleNoDescDialog(
    private val message: String,
    private val isBlockStep: Boolean = false,
    private val onNextEvent: (() -> Unit)? = null
) : DialogFragment() {

    private var _binding: DialogVerifyVehicleNoSpecLayoutBinding? = null
    private val binding: DialogVerifyVehicleNoSpecLayoutBinding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogVerifyVehicleNoSpecLayoutBinding.inflate(inflater, container, false)

        binding.tvErrorMessage.text = message

        if (isBlockStep) {
            binding.llCancelAndNextGroup.visibility = View.GONE
            binding.tvNote.visibility = View.GONE
            binding.btDismiss.visibility = View.VISIBLE
        } else {
            binding.llCancelAndNextGroup.visibility = View.VISIBLE
            binding.tvNote.visibility = View.VISIBLE
            binding.btDismiss.visibility = View.GONE
        }

        binding.btCancel.setOnClickListener {
            dismiss()
        }

        binding.btDismiss.setOnClickListener {
            dismiss()
        }

        binding.btNext.setOnClickListener {
            onNextEvent?.invoke()
        }

        return binding.root
    }
}