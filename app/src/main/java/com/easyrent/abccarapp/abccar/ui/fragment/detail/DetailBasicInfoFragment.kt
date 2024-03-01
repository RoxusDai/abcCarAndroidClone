package com.easyrent.abccarapp.abccar.ui.fragment.detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.dataType.EditCarStatus
import com.easyrent.abccarapp.abccar.databinding.FragmentDetailBasicInfoBinding
import com.easyrent.abccarapp.abccar.manager.editor.basic.BasicInfoTable
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.CarInfoListFragmentDirections
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.BasicEditorStatus
import com.easyrent.abccarapp.abccar.viewmodel.detail.CarDetailViewModel
import com.easyrent.abccarapp.abccar.viewmodel.detail.DetailBasicInfoViewModel

class DetailBasicInfoFragment : BaseFragment() {
    var _binding :FragmentDetailBasicInfoBinding? = null
    private val binding get() = _binding!!

    private val shareViewModel : CarDetailViewModel by activityViewModels()
    private val viewModel : DetailBasicInfoViewModel by viewModels{
        DetailBasicInfoViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBasicInfoBinding.inflate(inflater, container, false)

        initLiveData()
        initView()
        return binding.root
    }

    private fun initView() {

    }
    private fun initLiveData(){
        viewModel.tableLiveData.observe(viewLifecycleOwner) { tableInfo ->
            setTableInfoDisplay(tableInfo)
        }

        viewModel.statusLiveData.observe(viewLifecycleOwner) { status ->
            when(status){
                is BasicEditorStatus.InitTableInfo->{
                    ////api取得資料,更新詳細資料

                    viewModel.initBasicTable(status.resp)
                    shareViewModel.setEditCarInfoResp(status.resp)

                    val data = status.resp.carInfo
                    viewModel.refreshTable()
                    binding.tvDetailId.text = data.id.toString()
                    val carStatus = EditCarStatus.parser(data.status)
                    setStatusTag( carStatus)

                    binding.tvDetailCaution.apply {
                        val statusString = shareViewModel.warnStatus
                        if (statusString.isEmpty() || statusString.isBlank()) {
                            visibility = View.GONE
                            text = ""
                        } else {
                            visibility = View.VISIBLE
                            text = statusString
                        }
                    }

                }
                is BasicEditorStatus.ErrorMessage->{
                    ///讀取失敗
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("讀取失敗 請退回並重新讀取")
                        .setPositiveButton("確認") { _, _ -> }
                        .setOnDismissListener {
                            shareViewModel.LoadFail()
                        }.create().show()
                }

                else -> {}
            }
        }
        ///呼叫api取得車輛詳細資料
        viewModel.getEditCarInfo(shareViewModel.getID())
    }

    //表單刷新
    @SuppressLint("SetTextI18n")
    private fun setTableInfoDisplay(info: BasicInfoTable) {
        ///設定封面圖片
        Glide.with(binding.root)
            .load(shareViewModel.getPhotoInfoTable().imageUrlList.filter(){it.isCover}[0].url)
            .into(binding.ivCarCover)
        //設定基本資料
        binding.tvDetailTitle.text = "${info.series.name} ${info.category.name} ${info.manufacture.year}"
        binding.tvDetailPrice.text = "${info.priceInfo.basePrice}萬"
    }

    //設定狀態
    private fun setStatusTag( status: EditCarStatus) {
        // is EditCarStatus.Already -> if (info.onlineDescription == 1) { "受訂中" } else { "上架中" } // 1
        binding.tvDetailStatus.apply {
            if (status is EditCarStatus.Already && shareViewModel.onlineDescribe == 1) {
                setBackgroundColor(resources.getColor(R.color.main_red, null))
                setTextColor(ContextCompat.getColor(this.context, R.color.white))
            } else {
                setBackgroundColor(resources.getColor(status.colorRes, null))
                setTextColor(ContextCompat.getColor(this.context, status.textColorRes))
            }
            //設定文字
            text = when (status) {
                is EditCarStatus.Discontinued -> status.desc // 0
                is EditCarStatus.Already -> if (shareViewModel.onlineDescribe == 1) {
                    "受訂中"
                } else {
                    "上架中"
                } // 1
                is EditCarStatus.SoldPause -> "受訂中" // -20
                is EditCarStatus.ReviewFail -> "審核失敗" // -19
                is EditCarStatus.SoldOutNotCharge -> "免費下架" // -18
                is EditCarStatus.SoldOutWithUseOther -> "下架驗證" // -15
                else -> "已成交" // else
            }

        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}