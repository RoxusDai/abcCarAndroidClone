package com.easyrent.abccarapp.abccar.ui.fragment.editor

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.FragmentEditorBasicInfoBinding
import com.easyrent.abccarapp.abccar.manager.editor.basic.BasicInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.colum.LicenseInfo
import com.easyrent.abccarapp.abccar.manager.editor.colum.UnqualifiedResponse
import com.easyrent.abccarapp.abccar.remote.response.CarInfo
import com.easyrent.abccarapp.abccar.ui.adapter.SimpleDescListAdapter
import com.easyrent.abccarapp.abccar.ui.dialog.EditorCloseConfirmDialog
import com.easyrent.abccarapp.abccar.ui.dialog.EditorListSelectorDialog
import com.easyrent.abccarapp.abccar.ui.dialog.EditorVehicleNoDescDialog
import com.easyrent.abccarapp.abccar.ui.dialog.HotCertificateResultDialog
import com.easyrent.abccarapp.abccar.ui.dialog.LicenseInfoDescDialog
import com.easyrent.abccarapp.abccar.ui.dialog.VerifyVehicleNoResultDialog
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.BasicEditorStatus
import com.easyrent.abccarapp.abccar.viewmodel.editor.EditorActivityViewModel
import com.easyrent.abccarapp.abccar.viewmodel.editor.EditorBasicInfoViewModel
import org.json.JSONObject

/**
 *
 *  Step 1 - 基本資料
 *
 */
class EditorBasicInfoFragment : BaseFragment() {

    private lateinit var licenseImagePicker: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var licensePdfPicker: ActivityResultLauncher<Array<String>>

    private lateinit var importDocImagePicker: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var importDocPdfPicker: ActivityResultLauncher<Array<String>>

    private var _binding: FragmentEditorBasicInfoBinding? = null
    private val binding get() = _binding!!

    private fun getToolbar() = binding.topToolbar
    private fun getBasicTable() = binding.basicTable
    private fun getLicenseTable() = binding.licenseTable
    private fun getPriceTable() = binding.priceTable

    private val descListAdapter = SimpleDescListAdapter()


    // Activity ViewModel
    private val shareViewModel: EditorActivityViewModel by activityViewModels()

    private val viewModel: EditorBasicInfoViewModel by viewModels {
        EditorBasicInfoViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFilePickerRegister()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditorBasicInfoBinding.inflate(inflater, container, false)

        initView()
        initLiveData()
        initTable()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.initSpecificationTypesManager()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initTable() {
        val table = shareViewModel.getBasicInfoTable()
        val carId = shareViewModel.getCarId()

        if (carId > 0) {
            if (table.isInit) {
                viewModel.setTable(table)
                editModeCheck()
            } else {
                viewModel.getEditCarInfo(carId)
            }
        } else {
            viewModel.setTable(table)
        }
    }

    private fun initView() {
        val toolbar = getToolbar()
        val basic = getBasicTable()
        val licenseGroup = getLicenseTable()

        binding.cvErrorDescList.apply {
            adapter = descListAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    RecyclerView.VERTICAL
                )
            )
        }

        // 設定標題
        toolbar.tvTitle.text = if (shareViewModel.getCarId() > 0) {
            "編輯物件"
        } else {
            "新增物件"
        }

        // 離開建立物件程序顯示提示Dialog
        toolbar.ivCloseButton.setOnClickListener {
            EditorCloseConfirmDialog().show(childFragmentManager, "CloseConfirmDialog")
        }

