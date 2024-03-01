package com.easyrent.abccarapp.abccar.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.FragmentAccountInfoBinding
import com.easyrent.abccarapp.abccar.remote.response.DisplayTitle
import com.easyrent.abccarapp.abccar.remote.response.GetLastPoolPointsHistoryResp
import com.easyrent.abccarapp.abccar.ui.fragment.state.AccountInfoState
import com.easyrent.abccarapp.abccar.viewmodel.AccountInfoViewModel

class AccountInfoFragment : BaseFragment() {

    private var _binding: FragmentAccountInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountInfoViewModel by viewModels {
        AccountInfoViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountInfoBinding.inflate(inflater, container, false)

        initMenu()

        initLiveData()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAccountInfo()
    }

    private fun initLiveData() {
        viewModel.statLiveData.observe(viewLifecycleOwner) { state ->
            if (state !is AccountInfoState.Loading) hideLoadingDialog()

            when(state) {
                AccountInfoState.Loading -> showLoadingDialog()
                is AccountInfoState.AccountInfo -> {
                    initCarInfo(state.displayTitle)
                }

                is AccountInfoState.PointInfo -> {
                    initPointInfo(state.info)
                }

                is AccountInfoState.MemberInfo -> {
                    binding.tvName.text = state.info.name
                }

                is AccountInfoState.Error -> {
                    // 9000為無儲值資料，不顯示錯誤
                    if (state.errorCode == "9000") return@observe
                    checkErrorCode(
                        errorCode = state.errorCode,
                        message = state.message
                    )
                    Toast.makeText(
                        requireActivity(),
                        state.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun initCarInfo(displayTitle: DisplayTitle) {
        binding.apply {
            tvTotalCount.text = "${displayTitle.carInGarageTotalCount}" // 總在庫數
            tvTotalPublishingCount.text = "${displayTitle.carInGarageTotalCount + displayTitle.carFixedPublishingCount}" // 刊登總上架數
            tvForSaleCount.text = "${displayTitle.carForSaleCount}" // 未上架車輛數
            tvDealCount.text = "${displayTitle.carDealDoneCount}" // 已成交車輛數
            tvCarPublishingCount.text = String.format(
                getString(R.string.profile_publish_count_value), displayTitle.carPublishingCount
            ) // 上架數
        }
    }

    private fun initPointInfo(info: GetLastPoolPointsHistoryResp) {
        binding.apply {
            tvHoldPoints.text = String.format(getString(R.string.profile_pre_deduct_point_value), info.holdPoints)
            pointsValue.text = "$${info.realPoolPoints}"
        }
    }

    // Fragment設定Toolbar menu可參考
    // https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
    private fun initMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}