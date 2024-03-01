package com.easyrent.abccarapp.abccar.ui.fragment.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.FragmentEditorContactInfoBinding
import com.easyrent.abccarapp.abccar.manager.AccountInfoManager
import com.easyrent.abccarapp.abccar.manager.editor.contact.ContactInfoTable
import com.easyrent.abccarapp.abccar.remote.request.AddContactPersonReq
import com.easyrent.abccarapp.abccar.remote.response.GetContactPersonListResp
import com.easyrent.abccarapp.abccar.tools.DateFormatTool
import com.easyrent.abccarapp.abccar.ui.adapter.ContactListAdapter
import com.easyrent.abccarapp.abccar.ui.dialog.EditorCloseConfirmDialog
import com.easyrent.abccarapp.abccar.ui.dialog.EditorListSelectorDialog
import com.easyrent.abccarapp.abccar.ui.dialog.WarningDescDialog
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.ContactEditorStatus
import com.easyrent.abccarapp.abccar.viewmodel.editor.ContactInfoViewModel
import com.easyrent.abccarapp.abccar.viewmodel.editor.EditorActivityViewModel
import org.json.JSONObject
import java.util.Date

/**
 *
 *  Step 5 - 聯絡資料
 *
 *  清單與建立畫面先用GroupView實作，避免使用 ViewPager2 要處理ChildFragment之間複雜的資料交換程序
 *  後續可以使用同一組 Layout 替換成 TabLayout + ViewPager2
 */
class EditorContactInfoFragment : BaseFragment() {

    private var _binding: FragmentEditorContactInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactInfoViewModel by viewModels { ContactInfoViewModel.Factory }

    private val shareViewModel: EditorActivityViewModel by activityViewModels()

    private val contactListAdapter = ContactListAdapter {
        WarningDescDialog(
            title =  "已選擇3名",
            message = "最多可選擇3名物件聯絡人"
        ).show(childFragmentManager, "WarningDescDialog")
    }

    private val dateFormatter = DateFormatTool()

    private fun getListGroup() = binding.contactGroup
    private fun getCreateGroup() = binding.createGroup

    private fun getToolbar() = binding.topToolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditorContactInfoBinding.inflate(inflater, container, false)

        initView()
        initLiveData()
        initContactInfo()

