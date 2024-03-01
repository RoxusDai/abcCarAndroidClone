package com.easyrent.abccarapp.abccar.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 *  離開新增物件程序Dialog
 */

class EditorCloseConfirmDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("確定要關閉嗎？")
                setMessage("關閉將要重新填寫")
                setPositiveButton("確定") { dialog, _ ->
                    dialog.dismiss()
                    requireActivity().finish()
                }
                setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
            }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}