package com.easyrent.abccarapp.abccar.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.SearchView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.dataType.EditCarStatus
import com.easyrent.abccarapp.abccar.databinding.FragmentCarInfoListBinding
import com.easyrent.abccarapp.abccar.remote.request.BatchUpdateCarAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.UpdateMemberCarsStatusReq
import com.easyrent.abccarapp.abccar.remote.response.CarEdit
import com.easyrent.abccarapp.abccar.ui.activity.CarInfoEditorActivity
import com.easyrent.abccarapp.abccar.ui.adapter.CarInfoListAdapter
import com.easyrent.abccarapp.abccar.ui.dialog.WarningDescDialog
import com.easyrent.abccarapp.abccar.ui.fragment.state.CarInfoListState
import com.easyrent.abccarapp.abccar.ui.fragment.state.FilterListState
import com.easyrent.abccarapp.abccar.viewmodel.MainCarListViewModel
import org.json.JSONObject

class CarInfoListFragment : BaseFragment() {

    private var _binding: FragmentCarInfoListBinding? = null
    private val binding get() = _binding!!

    private var editMode = 1

    private var sortMode = 1

    private var memberId =0

    private val carInfoListAdapter = CarInfoListAdapter(
        onItemEditorClicked = { it, editable ->
            editMode = 1
            val mixpanel = initialMixpanel()
            mixpanel.track("edit_button_tapped", JSONObject().put("memberId",memberId).put("carId",it.carID))//編輯
            startToEditorActivity(it.carID, checkEditmode(it))
        },
        onItemPublishClicked = {
            when (it.status) {
                ///上架中,下架api
                1 -> {
                    val builder = AlertDialog.Builder(requireActivity())
                    builder.setTitle("確定下架此車輛物件?")
                        .setMessage("提醒您\n 系統將會對此車輛做驗證追蹤\n 正確後則會再進行付費流程\n")
                        .setPositiveButton("確定下架") { _, _ ->
                            val mixpanel = initialMixpanel()
                            mixpanel.track("published car removeFromShelves on home page",
                                JSONObject().put("car id", it.carID)
                            )//下架
                            viewModel.updateMemberCarsStatus(
                                BatchUpdateCarAdvertisingBoardReq(
                                    MemberID = 0,
                                    ActionStatus = -15,
                                    status = -15,
                                    carID = listOf( it.carID),
                                    PullReason = 0,
                                    ZoneReason = 0,
                                    OnlineDescription = 0,
                                )
                            )
                        }
                        .setNegativeButton("取消") { _, _ -> }
                    builder.create().show()
                }
                ///非上架中,上架api
                else -> {
                    val mixpanel = initialMixpanel()
                    mixpanel.track("published car on home page", JSONObject().put("carId",it.carID))//上架
                    viewModel.updateMemberCarsStatus(
                        BatchUpdateCarAdvertisingBoardReq(
                            MemberID = 0,
                            ActionStatus = 1,
                            status = 1,
                            carID = listOf( it.carID),
                            PullReason = 0,
                            ZoneReason = 0,
                            OnlineDescription = 0,
                        )
                    )
                }
            }
        },
        onDetailClicked = {
            val mixpanel = initialMixpanel()
            mixpanel.track("show detail view", JSONObject().put("memberId",memberId).put("carId",it.carID))//詳細

            val bundle = setDetailBundle(it)
            val navController = findNavController()
            navController.navigate(R.id.carDetailFragment, bundle)
        },
        onReserveClicked = {
            if (it.onlineDescription == 1) {
                //取消受訂
                val mixpanel = initialMixpanel()
                mixpanel.track("cancelling Order Tapped on home page", JSONObject().put("car id",it.carID))//取消受訂
                viewModel.batchUpdateCarAdvertisingBoard(
                    BatchUpdateCarAdvertisingBoardReq(
                        MemberID = 0,
                        ActionStatus = 1,
                        status = 1,
                        carID = listOf( it.carID),
                        PullReason = 0,
                        ZoneReason = 0,
                        OnlineDescription = 0,
                    )
                )
            } else {
                //受訂
                val mixpanel = initialMixpanel()
                mixpanel.track("updateAcceptingOrder true on home page",JSONObject().put("car id",it.carID))//受訂
                viewModel.batchUpdateCarAdvertisingBoard(
                    BatchUpdateCarAdvertisingBoardReq(
                        MemberID = 0,
                        ActionStatus = 1,
                        status = 1,
                        carID = listOf( it.carID),
                        PullReason = 0,
                        ZoneReason = 0,
                        OnlineDescription = 1,
                    )
                )
            }
        },
        onDescClicked = { title, message ->

            WarningDescDialog(title, message).show(childFragmentManager, "WarningDescDialog")
//            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
        }
    )

