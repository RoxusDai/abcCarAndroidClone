package com.easyrent.abccarapp.abccar.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.easyrent.abccarapp.abccar.databinding.DialogLoadingBinding

class LoadingDialog : DialogFragment() {

    private var _binding: DialogLoadingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            this.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogLoadingBinding.inflate(inflater, container, false)

        // LoadingDialog無法取消
        dialog?.setCancelable(false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}