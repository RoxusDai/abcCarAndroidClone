package com.easyrent.abccarapp.abccar.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.easyrent.abccarapp.abccar.databinding.DialogListSelectorBinding
import com.easyrent.abccarapp.abccar.ui.adapter.EditorSelectorAdapter

class EditorListSelectorDialog(
    private val title: String,
    private val list: List<String>,
    private val onItemSelectIndex: (Int) -> Unit
) : DialogFragment() {

    private var _binding: DialogListSelectorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogListSelectorBinding.inflate(inflater, container, false)

        initView()

        return binding.root
    }

    private fun initView() {
        binding.title.text = title
        binding.rvList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = EditorSelectorAdapter(list) {
                onItemSelectIndex(it)
                dismiss()
            }
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    RecyclerView.VERTICAL
                )
            )
        }
    }

}