        return binding.root
    }


    private fun initContactInfo() {
        viewModel.getContactList()
    }

    private fun initLiveData() {
        viewModel.statusLiveData.observe(viewLifecycleOwner) { status ->
            if (status !is ContactEditorStatus.Loading) {
                hideLoadingDialog()
            }

            when(status) {
                is ContactEditorStatus.ContactList -> {
                    if(status.list.isNotEmpty()){
                        initContactList(status.list)
                    }
                    else { }
                }
                is ContactEditorStatus.AddContact -> {
                    if (status.success) {

                        Toast.makeText(requireContext(), "新增成功", Toast.LENGTH_SHORT).show()
                        binding.tvListButton.performClick() // 新增成功回到通訊錄
                        clearCreateTable()
                    } else {
                        Toast.makeText(requireContext(), "新增失敗", Toast.LENGTH_SHORT).show()
                    }
                }
                is ContactEditorStatus.ErrorMessage -> {
                    checkErrorCode(status.errorCode, status.message)
                }

                is ContactEditorStatus.CountyList -> {
                    val countyList = status.list
                    EditorListSelectorDialog(
                        title = "請選擇縣市",
                        list = countyList.map { it.name }
                    ) { index ->
                        val item = countyList[index]
                        viewModel.setCountyItem(item)
                        getCreateGroup().tvCityCountyValue.text = item.name
                    }.show(childFragmentManager, "EditorListSelectorDialog")
                }
                is ContactEditorStatus.DistrictList -> {
                    val districtList = status.list

                    if (status.list.isEmpty()) {
                        Toast.makeText(requireActivity(), "請先選擇縣市", Toast.LENGTH_SHORT).show()
                    } else {
                        EditorListSelectorDialog(
                            title = "請選擇區域",
                            list = districtList.map { it.name }
                        ) { index ->
                            val item = districtList[index]
                            viewModel.setDistrictItem(districtList[index])
                            getCreateGroup().tvDistrictValue.text = item.name
                        }.show(childFragmentManager, "EditorListSelectorDialog")
                    }
                }

                ContactEditorStatus.EditDone -> {
                    val mixpanel = initialMixpanel()
                    mixpanel.track("save edited car", JSONObject().put("carId" , shareViewModel.getCarId()).put("memberId",AccountInfoManager.getMemberInfo().memberId)) //step5編輯
                    Toast.makeText(requireActivity(), "編輯完成", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }

                ContactEditorStatus.AddCarFinish -> {
                    val mixpanel = initialMixpanel()
                    mixpanel.track("addcar", JSONObject().put("carId" , shareViewModel.getCarId()).put("memberId",AccountInfoManager.getMemberInfo().memberId))//step5新增車輛 並上架
                    Toast.makeText(requireActivity(), "新增為待售物件", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }

                ContactEditorStatus.AddCarAndPutOnAdvertisingBoardFinish -> {
                    val mixpanel = initialMixpanel()
                    mixpanel.track("addCarAndPutOnAdvertisingBoard", JSONObject().put("carId" , shareViewModel.getCarId()).put("memberId",AccountInfoManager.getMemberInfo().memberId))//step5新增車輛 為待售
                    Toast.makeText(requireActivity(), "(待審核)新增物件並上架", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }

                ContactEditorStatus.Loading -> showLoadingDialog()
                else -> {}
            }
        }
    }

    private fun initContactList(list: GetContactPersonListResp) {
        val mixpanel = initialMixpanel()
        mixpanel.track("Reload contact tapped")//更新step5聯絡人

        val checkedIds = if (shareViewModel.getCarId() > 0) {
            shareViewModel.getEditCarInfoResp()?.carInfo?.memberContactIDs ?: emptyList()
        } else {
            shareViewModel.getContactInfoTable().memberContactIDs
        }
        if (checkedIds.isNotEmpty()) {
            list.forEach { item ->
                checkedIds.forEach { checked ->
                    if (item.id == checked) {
                        item.isChecked = true
                    }
                }
            }
        }
        contactListAdapter.setList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initView() {

        // 設定標題
        getToolbar().tvTitle.text = "Step 5 - 聯絡資料"

        // 離開建立物件程序顯示提示Dialog
        getToolbar().ivCloseButton.setOnClickListener {
            EditorCloseConfirmDialog().show(childFragmentManager, "CloseConfirmDialog")
        }

        getToolbar().llBackButtonGroup.setOnClickListener {
            findNavController().popBackStack()
        }

        getListGroup().rvContactList.apply {
            adapter = contactListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        getCreateGroup().btCreateContact.setOnClickListener {

            val mixpanel = initialMixpanel()
            mixpanel.track("New Contact")//step5 點擊新增聯絡人

            val nowString = dateFormatter.dateToString(Date())

            if (checkInfo()) {

                val req = AddContactPersonReq(
                    memberID = AccountInfoManager.getMemberInfo().memberId, //    "MemberID":18384,
                    name = getCreateGroup().edName.text.toString(), //    "Name":"ado",
                    areaCode = getCreateGroup().edAreaCode.text.toString(), //    "AreaCode":"",
                    telephone = getCreateGroup().edTelephoneNumber.text.toString(), //    "Telephone":"",
                    ext = getCreateGroup().edPhoneExtNumber.text.toString(), //    "Ext":"",
                    cellPhone = getCreateGroup().edCellPhoneNumber.text.toString(), //    "CellPhone":"1234567890",
                    lineID = getCreateGroup().edLineId.text.toString(), //    "LineID":"",
                    lineUrl = getCreateGroup().edLineUrl.text.toString(), //    "LineUrl":"",
                    countryName = getCreateGroup().tvCityCountyValue.text.toString(), //    "CountryName":"高雄市",
                    districtName = getCreateGroup().tvDistrictValue.text.toString(), //    "DistrictName":"仁武區",
                    address = getCreateGroup().edAddressInput.text.toString(), //    "Address":"",
                    createDate =  nowString, //    "CreateDate":"2023-09-07T13:32:00",
                    modifyDate = nowString //    "ModifyDate":"2023-09-07T13:32:00"
                )
                viewModel.addContactPerson(req)
            }
        }

        if (shareViewModel.getCarId() > 0) {
            getListGroup().llCreateItemButton.visibility = View.GONE
            getListGroup().btEditItem.visibility = View.VISIBLE
        } else {
            getListGroup().llCreateItemButton.visibility = View.VISIBLE
            getListGroup().btEditItem.visibility = View.GONE
        }

        binding.tvListButton.setOnClickListener {
            initContactInfo()
            it.setBackgroundColor(
                ContextCompat.getColor( requireContext(), R.color.light_gray)
            )
            binding.tvCreateButton.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )

            getListGroup().root.visibility = View.VISIBLE
            getCreateGroup().root.visibility = View.GONE
        }

        binding.tvCreateButton.setOnClickListener {
            it.setBackgroundColor(
                ContextCompat.getColor( requireContext(), R.color.light_gray)
            )
            binding.tvListButton.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )

            getListGroup().root.visibility = View.GONE
            getCreateGroup().root.visibility = View.VISIBLE
        }

        getCreateGroup().tvCityCountyValue.setOnClickListener {
            viewModel.getCountyList()
        }

        getCreateGroup().tvDistrictValue.setOnClickListener {
            viewModel.getDistrictList()
        }

        getListGroup().btCreateContact.setOnClickListener {

            // 儲存聯絡人
            val contentList = contactListAdapter.getList().filter { it.isChecked }.map { it.id }
            shareViewModel.setContactInfoTable(ContactInfoTable(memberContactIDs = contentList))

            if (contentList.isEmpty()) {
                Toast.makeText(requireContext(), "請至少選擇一名聯絡人", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 呼叫 API
            callAddCar()
        }

        getListGroup().btCreateAndPublish.setOnClickListener {


            // 儲存聯絡人
            val contentList = contactListAdapter.getList().filter { it.isChecked }.map { it.id }
            shareViewModel.setContactInfoTable(ContactInfoTable(memberContactIDs = contentList))

            if (contentList.isEmpty()) {
                Toast.makeText(requireContext(), "請至少選擇一名聯絡人", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            callAddCarAndPutOnAdvertisingBoard()
        }

        getListGroup().btEditItem.setOnClickListener {
            // 儲存聯絡人
            val contentList = contactListAdapter.getList().filter { it.isChecked }.map { it.id }
            shareViewModel.setContactInfoTable(ContactInfoTable(memberContactIDs = contentList))

            if (contentList.isEmpty()) {
                Toast.makeText(requireContext(), "請至少選擇一名聯絡人", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            callEditItem()
        }
    }


    private fun clearCreateTable() {
        getCreateGroup().apply {
            edName.text.clear()
            edCellPhoneNumber.text.clear()
            edAreaCode.text.clear()
            edTelephoneNumber.text.clear()
            edPhoneExtNumber.text.clear()
            edLineId.text.clear()
            edLineUrl.text.clear()
            tvCityCountyValue.text = "- - -"
            tvDistrictValue.text = "- - -"
            edAddressInput.text.clear()
            viewModel.clearTable()
        }
    }


    // 檢查必填狀態是否合格
    private fun checkInfo(): Boolean {
        val name = getCreateGroup().edName.text.isNotBlank()
        val cellPhone = getCreateGroup().edCellPhoneNumber.text.isNotBlank()
        val area = viewModel.isCountyDistrictPass()
        val address = getCreateGroup().edAddressInput.text.isNotBlank()

        val status = name && cellPhone && area && address


        if (!status) {
            WarningDescDialog(
                title = "欄位為空",
                message = "星號欄位不可為空"
            ).show(childFragmentManager, "WarningDescDialog")
        }

        return name && cellPhone && area && address
    }

    private fun callAddCar() {
        val req = shareViewModel.getAddReq()
        viewModel.addCar(req)
    }

    private fun callAddCarAndPutOnAdvertisingBoard() {
        val req = shareViewModel.getAddCarAndPutOnAdvertisingBoardReq()
        viewModel.addCarAndPutOnAdvertisingBoard(req)
    }


    private fun callEditItem() {
        val req = shareViewModel.getEditCarReq()
        viewModel.editCar(req)
    }

}