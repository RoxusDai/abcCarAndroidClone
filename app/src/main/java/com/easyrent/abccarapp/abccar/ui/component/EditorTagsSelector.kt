package com.easyrent.abccarapp.abccar.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.ViewEditorAccessorSelectorBinding

class EditorTagsSelector @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: ViewEditorAccessorSelectorBinding? = null
    private val binding get() = _binding!!

    private val itemListAdapter = TagsListAdapter()

    private var title: String
    private var pickedEmptyDesc: String
    private var allPickedDesc: String

    init {

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.EditorAccessoriesSelector,
            0, 0).apply {

            try {
                title = getString(R.styleable.EditorAccessoriesSelector_title) ?: "Undefine"
                pickedEmptyDesc = getString(R.styleable.EditorAccessoriesSelector_picked_empty_desc)?: "Undefine"
                allPickedDesc = getString(R.styleable.EditorAccessoriesSelector_all_picked_desc) ?: "Undefine"
            } finally {
                recycle()
            }
        }


        _binding = ViewEditorAccessorSelectorBinding.inflate(
            LayoutInflater.from(context), this, true
        )

        initView()
    }

    fun setList(list: List<TagsItem>) {
        itemListAdapter.setList(list)
    }

    fun getCheckedSum() = itemListAdapter.getCheckedItemSum()

    fun getTagsList() = itemListAdapter.getTagsList()

    fun setCheckState(list: List<Int>) {
        itemListAdapter.setCheckState(list)
    }

    private fun initView() {
        binding.title.text = title
        binding.pickupListEmptyDesc.text = pickedEmptyDesc

        binding.tvSelectAll.setOnClickListener {
            itemListAdapter.selectAll()
        }

        binding.tvCancelAll.setOnClickListener {
            itemListAdapter.cancelAll()
        }

        binding.rvPickupItemList.apply {
            layoutManager = GridLayoutManager(context, 2)
//            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = itemListAdapter
        }
    }

}