        // 第一頁隱藏返回按鈕
        toolbar.llBackButtonGroup.visibility = View.INVISIBLE

//        if (shareViewModel.getEditMode() == 1 || shareViewModel.getEditMode() == 3) {
        // 選擇廠牌
        basic.llBasicInfoBrand.setOnClickListener { viewModel.getBrandList() }
        // 選擇車型
        basic.tvSeriesValue.setOnClickListener { viewModel.getSeries() }
        // 選擇年份
        basic.tvManufactureYearValue.setOnClickListener { viewModel.getManufactureYearsList() }
        // 選擇月份 ( 不需要API )
        basic.tvManufactureMonthValue.setOnClickListener { viewModel.getManufactureMonthList() }
        // 物件車款,呼叫車款清單
        basic.tvCategoryValue.setOnClickListener { viewModel.getCarCategory() }
        // 傳動系統
        basic.tvTransmissionValue.setOnClickListener { viewModel.getTransmissionTypeList() }
        // 變速系統
        basic.tvGearValue.setOnClickListener { viewModel.getGearTypeList() }
        // 燃油種類
        basic.tvGasValue.setOnClickListener { viewModel.getFuelTypeList() }
        // 排氣量
        basic.tvEngineDisplacementValue.setOnClickListener { viewModel.getEngineDisplacementItemList() }
        // 乘坐人數
        basic.tvPassengerValue.setOnClickListener { viewModel.getPassengerItemList() }
        // 外觀顏色
        basic.tvColorValue.setOnClickListener { viewModel.getColorTypeList() }

        //設定里程數
        basic.edMileageValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.toString().matches(Regex("^0"))) {
                        basic.edMileageValue.setText("")
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val mileage = it.let {
                        if (it.isBlank()) "0" else it.toString()
                    }
                    viewModel.setMileage(mileage)
                }
            }
        })
        // 是否為平輸車
        licenseGroup.cbCarSourceFromImport.setOnCheckedChangeListener { _, b ->
            // 全權交由 getTable 更新對應狀態
            viewModel.setIsImport(b)
            viewModel.refreshTable() // 刷新對應表格
        }