    private val viewModel: MainCarListViewModel by viewModels {
        MainCarListViewModel.Factory
    }

    private fun setDetailBundle(it: CarEdit): Bundle {
        val bundle = Bundle()

        bundle.putInt("carId", it.carID)
        bundle.putDouble("price", it.price)
        bundle.putString("imageUrl", it.imageUrl)
        bundle.putInt("viewCount", it.viewCount)
        bundle.putInt("favoriteCount", it.favoriteCount)
        //設定狀態用
        bundle.putInt("onlineDescription", it.onlineDescription)
        bundle.putString(
            "warnStatus",
            carInfoListAdapter.getRightTopTagString(EditCarStatus.parser(it.status), it)
        )
        return bundle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCarInfoListBinding.inflate(inflater, container, false)
        memberId = savedInstanceState?.getInt("memberId") ?:0

        //設定view前取得過濾清單
        viewModel.getBrandFilterList()

        initMenu()
        initView()
        initLiveData()

        if (carInfoListAdapter.itemCount == 0) viewModel.refreshCarList()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refreshList()
        editMode = 0

    }

    private fun initLiveData() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            if (state !is CarInfoListState.Loading) hideLoadingDialog()

            when (state) {
                is CarInfoListState.GetList -> {
                    val list = state.list
                    if (list.isNotEmpty()) {
                        binding.ivNoCarData.visibility = View.GONE
                        carInfoListAdapter.addList(state.list)
                    }
                    setNoDataImage()

                }

                is CarInfoListState.Error -> {
                    // 檢查ErrorCode
                    checkErrorCode(
                        errorCode = state.errorCode,
                        message = state.message
                    )
                }

                CarInfoListState.Loading -> showLoadingDialog()

                is CarInfoListState.PublishDone -> {
                    when (state.mode.status) {
                        1 -> {//上架
                            refreshList()
                            Toast.makeText(requireActivity(), "物件上架成功", Toast.LENGTH_SHORT).show()
                        }
                        -15 -> {//下架
                            refreshList()
                            Toast.makeText(requireActivity(), "物件下架成功", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                is CarInfoListState.ReserveDone -> {
                    when (state.mode) {
                        1 -> {//受訂
                            refreshList()
                            Toast.makeText(requireActivity(), "物件受訂成功", Toast.LENGTH_SHORT).show()
                        }
                        0 -> {//取消
                            refreshList()
                            Toast.makeText(requireActivity(), "取消受訂成功", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                else -> {}
            }
        }

        viewModel.filterLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FilterListState.GetBrandList -> {
                    //設定過濾車廠列表
                    binding.spCarBrand.adapter = ArrayAdapter(
                        requireActivity(),
                        R.layout.adapter_filter_item,
                        R.id.spinner_text1,
                        state.list
                    )
                    binding.spCarBrand.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedID = viewModel.brandList[position].id
                                //更新過濾車廠,取得車款列表
                                if (selectedID != carInfoListAdapter.carBrandIDFilter) {
                                    carInfoListAdapter.brandFilterList(selectedID)
                                    viewModel.getCategoryList(selectedID)
                                }
                                setNoDataImage()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                }

                is FilterListState.GetCategoryList -> {
                    //設定過濾車款列表
                    binding.spCarCategory.adapter = ArrayAdapter(
                        requireActivity(),
                        R.layout.adapter_filter_item,
                        R.id.spinner_text1,
                        state.list
                    )
                    binding.spCarCategory.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedID = viewModel.categoryList[position].id
                                if (carInfoListAdapter.carCategoryFilter != selectedID) {
                                    carInfoListAdapter.categoryFilterList(selectedID)
                                }
                                setNoDataImage()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                }

                is FilterListState.Error -> {

                }

                else -> {}
            }
        }
    }

    private fun initView() {
        binding.btCreateInfo.setOnClickListener {
            val mixpanel = initialMixpanel()
            mixpanel.track("clear_filter_tapped")//清除篩選
            mixpanel.track("add_car_button_tapped", JSONObject().put("memberId", memberId)) //新增物件(車輛)
            editMode = 1
            startToEditorActivity(0, 3)
        }

        // List初始化
        binding.rvCarInfoList.apply {
            adapter = carInfoListAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    RecyclerView.VERTICAL
                )
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val manager = recyclerView.layoutManager as LinearLayoutManager
                    val currentIndex = manager.findLastCompletelyVisibleItemPosition()
                    if (
                        viewModel.isMoreEditCarInfo(
                            currentListPosition = currentIndex,
                            currentListLastIndex = carInfoListAdapter.getCurrentList().lastIndex,
                        )
                    ) {
                        viewModel.loadMoreCarList()
                    }
                }
            })
        }

        // 設定搜尋欄
        binding.svFilter.queryHint = getString(R.string.Search_Hint)
        binding.svFilter.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setNoDataImage()
                if (!newText.isNullOrBlank()) {
                    carInfoListAdapter.searchFilterList(newText)

                }
                return false
            }
        })

        //設定radio button
        binding.rbSourceAbc.setOnCheckedChangeListener { buttonView, isChecked ->
            radioButtonViewChange(buttonView, isChecked)
            if (isChecked) carInfoListAdapter.sourceFilterList(1)
            setNoDataImage()
        }
        binding.rbSourceExternal.setOnCheckedChangeListener { buttonView, isChecked ->
            radioButtonViewChange(buttonView, isChecked)
            if (isChecked) carInfoListAdapter.sourceFilterList(2)
            setNoDataImage()
        }
        binding.rbSourceEasy.setOnCheckedChangeListener { buttonView, isChecked ->
            radioButtonViewChange(buttonView, isChecked)
            if (isChecked) carInfoListAdapter.sourceFilterList(3)
            setNoDataImage()
        }
        //設定狀態過濾列表
        binding.spCarCategory.adapter = ArrayAdapter(
            requireActivity(),
            R.layout.adapter_filter_item,
            R.id.spinner_text1,
            arrayListOf(getString(R.string.select_category))
        )
        binding.spCarCategory.prompt = getString(R.string.select_category)

        binding.spCarStatus.adapter = ArrayAdapter(
            requireActivity(),
            R.layout.adapter_filter_item,
            R.id.spinner_text1,
            viewModel.statusShowList
        )
        binding.spCarStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                carInfoListAdapter.statusFilterList(position)
                setNoDataImage()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //設定重置搜尋,過濾
        binding.btResetFilter.setOnClickListener {
            val mixpanel = initialMixpanel()
            mixpanel.track("clear_filter_tapped")//清除篩選

            binding.svFilter.setQuery("", false)
            binding.spCarBrand.setSelection(0, true)
            binding.spCarCategory.adapter = ArrayAdapter(
                requireActivity(),
                R.layout.adapter_filter_item,
                R.id.spinner_text1,
                arrayListOf(getString(R.string.select_category))
            )
            binding.spCarStatus.setSelection(0, true)
            binding.rgCarSource.check(R.id.rb_source_abc)
            carInfoListAdapter.resetFilterList()
        }

        // 設定下拉更新選單Listener，刷新所有List狀態
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshList()
        }
    }

    private fun setNoDataImage() {
        if (carInfoListAdapter.itemCount == 0) binding.ivNoCarData.visibility = View.VISIBLE
        else binding.ivNoCarData.visibility = View.GONE
    }

    private fun refreshList() {
        carInfoListAdapter.clearList()
        viewModel.refreshCarList()
        setNoDataImage()
        binding.swipeRefreshLayout.isRefreshing = false
    }


    // Fragment設定Toolbar menu可參考
    // https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
    private fun initMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_setting_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    val navController = findNavController()
                    return when (menuItem.itemId) {
                        R.id.car_sort_fragment -> {
                            setFragmentResultListener("sortMode") { _, bundle ->
                                val sortFragmentRes = bundle.getInt("sortMode")
                                if (sortMode != sortFragmentRes) {
                                    //sort carlist
                                    sortMode = sortFragmentRes
                                    carInfoListAdapter.setSortMode(sortMode)

                                }
                            }
                            navController.navigate(
                                R.id.sortFragment,
                                bundleOf("sortMode" to sortMode)
                            )
                            true
                        }

                        R.id.account_info_fragment -> {
                            val mixpanel = initialMixpanel()
                            mixpanel.track("info_view_tapped") //帳號資訊
                            navController.navigate(
                                CarInfoListFragmentDirections.actionCarInfoListFragmentToAccountInfoFragment()
                            )
                            true
                        }

                        R.id.setting_fragment -> {
                            val mixpanel = initialMixpanel()
                            mixpanel.track("settings_tapped") //設定(齒輪)

                            navController.navigate(
                                CarInfoListFragmentDirections.actionCarInfoListFragmentToSettingFragment()
                            )
                            true
                        }

                        else -> false
                    }
                }
            }
        )
    }

    private fun radioButtonViewChange(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            buttonView.background = resources.getDrawable(R.drawable.rb_checked)
            buttonView.setTextColor(resources.getColor(R.color.black))
        } else {
            buttonView.background = null
            buttonView.setTextColor(resources.getColor(R.color.mid_gray))
        }
    }

    private fun startToEditorActivity(
        carId: Int,
        editmode: Int
    ) {
        val bundle = Bundle().apply {
            putInt("carId", carId)
            //判斷是否為可編輯的簡易車源
            putInt("editmode", editmode)
        }
        val intent = Intent(requireActivity(), CarInfoEditorActivity::class.java).apply {
            putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun checkEditmode(carEdit: CarEdit): Int {
        //0 不可編輯
        //1 物件廠牌,車型,出場,物件車款,排氣量可編輯 外部跟簡易刊登車元 細項都可以編輯
        //abc物件廠牌,車型,出場,物件車款,排氣量不可編輯
        //2 車身號碼與行照可編輯 ,如非缺或更正車身號碼時,車身號碼,行照不能編輯
        //4 可進入編輯模式

        val source = checkSource(carEdit.source as String?)
        //待售,上架中以外不能編輯 ,車輛 待審核 皆不可編輯

            var editmode = 0

            //非abc車源
            if (source == 2 || source == 3) editmode += 1

            //缺車身號碼與行照
            if ((carEdit.vehicleNoCheckStatus == 2 || carEdit.vehicleNoCheckStatus == 0) && carEdit.vehicleNoReview == 0 && carEdit.transferCheckId == 0) editmode += 2
            return editmode
    }

    private fun checkSource(source: String?): Int = when (source) {
        null, "" -> 1 //abc
        "32", "HAA" -> 2//外部車源
        "12", "13" -> 3 //簡易刊登
        else -> 1
    }
}