//        } else {
//            //關閉里程數改變
//            basic.edMileageValue.isEnabled = false
//            //關閉平輸車按鈕功能
//            licenseGroup.cbCarSourceFromImport.isEnabled = false
//        }


        // 行照說明
        licenseGroup.ivLicenseDesc.setOnClickListener {
            LicenseInfoDescDialog().show(childFragmentManager, "LicenseInfoDescDialog")
        }

        // 上傳行照
        if (shareViewModel.getEditMode() > 1)
            licenseGroup.btUploadLicense.setOnClickListener {
                PopupMenu(requireActivity(), it).apply {
                    menuInflater.inflate(R.menu.editor_file_picker_menu, menu)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.editor_picker_menu_image -> {
                                pickupLicenseImageFile()
                            }

                            R.id.editor_picker_menu_pdf -> {
                                pickupLicensePdfFile()
                            }

                            R.id.editor_picker_menu_camera -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.implement_yet_desc),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        true
                    }
                }.show()
            }

        // 清除暫存
        licenseGroup.btDeleteLicenseFile.setOnClickListener {
            clearFileTempDesc()
        }

        // 平輸車文件說明
        licenseGroup.ivImportDesc.setOnClickListener {
            LicenseInfoDescDialog().show(childFragmentManager, "LicenseInfoDescDialog")
        }

        // 上傳報關文件
        licenseGroup.btUploadImportDoc.setOnClickListener {
            PopupMenu(requireActivity(), it).apply {
                menuInflater.inflate(R.menu.editor_file_picker_menu, menu)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.editor_picker_menu_image -> {
                            pickupImportImageFile()
                        }

                        R.id.editor_picker_menu_pdf -> {
                            pickupImportPdfFile()
                        }

                        R.id.editor_picker_menu_camera -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.implement_yet_desc),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    true
                }
            }.show()
        }

        // 清除暫存
        licenseGroup.btDeleteImportFile.setOnClickListener {
            clearFileTempDesc()
        }

        licenseGroup.edVerifyIdValue.apply {
            setOnFocusChangeListener { v, hasFocus ->
                // Focus 轉移自動更新車身號碼
                if (!hasFocus) {
                    viewModel.setVehicleNo(text.toString())
                }
            }
        }

        // 下一步
        binding.btNext.setOnClickListener { view ->
            // 里程數，下一步後儲存
            val mileage = basic.edMileageValue.text.toString().let {
                it.ifBlank { "0" }
            }
            // 車身號碼，下一步呼叫API時檢查
            val vehicleNo = licenseGroup.edVerifyIdValue.text.toString()
            val hotVerifyId = licenseGroup.edHotCerIdValue.text.toString()
            val basePrice = getPriceTable().edPriceValue.text.toString().toDouble()
            val ceilingPrice = getPriceTable().edCeilingPrice.text.toString().toDouble()

            viewModel.setBasePrice(basePrice)
            viewModel.setCeilingPrice(ceilingPrice)

            viewModel.setVehicleNo(vehicleNo)
            viewModel.setMileage(mileage)
            viewModel.setHotVerifyNo(hotVerifyId)

            val needCheckVehicleNo = shareViewModel.getEditCarInfoResp()?.carInfo?.let { info ->
                info.id > 0 && needCheckVehicleNo(info)
            } ?: true // 如果沒有InfoResp，預設檢查車身號碼

            viewModel.startTableCheckProcess(needCheckVehicleNo)
        }

        // Hot編號輸入框
        getLicenseTable().edHotCerIdValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {  // 如果輸入框字串不為Blank，則顯示驗證按鈕
                    if (it.isNotBlank()) {
                        getLicenseTable().btCheckCerNo.visibility = View.VISIBLE
                    } else {
                        getLicenseTable().btCheckCerNo.visibility = View.GONE
                    }
                }
            }
        })

        getLicenseTable().btCheckCerNo.setOnClickListener {
            // 檢查HotCerNo
            viewModel.checkHotCertificateNo(
                getLicenseTable().edHotCerIdValue.text.toString()
            )
        }

        // 售價，TextWatcher處理格式問題，限制小數點位數，下一步時檢查是否為
        getPriceTable().edPriceValue.addTextChangedListener(getBasePriceTextWatcher())

        // 最高價，不可低於基底價格
        getPriceTable().edCeilingPrice.addTextChangedListener(getCeilingPriceTextWatcher())

        getPriceTable().edCeilingPrice.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.checkPriceInfoInput()
            }
        }
    }

    //編輯模式
    private fun editModeCheck() {
        shareViewModel.getEditCarInfoResp()?.carInfo?.let { info ->

            // 編輯模式限制欄位輸入
            if (shareViewModel.getCarId() > 0) {
                binding.cvCarIdGroup.visibility = View.VISIBLE
                binding.tvCarIdValue.text = shareViewModel.getCarId().toString()

                if (shareViewModel.getEditMode() == 0 || shareViewModel.getEditMode() == 2) {
                    getBasicTable().apply {
                        //過濾可編輯或是新增物件
                        llBasicInfoBrand.isEnabled = false
                        llBasicInfoCaetgory.isEnabled = false
                        llBasicInfoSeries.isEnabled = false
                        //關閉里程數改變
                        edMileageValue.isEnabled = false
                        tvBrandValue.isEnabled = false
                        tvSeriesValue.isEnabled = false
                        tvManufactureYearValue.isEnabled = false
                        tvManufactureMonthValue.isEnabled = false
                        tvCategoryValue.isEnabled = false
                        tvEngineDisplacementValue.isEnabled = false
                    }
                    getLicenseTable().apply {
                        //關閉平輸車按鈕功能
                        cbCarSourceFromImport.isEnabled = false
                    }

                }
                if (shareViewModel.getEditMode() == 0 || shareViewModel.getEditMode() == 1) {
                    getLicenseTable().apply {
                        edVerifyIdValue.isEnabled = false
                        edHotCerIdValue.isEnabled = false

                        licenseFileButtonGroup.visibility = View.GONE
                        importDocFileButtonGroup.visibility = View.GONE

                        edVerifyIdValue.isEnabled = needCheckVehicleNo(info)
                    }
                }
            }
        }

    }


    // 判斷是否為平輸車，顯示對應表格
    private fun setSourceCardLayout(isImport: Boolean) {
        getLicenseTable().apply {
            if (isImport) {
                tvVerifyIdStar.visibility = View.INVISIBLE // 車身號碼星號
                cvLicenseGroup.visibility = View.GONE
                cvImportGroup.visibility = View.VISIBLE

            } else {
                tvVerifyIdStar.visibility = View.VISIBLE // 車身號碼星號
                cvLicenseGroup.visibility = View.VISIBLE
                cvImportGroup.visibility = View.GONE
            }
        }
    }

    private fun clearFileTempDesc() {
        viewModel.clearFiles()

        val table = getLicenseTable()
        table.btDeleteLicenseFile.visibility = View.GONE
        table.btDeleteImportFile.visibility = View.GONE
        table.ivImportPreview.visibility = View.GONE
        table.ivLicensePreview.visibility = View.GONE

        table.tvLicenseFileDesc.visibility = View.VISIBLE
        table.tvLicenseFileDesc.text = getString(R.string.file_pickup_desc)
        table.tvImportFileDesc.visibility = View.VISIBLE
        table.tvImportFileDesc.text = getString(R.string.file_pickup_desc)
    }

    // 檢查是否為Hot會員 ( 檢查是否有hotMemberID )
    private fun checkIsHotMember() {
        getLicenseTable().cvHotMemberCard.apply {
            visibility = if (viewModel.isHotMember()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun checkIsShowPriceRange() {
        // 如果為平輸車且價格大於150萬
        getPriceTable().llPriceRangeGroup.apply {
            visibility = if (viewModel.isShowPriceRangeGroup()) {
                View.VISIBLE
            } else {
                viewModel.setCeilingPrice(0.0) // 最高售價歸零
                View.GONE
            }
        }
    }

    // 檢查價格小數點格式
    private fun checkPriceFormat(editable: Editable, price: Double) {
        val str = price.toString()
        // 檢查小數點
        val decimalPointStatus = str.substringAfter('.').length >= 2
        if (decimalPointStatus) { // 超過就丟棄最後一位
            editable.delete(editable.length - 1, editable.length)
        }
    }

    private fun getBasePriceTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.toString().matches(Regex("^((0{2,}[0-9]*)\\.?[0-9]*|\\.)"))) {
                        binding.priceTable.edPriceValue.setText("0.")
                        binding.priceTable.edPriceValue.setSelection(2)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let { editable ->
                    editable.toString().toDoubleOrNull()?.let { basePrice ->
                        checkPriceFormat(editable, basePrice)
                        // 將數字連動到價格區間 ( 無論有無顯示
                        binding.priceTable.tvBasePrice.text = editable.toString()
                        viewModel.setBasePrice(editable.toString().toDoubleOrNull() ?: 0.0)

                        getPriceTable().llPriceRangeGroup.apply {
                            visibility =
                                if (viewModel.isShowPriceRangeGroup()) View.VISIBLE else View.GONE
                        }

                    } ?: run {
                        // 如果為空白，不符合數字條件
                        if (editable.toString().isBlank()) {
                            binding.priceTable.tvBasePrice.text = "0"
                        }
                    }
                }
            }
        }
    }

    private fun getCeilingPriceTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.toString().matches(Regex("^((0{2,}[0-9]*)\\.?[0-9]*|\\.)"))) {
                        binding.priceTable.edCeilingPrice.setText("0.")
                        binding.priceTable.edCeilingPrice.setSelection(2)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let { editable ->
                    editable.toString().toDoubleOrNull()?.let { ceilingPrice ->
                        checkPriceFormat(editable, ceilingPrice)
                        viewModel.setCeilingPrice(editable.toString().toDoubleOrNull() ?: 0.0)
                    }
                }
            }
        }
    }


    private fun initLiveData() {
        // Table
        viewModel.tableLiveData.observe(viewLifecycleOwner) { tableInfo ->
            setTableInfo(tableInfo)
        }

        viewModel.statusLiveData.observe(viewLifecycleOwner) { status ->
            //basicitem updated
            parseStatus(status)
        }

        viewModel.columnCheckLiveData.observe(viewLifecycleOwner) {
            parseUnqualified(it)
        }
    }

    private fun gotoNextStep() {
        // 儲存本頁資料
        val table = viewModel.getTableInfo().apply {
            isInit = true
        }
        shareViewModel.setBasicInfoTable(table)

        val mixpanel = initialMixpanel()
        mixpanel.track("step 1 passed")//step1 to 2

        findNavController().navigate(
            EditorBasicInfoFragmentDirections.actionEditorBasicInfoFragmentToEditorAccessoriesFragment()
        )
    }

    // 表單刷新
    private fun setTableInfo(info: BasicInfoTable) {
        val basic = binding.basicTable

        // 廠牌
        if (info.brand.isDefault()) {
            basic.tvBrandValue.text = getString(R.string.please_select_brand)
        } else {
            basic.tvBrandValue.text = info.brand.name
        }

        // 車型
        if (info.series.isDefault()) {
            basic.tvSeriesValue.text = getString(R.string.please_select_brand_first)
        } else {
            basic.tvSeriesValue.text = info.series.name
        }

        // 年份
        if (info.manufacture.isYearDefault()) {
            basic.tvManufactureYearValue.text = getString(R.string.table_info_undefine_desc)
        } else {
            basic.tvManufactureYearValue.text = info.manufacture.year.toString()
        }

        // 月份
        if (info.manufacture.isMonthDefault()) {
            basic.tvManufactureMonthValue.text = getString(R.string.table_info_undefine_desc)
        } else {
            basic.tvManufactureMonthValue.text = info.manufacture.month.toString()
        }

        // 車款
        if (info.category.isDefault()) {
            basic.tvCategoryValue.text = getString(R.string.please_select_year)
            // 初始化其他參數
        } else {
            basic.tvCategoryValue.text = info.category.name
        }

        // 認證資訊
        setLicenseInfo(info)

        // 規格資訊
        setSpecInfo(info)

        // 顏色
        getBasicTable().tvColorValue.text = info.descInfo.colorType.name

        getBasicTable().edMileageValue.apply {
            setText("${info.descInfo.mileage}")
            if (text.isNotEmpty()) {
                setSelection(text.length)
            }
        }

        // 是否為平輸車
        getLicenseTable().cbCarSourceFromImport.isChecked = info.licenseInfo.isImport

        // 價格
        getPriceTable().edPriceValue.apply {
            setText(info.priceInfo.basePrice.toString())
            if (text.isNotEmpty()) {
                setSelection(text.length)
            }
        }
        getPriceTable().edCeilingPrice.apply {
            setText(info.priceInfo.ceilingPrice.toString())
            if (text.isNotEmpty()) {
                setSelection(text.length)
            }
        }
    }

    private fun setLicenseInfo(info: BasicInfoTable) {
        getLicenseTable().apply {
            edHotCerIdValue.setText(info.licenseInfo.hotVerifyId)
            edVerifyIdValue.setText(info.licenseInfo.vehicleNo)
        }
        setSourceCardLayout(info.licenseInfo.isImport)
        initFilePreview(info.licenseInfo)
        checkIsShowPriceRange()
        checkIsHotMember()
    }

    private fun initFilePreview(info: LicenseInfo) {
        if (info.licenseFileUrl.url.isNotBlank()) {
            if (info.licenseFileUrl.extension != "pdf") {
                getLicenseTable().ivLicensePreview.visibility = View.VISIBLE
                getLicenseTable().tvLicenseFileDesc.visibility = View.GONE

                Glide.with(requireView())
                    .load(info.licenseFileUrl.url)
                    .into(getLicenseTable().ivLicensePreview)
            } else {
                getLicenseTable().ivLicensePreview.visibility = View.GONE
                getLicenseTable().tvLicenseFileDesc.visibility = View.VISIBLE
            }

            getLicenseTable().btDeleteLicenseFile.visibility = View.VISIBLE
        } else {
            getLicenseTable().btDeleteLicenseFile.visibility = View.GONE
        }

        if (info.importDocFileUrl.url.isNotBlank()) {
            if (info.importDocFileUrl.extension != "pdf") {
                getLicenseTable().ivImportPreview.visibility = View.VISIBLE
                getLicenseTable().tvImportFileDesc.visibility = View.GONE

                Glide.with(requireView())
                    .load(info.importDocFileUrl.url)
                    .into(getLicenseTable().ivImportPreview)
            } else {
                getLicenseTable().ivImportPreview.visibility = View.GONE
                getLicenseTable().tvImportFileDesc.visibility = View.VISIBLE
            }
            getLicenseTable().btDeleteImportFile.visibility = View.VISIBLE
        } else {
            getLicenseTable().btDeleteImportFile.visibility = View.GONE
        }
    }

    // 需要處理預設值
    private fun setSpecInfo(info: BasicInfoTable) {
        val basicTable = binding.basicTable
        val spec = info.specification
        // 傳動系統
        basicTable.tvTransmissionValue.text = info.specification.transmission.name

        // 變速系統
        basicTable.tvGearValue.text = spec.gearType.name

        // 燃油種類
        basicTable.tvGasValue.text = spec.fuelType.name

        // 排氣量
        basicTable.tvEngineDisplacementValue.text = spec.engineDisplacement.name

        // 乘坐人數
        basicTable.tvPassengerValue.text = spec.passenger.name
    }

    private fun parseStatus(status: BasicEditorStatus) {

        // 如果不是Loading，隱藏LoadingDialog
        if (status !is BasicEditorStatus.Loading) {
            hideLoadingDialog()
        }

        when (status) {

            BasicEditorStatus.DataInitDone -> {
                // 初始資料載入完成
            }

            is BasicEditorStatus.InitTableInfo -> {
                viewModel.initBasicTable(status.resp)
                shareViewModel.setEditCarInfoResp(status.resp)
                editModeCheck()
            }

            is BasicEditorStatus.Brand -> { // 廠牌
                val brandList = status.list
                val list = status.list.map { item -> item.brandName }

                showSelectorDialog(
                    getString(R.string.please_select_brand),
                    list
                ) {
                    val item = brandList[it]
                    viewModel.setBrand(item.brandID, item.brandName)
                    getBasicTable().tvBrandValue.text = item.brandName
                }
            }

            is BasicEditorStatus.Series -> { // 車型
                val seriesList = status.list
                val list = seriesList.map { item -> item.seriesName }

                showSelectorDialog(
                    getString(R.string.please_select_category),
                    list
                ) {
                    val item = seriesList[it]
                    viewModel.setSeries(item.seriesID, item.seriesName)
                    getBasicTable().tvSeriesValue.text = item.seriesName
                }
            }

            is BasicEditorStatus.ManufactureYear -> { // 出廠年份
                val yearList = status.list

                showSelectorDialog(
                    getString(R.string.please_select_manufacture_year),
                    yearList
                ) {
                    viewModel.setManufactureYear(yearList[it].toInt())
                    getBasicTable().tvManufactureYearValue.text = yearList[it]

                    viewModel.setManufactureMonth(1) // 選擇年份後自動帶入月份
                    getBasicTable().tvManufactureMonthValue.text = "1"
                }
            }

            is BasicEditorStatus.ManufactureMonth -> { // 出廠月份
                val monthList = status.list

                showSelectorDialog(
                    getString(R.string.please_select_manufacture_month),
                    monthList
                ) {
                    viewModel.setManufactureMonth(it + 1) // 陣列index
                    getBasicTable().tvManufactureMonthValue.text = "${it + 1}"
                }
            }

            is BasicEditorStatus.CategoryAndSpecification -> {
                // 將資訊帶入表單
                getBasicTable().tvCategoryValue.text = status.categoryName
                val info = status.specItem

                // 不需要處理預設值
                getBasicTable().apply {
                    tvTransmissionValue.text = info.transmission.name
                    tvGearValue.text = info.gearType.name
                    tvGasValue.text = info.fuelType.name
                    tvEngineDisplacementValue.text = info.engineDisplacement.name
                    tvPassengerValue.text = info.passenger.name
                }
            }

            is BasicEditorStatus.Category -> {
                val list = status.list
                val stringList = list.map { it.name }
                showSelectorDialog(
                    getString(R.string.please_select_model),
                    stringList
                ) {
                    viewModel.setCategory(list[it].categoryId, list[it].name)
                    getBasicTable().tvCategoryValue.text = list[it].name
                }
            }

            is BasicEditorStatus.TransmissionTypeList -> {
                val list = status.list
                val stringList = list.map { it.name }
                showSelectorDialog(
                    getString(R.string.please_select_transmission),
                    stringList
                ) {
                    viewModel.setTransmissionType(list[it])
                    getBasicTable().tvTransmissionValue.text = list[it].name
                }
            }

            is BasicEditorStatus.GearTypeList -> {
                val list = status.list
                val stringList = list.map { it.name }
                showSelectorDialog(
                    getString(R.string.please_select_gear),
                    stringList
                ) {
                    viewModel.setGearType(list[it])
                    getBasicTable().tvGearValue.text = list[it].name
                }
            }

            is BasicEditorStatus.FuelTypeList -> {
                val list = status.list
                val stringList = list.map { it.name }
                showSelectorDialog(
                    getString(R.string.please_select_fuel_type),
                    stringList
                ) {
                    viewModel.setFuelType(list[it])
                    getBasicTable().tvGasValue.text = list[it].name
                }
            }

            is BasicEditorStatus.EngineDisplacementItemList -> {
                val list = status.list
                val stringList = list.map { it.name }
                showSelectorDialog(
                    getString(R.string.please_select_displacement),
                    stringList
                ) {
                    viewModel.setEngineDisplacement(list[it])
                    getBasicTable().tvEngineDisplacementValue.text = list[it].name
                }
            }

            is BasicEditorStatus.PassengerItemList -> {
                val list = status.list
                val stringList = list.map { it.name }
                showSelectorDialog(
                    getString(R.string.please_select_passenger_number),
                    stringList
                ) {
                    viewModel.setPassenger(list[it])
                    getBasicTable().tvPassengerValue.text = list[it].name
                }
            }

            is BasicEditorStatus.ColorTypeList -> {
                val list = status.list
                val stringList = list.map { it.name }
                showSelectorDialog(
                    getString(R.string.please_select_color),
                    stringList
                ) {
                    viewModel.setColorType(list[it])
                    getBasicTable().tvColorValue.text = list[it].name
                }
            }

            is BasicEditorStatus.HotCerNoResult -> {
                HotCertificateResultDialog(
                    result = status.boolean,
                    message = status.message
                ).show(childFragmentManager, "HotCertificateResultDialog")
            }

            is BasicEditorStatus.VehicleNo -> {

                VerifyVehicleNoResultDialog(
                    errorMessage = status.errorMessage,
                    isShowUploadDesc = status.showUploadDesc
                ).show(childFragmentManager, "VerifyVehicleNoResultDialog")

                if (status.showUploadDesc) {
                    // 顯示行照星號
                    getLicenseTable().tvLicenseStar.visibility = View.VISIBLE
                }
            }

            // 車身檢查 ErrorCode 9998
            // 修改鈕停留於 Step 1，仍要下一步進入 Step 2
            is BasicEditorStatus.VehicleNumberEdit -> {
                EditorVehicleNoDescDialog(
                    message = status.message,
                    onNextEvent = {
                        gotoNextStep()
                    }
                ).show(childFragmentManager, "EditorVehicleNoDescDialog")
            }

            // 車身檢查 ErrorCode 9996
            // 顯示確認按鈕，無法進入 Step 2
            is BasicEditorStatus.VehicleNumberBlock -> {
                EditorVehicleNoDescDialog(
                    isBlockStep = true,
                    message = status.message
                ).show(childFragmentManager, "EditorVehicleNoDescDialog")
            }


            is BasicEditorStatus.FileUploadError -> {
                val message = status.message
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }

            is BasicEditorStatus.PriceErrorDesc -> {
                val lowerError = status.isShowLowerError

                getPriceTable().tvCeilingLowerThenBasicErrorDesc.apply {
                    visibility = if (lowerError) View.VISIBLE else View.GONE
                }

                val rangeError = status.isShowRangeError
                val maxPrice = status.maxPrice
                getPriceTable().tvPriceRangeErrorDesc.apply {
                    visibility = if (rangeError) View.VISIBLE else View.GONE
                    text = String.format(getString(R.string.basic_price_over_error_desc, maxPrice))
                }
            }

            BasicEditorStatus.Pass -> {
                gotoNextStep()
            }

            BasicEditorStatus.Loading -> {
                showLoadingDialog()
            }

            BasicEditorStatus.LoadingFinish -> {
                hideLoadingDialog()
            }

            is BasicEditorStatus.ResponseMessage -> { // 訊息
                val message = status.message
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }

            is BasicEditorStatus.ErrorMessage -> {
                checkErrorCode(errorCode = status.errorCode, message = status.message)
            }
        }
    }


    private fun parseUnqualified(list: List<UnqualifiedResponse>) {
        val descList = list.map { it.desc }

        if (descList.isEmpty()) {
            binding.llErrorDescGroup.visibility = View.GONE
        } else {
            binding.llErrorDescGroup.visibility = View.VISIBLE
            descListAdapter.setList(descList)
        }
    }

    // 清單選擇Dialog
    private fun showSelectorDialog(
        title: String,
        list: List<String>,
        onItemSelectIndex: (Int) -> Unit
    ) {
        EditorListSelectorDialog(
            title,
            list,
            onItemSelectIndex
        ).show(childFragmentManager, "EditorListSelectorDialog")
    }


    // https://developer.android.com/training/data-storage/shared/photopicker
    private fun pickupLicenseImageFile() {
        licenseImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun pickupLicensePdfFile() {
        licensePdfPicker.launch(arrayOf("application/pdf"))
    }

    private fun pickupImportImageFile() {
        importDocImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun pickupImportPdfFile() {
        importDocPdfPicker.launch(arrayOf("application/pdf"))
    }

    // ImagePicker
    private fun getImagePickerRegister(
        onCallback: (Uri?) -> Unit
    ): ActivityResultLauncher<PickVisualMediaRequest> {
        return registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            onCallback(uri)
        }
    }

    // PDF Picker
    private fun getPdfPickerRegister(
        onCallback: (Uri?) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            onCallback(uri)
        }
    }

    private fun initFilePickerRegister() {

        licenseImagePicker = getImagePickerRegister { uri ->
            uri?.let {
                val mimeType = requireContext().contentResolver.getType(it)
                when (mimeType) {
                    "image/jpeg" -> {
                        viewModel.uploadLicenseFile(it, "jpg", requireContext())
                        binding.licenseTable.tvLicenseFileDesc.text = it.path
                    }

                    "image/png" -> {
                        viewModel.uploadLicenseFile(it, "png", requireContext())
                        binding.licenseTable.tvLicenseFileDesc.text = it.path
                    }

                    else -> Toast.makeText(
                        requireContext(),
                        "不支援 $mimeType 格式",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        licensePdfPicker = getPdfPickerRegister { uri ->
            uri?.let {
                val mimeType = requireContext().contentResolver.getType(it)
                if (mimeType == "application/pdf") {
                    viewModel.uploadLicenseFile(it, "pdf", requireContext())
                    binding.licenseTable.tvLicenseFileDesc.text = it.path
                } else {
                    Toast.makeText(requireContext(), "不支援 $mimeType 格式", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        importDocImagePicker = getImagePickerRegister { uri ->
            uri?.let {
                val mimeType = requireContext().contentResolver.getType(it)
                when (mimeType) {
                    "image/jpeg" -> {
                        viewModel.uploadImportFile(it, "jpg", requireContext())
                        binding.licenseTable.tvImportFileDesc.text = it.path
                    }

                    "image/png" -> {
                        viewModel.uploadImportFile(it, "png", requireContext())
                        binding.licenseTable.tvImportFileDesc.text = it.path
                    }

                    else -> Toast.makeText(
                        requireContext(),
                        "不支援 $mimeType 格式",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        importDocPdfPicker = getPdfPickerRegister { uri ->
            uri?.let {
                val mimeType = requireContext().contentResolver.getType(it)
                if (mimeType == "application/pdf") {
                    viewModel.uploadImportFile(it, "pdf", requireContext())
                    binding.licenseTable.tvImportFileDesc.text = it.path
                } else {
                    Toast.makeText(requireContext(), "不支援 $mimeType 格式", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun needCheckVehicleNo(info: CarInfo): Boolean {
        return when {
            info.vehicleNoCheckStatus == 2 && info.vehicleNoReview == 0 && info.transferCheckId == 0 -> {
                // 補正確車身號碼
                true
            }

            info.vehicleNoCheckStatus == 0 && info.vehicleNoReview == 0 && info.transferCheckId == 0 -> {
                // 缺車身號碼
                true
            }

            else -> {
                false
            }
        }
    }
}