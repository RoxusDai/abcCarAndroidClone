package com.easyrent.abccarapp.abccar.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.AdapterCarInfoItemBinding
import com.easyrent.abccarapp.abccar.remote.response.CarEdit
import com.easyrent.abccarapp.abccar.dataType.EditCarStatus
import com.easyrent.abccarapp.abccar.tools.DateFormatTool
import java.util.Calendar

class CarInfoListAdapter(
    private val onItemEditorClicked: (CarEdit, Boolean) -> Unit,
    private val onItemPublishClicked: (CarEdit) -> Unit,
    private val onDetailClicked: (CarEdit) -> Unit,
    private val onReserveClicked: (CarEdit) -> Unit,
    private val onDescClicked: (String, String) -> Unit
) : RecyclerView.Adapter<CarInfoListAdapter.CarInfoViewHolder>() {

    private var sortMode = 1
    private val carInfoList = mutableListOf<CarEdit>()

    //variable for filter
    private var carInfoFilteredList = mutableListOf<CarEdit>()

    //過濾狀態
    private val statusList: Map<Int, Int> = mapOf<Int, Int>(
        Pair(-999, -1), ///預設(後臺查詢)
        Pair(-21, 3), ///受訂(過戶)
        Pair(-20, 1),///受訂(上架)
        Pair(-19, 2),///審查失敗(待售)
        Pair(-18, 4),///不收費,以下架超過2個月
        Pair(-17, 3),///已成交-非成交下架車輛排程過戶掃描下架
        Pair(-16, 3),//已成交-上架中車輛排程過戶掃描下架
        Pair(-15, 4),///已成交,透過其他管道
        Pair(-14, 3),//已成交,透過ABC
        Pair(-13, 4),//會員刪除車輛
        Pair(12, -1),//範本
        Pair(-11, 4),//流標
        Pair(-10, 4),//車輛被下架或預覽後重新編輯
        Pair(-9, -1),//預覽
        Pair(-8, -1),//車輛停權
        Pair(-7, -1),//車輛假刪除
        Pair(-6, -1),///會員帳號停權
        Pair(-5, -1),//草稿
        Pair(-4, -1),//審核失敗
        Pair(-3, -1),//棄標
        Pair(-2, -1),//車身號碼錯誤
        Pair(-1, 0),//待審核
        Pair(0, 2),///待售
        Pair(1, 1),///刊登中
        Pair(2, 3),//售出
        Pair(3, 3),//截標
        Pair(99, 0)///回報成交
    )

    private val dateTool = DateFormatTool()

    private var searchText = ""
    private var carSourceFilter = 1
    var carBrandIDFilter = 0
    var carCategoryFilter = 0
    var carStatusFilter = 0

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        carInfoList.clear()
        carInfoFilteredList.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(list: List<CarEdit>) {
        list.forEach {
            carInfoList.remove(it)
            carInfoFilteredList.remove(it)
        }
        carInfoList.addAll(list)

        if (carInfoFilteredList.size == 0) {
            //初始list
            carInfoFilteredList = filterCarInfo(carInfoList)
            sortCarList()
            notifyDataSetChanged()
        } else {
            //先增加list
            val changeStartIndex = carInfoFilteredList.size
            carInfoFilteredList.addAll(filterCarInfo(list))
//            notifyDataSetChanged()
            notifyItemRangeChanged(changeStartIndex, list.size) // 告知更新的範圍
            // 避免使用notifyDataSetChanged()方法，以節省效能
        }
    }

    //重設車單過濾
    fun resetFilterList() {
        searchText = ""
        carSourceFilter = 1
        carBrandIDFilter = 0
        carCategoryFilter = 0
        carStatusFilter = 0
        carInfoFilteredList = filterCarInfo(carInfoList)
        sortCarList()
    }

    //設定搜尋過濾
    @SuppressLint("NotifyDataSetChanged")
    fun searchFilterList(text: String?) {
        searchText = if (text!!.length > 2) text else ""
        carInfoFilteredList = filterCarInfo(carInfoList)
        sortCarList()
        notifyDataSetChanged()

    }

    //設定來源過濾
    @SuppressLint("NotifyDataSetChanged")
    fun sourceFilterList(type: Int) {
        carSourceFilter = type
        carInfoFilteredList = filterCarInfo(carInfoList)
        sortCarList()
        notifyDataSetChanged()
    }

    //設定車廠過濾
    @SuppressLint("NotifyDataSetChanged")
    fun brandFilterList(type: Int) {
        carBrandIDFilter = type
        carInfoFilteredList = filterCarInfo(carInfoList)
        sortCarList()
        notifyDataSetChanged()
    }

    //設定車類過濾
    @SuppressLint("NotifyDataSetChanged")
    fun categoryFilterList(type: Int) {
        carCategoryFilter = type
        carInfoFilteredList = filterCarInfo(carInfoList)
        sortCarList()
        notifyDataSetChanged()
    }

    //設定狀態過濾
    @SuppressLint("NotifyDataSetChanged")
    fun statusFilterList(type: Int) {
        carStatusFilter = type
        carInfoFilteredList = filterCarInfo(carInfoList)
        sortCarList()
        notifyDataSetChanged()
    }

    fun getCurrentList(): List<CarEdit> = carInfoFilteredList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarInfoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterCarInfoItemBinding.inflate(layoutInflater, parent, false)

        return CarInfoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return carInfoFilteredList.size
    }

    fun setSortMode(mode: Int) {
        sortMode = mode
    }

    fun sortCarList() {
        when (sortMode) {
            1 -> {
                carInfoFilteredList.sortByDescending { it.modifyDate }
            }

            2 -> {
                carInfoFilteredList.sortByDescending { it.carID }
            }

            3 -> {
                carInfoFilteredList.sortBy { it.price }
            }

            4 -> {
                carInfoFilteredList.sortByDescending { it.status }
            }
        }
    }

    override fun onBindViewHolder(holder: CarInfoViewHolder, position: Int) {

        val info = carInfoFilteredList[position]

        // 車輛ID
        holder.binding.tvCarId.text = info.carID.toString()

        // 車輛名稱
        holder.binding.tvCarModelName.text = getTitle(info)

        // 售價
        holder.binding.carPrice.text = "${info.price} 萬"

        // 車輛狀態
        val status = EditCarStatus.parser(info.status)

        // 左下角標籤字串
        holder.binding.tvPublishStatus.text = getCarStatusTag(status, info)

        // 左下角標籤顏色
        setStatusTagColor(holder, status, info)

        // 上架日期
        setPublishDate(status, info, holder)

        // 右上角審核狀態，包含字串與顯示
        setVehicleNoStatus(holder, status, info)

        // 設定詳細按鈕clicked事件
        setDetailButtonStatus(holder, status, info)

        // 設定編輯按鈕Clicked事件
        setEditButtonStatus(holder, status, info)

        // 設定上架按鈕Clicked事件
        setPublishButtonStatus(holder, status, info)

        //設定受訂按鈕
        setReserveButtonStatus(holder,status, info)

        val url = info.imageUrl
        Glide.with(holder.binding.root)
            .load(url)
            .into(holder.binding.carImage)
    }

    //過濾更新車單
    @SuppressLint("NotifyDataSetChanged")
    private fun filterCarInfo(input: List<CarEdit>): MutableList<CarEdit> {

        //filtered by carSource
        val carInfoTempList =
            input.filter { checkSource(it.source as String?) == carSourceFilter }.toMutableList()

        //filtered by spinner
        if (carBrandIDFilter != 0) carInfoTempList.retainAll(carInfoTempList.filter { it.brandID == carBrandIDFilter })
        if (carCategoryFilter != 0) carInfoTempList.retainAll(carInfoTempList.filter { it.seriesID == carCategoryFilter })

        //status filter
        //過濾有設定
        if (carStatusFilter == 0) carInfoTempList.retainAll(carInfoTempList.filter { statusList[it.status]!! >= 0 })
        else carInfoTempList.retainAll(carInfoTempList.filter { statusList[it.status] == carStatusFilter })

        //filtered by search text
        if (searchText.length > 2) {
            carInfoTempList.retainAll(searchTextFilter(carInfoTempList))
        }
        return carInfoTempList.toMutableList()

    }

    private fun searchTextFilter(list: List<CarEdit>): List<CarEdit> {
        return list.filter { !it.searchBrandName.isNullOrBlank() || !it.searchCategoryName.isNullOrEmpty() }
            .filter {
                it.carID.toString().contains(searchText, true)
                        || it.searchBrandName.contains(searchText, true)
                        || it.searchCategoryName.contains(searchText, true)
                        || it.seriesName.contains(searchText, true)
                        || it.manufactureYear.contains(searchText, true)
            }
    }

    private fun setDetailButtonStatus(
        holder: CarInfoViewHolder,
        status: EditCarStatus,
        info: CarEdit
    ) {
        holder.binding.btDetail.apply {
            when (status) {
                is EditCarStatus.Discontinued, is EditCarStatus.Already -> {
                    this.visibility = View.VISIBLE
                    setOnClickListener {
                        onDetailClicked.invoke(info)
                    }
                }
                else -> {
                    this.visibility = View.GONE
                    setOnClickListener {
                        onDetailClicked.invoke(info)
                    }
                }
            }
        }
    }

    private fun setEditButtonStatus(
        holder: CarInfoViewHolder,
        status: EditCarStatus,
        info: CarEdit
    ) {
        holder.binding.btEdit.apply {
            // 車輛狀態只有待售和上架中 才可以編輯
            // 車輛 待審核 皆不可編輯
            when (status) {
                // 0 or 1
                is EditCarStatus.Discontinued, is EditCarStatus.Already -> {

                    val haveNoReviewBool =
                        if (status is EditCarStatus.Discontinued) (info.vehicleNoReview == 1 || info.vehicleNoReview == 3)
                        else (info.vehicleNoReview == 1)
                    //0 or 1 待審核

                    if (info.vehicleNoCheckStatus != 0 && info.transferCheckId == 0 && haveNoReviewBool) {
                        this.visibility = View.VISIBLE

                        setOnClickListener {
                            onDescClicked.invoke("無法編輯", "待審核")
                        }
                    } else if (info.onlineDescription == 1) {
                        //受訂
                        this.visibility = View.GONE
                    }
                    //開啟編輯頁面
                    else {
                        this.visibility = View.VISIBLE
                        setOnClickListener {
                            onItemEditorClicked.invoke(info, true)
                        }
                    }
                }

                else -> {
                    //非待售和上架中
                    setOnClickListener {
                        onDescClicked.invoke("無法編輯", "非待售或上架中")
                    }
                }
            }
        }
    }

    private fun setPublishButtonStatus(
        holder: CarInfoViewHolder,
        status: EditCarStatus,
        info: CarEdit
    ) {
        holder.binding.btPublish.apply {
            when (status) {
                is EditCarStatus.Already -> {
                    //上架中,將按鈕設定為下架
                    holder.binding.tvPublishButtonText.text = "下架"
                    holder.binding.ivPublishButtonIcon.setImageResource(R.drawable.ic_car_info_disemble)
                    setOnClickListener {
                        onItemPublishClicked.invoke(info)
                    }
                    if (info.onlineDescription == 1) {
                        // 上架且受訂，隱藏該按鈕
                        this.visibility = View.GONE
                    }else{
                        this.visibility = View.VISIBLE
                    }
                }
                is EditCarStatus.Discontinued -> {

                    // 補正車身號碼 & 待審核 皆不可上架
                    val publishBool = (info.vehicleNoCheckStatus == 2 ||info.vehicleNoCheckStatus == 0) && info.vehicleNoReview == 0 && info.transferCheckId == 0
                            || (info.vehicleNoCheckStatus != 1 && (info.vehicleNoReview == 1 || info.vehicleNoReview == 3) && info.transferCheckId == 0 )

                    this.visibility = View.VISIBLE
                    holder.binding.tvPublishButtonText.text = "上架"
                    holder.binding.ivPublishButtonIcon.setImageResource(R.drawable.ic_publish_24px)

                    when {
                        publishBool-> {
                            setOnClickListener {
                                onDescClicked.invoke(
                                    "無法上架",
                                    getRightTopTagString(status, info)
                                )
                            }
                        }
                        else -> {
                            // 上述情況皆非，則 true
                            setOnClickListener { onItemPublishClicked.invoke(info) }
                        }
                    }
                }


                else -> {
                    this.visibility = View.VISIBLE
                    holder.binding.tvPublishButtonText.text = "上架"
                    holder.binding.ivPublishButtonIcon.setImageResource(R.drawable.ic_publish_24px)
                    // 車輛狀態只要不是待售 皆不可上架
                    setOnClickListener {
                        onDescClicked.invoke("無法上架", status.desc)
                    }
                }
            }
        }
    }

    private fun setReserveButtonStatus(
        holder: CarInfoViewHolder,
        status: EditCarStatus,
        info: CarEdit
    ) {
        when(status){
            is EditCarStatus.Already->{
                holder.binding.btReserve.visibility = View.VISIBLE
                //只有上架中才能受訂
                //受訂function
                if(info.onlineDescription == 1){
                    //取消受訂
                    holder.binding.tvCarReserve.text = "取消受訂"
                    holder.binding.btReserve.setOnClickListener{
                        onReserveClicked.invoke(info)
                    }
                }
                else{
                    holder.binding.tvCarReserve.text = "受訂"
                    holder.binding.btReserve.setOnClickListener {
                        onReserveClicked.invoke(info)
                    }
                }
            }
            else->{
                //其他情況,隱藏
                holder.binding.btReserve.visibility = View.GONE
            }
        }

    }

    private fun setStatusTagColor(holder: CarInfoViewHolder, status: EditCarStatus, info: CarEdit) {
        // is EditCarStatus.Already -> if (info.onlineDescription == 1) { "受訂中" } else { "上架中" } // 1
        holder.binding.tvPublishStatus.apply {
            if (status is EditCarStatus.Already && info.onlineDescription == 1) {
                setBackgroundColor(resources.getColor(R.color.main_red, null))
                setTextColor(ContextCompat.getColor(this.context, R.color.white))
            } else {
                setBackgroundColor(resources.getColor(status.colorRes, null))
                setTextColor(ContextCompat.getColor(this.context, status.textColorRes))
            }
        }

    }

    // 如果為上架狀態，則顯示上架日期 ( 非待售即顯示上架日期 )
    private fun setPublishDate(status: EditCarStatus, info: CarEdit, holder: CarInfoViewHolder) {
        if (status is EditCarStatus.Discontinued) {
            holder.binding.tvPublishDate.apply {
                visibility = View.GONE
                text = ""
            }
        } else {
            holder.binding.tvPublishDate.apply {
                visibility = View.VISIBLE
                text = String.format(
                    ContextCompat.getString(context, R.string.start_date_desc),
                    info.startDate.substringBefore("T")
                )
            }
        }
    }

    // 左下角狀態字串
    private fun getCarStatusTag(status: EditCarStatus, info: CarEdit): String {
        return when (status) {
            is EditCarStatus.Discontinued -> status.desc // 0
            is EditCarStatus.Already -> if (info.onlineDescription == 1) {
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


    // 依照對應狀態設定車身號碼審核狀態 ( 右上角狀態 )
    private fun setVehicleNoStatus(
        holder: CarInfoViewHolder,
        status: EditCarStatus,
        info: CarEdit
    ) {
        holder.binding.tvVehicleNoStatus.apply {
            val statusString = getRightTopTagString(status, info)
            if (statusString.isBlank()) {
                visibility = View.GONE
                text = ""
            } else {
                visibility = View.VISIBLE
                text = statusString
            }
        }
    }

    // 右上角標籤字串，空字串為不顯示
    fun getRightTopTagString(status: EditCarStatus, info: CarEdit): String {
        return when (status) {
            is EditCarStatus.Discontinued -> { // 0
                if (info.vehicleNoCheckStatus == 2 && info.vehicleNoReview == 0 && info.transferCheckId == 0) {
                    "補正確車身號碼"
                } else
                    if (info.vehicleNoCheckStatus == 0 && info.vehicleNoReview == 0 && info.transferCheckId == 0) {
                        "缺車身號碼"
                    } else
                        if (info.vehicleNoCheckStatus != 1 && (info.vehicleNoReview == 1 || info.vehicleNoReview == 3) && info.transferCheckId == 0) {
                            "待審核"
                        } else {
                            "" // 上述皆不符合則回傳空字串
                        }
            }

            is EditCarStatus.Already -> {
                // (VehicleNoLen > 0 || CarSourceType === 2) && VehicleNoCheckStatus === 1 && VehicleNoReview === 0 && TransferCheckId === 0 && !OverApril
                if ((info.vehicleNoLen > 0 || info.carSourceType == 2)
                    && info.vehicleNoCheckStatus == 1 && info.vehicleNoReview == 0 && info.transferCheckId == 0 && !isOverApril(
                        info
                    )
                ) {
                    "免收費"
                } else
                // VehicleNoCheckStatus === 2 && VehicleNoReview === 0 && TransferCheckId === 0 && !OverApril
                    if (info.vehicleNoCheckStatus == 2 && info.vehicleNoReview == 0 && info.transferCheckId == 0 && !isOverApril(
                            info
                        )
                    ) {
                        "免收費"
                    } else
                    // VehicleNoCheckStatus === 0 && VehicleNoReview === 0 && TransferCheckId === 0 && !OverApril
                        if (info.vehicleNoCheckStatus == 0 && info.vehicleNoReview == 0 && info.transferCheckId == 0 && !isOverApril(
                                info
                            )
                        ) {
                            "免收費"
                        } else
                        // VehicleNoCheckStatus !== 1 && VehicleNoReview ===1 && TransferCheckId === 0
                            if (info.vehicleNoCheckStatus != 1 && info.vehicleNoReview == 1 && info.transferCheckId == 0) {
                                "待審核"
                            } else
                            // (VehicleNoLen > 0 || CarSourceType === 2) && VehicleNoCheckStatus === 1 && VehicleNoReview === 0 && TransferCheckId > 0
                                if ((info.vehicleNoLen > 0 || info.carSourceType == 2) && info.vehicleNoCheckStatus == 1 && info.vehicleNoReview == 0 && info.transferCheckId > 0) {
                                    "已驗證收費"
                                } else {
                                    "" // 上述皆不符合則回傳空字串
                                }
            }

            is EditCarStatus.SoldPause -> {
                // (VehicleNoLen > 0 || CarSourceType === 2) && VehicleNoCheckStatus === 1 && VehicleNoReview === 0 && TransferCheckId > 0
                if ((info.vehicleNoLen > 0 || info.carSourceType == 2) && info.vehicleNoCheckStatus == 1 && info.vehicleNoReview == 0 && info.transferCheckId > 0) {
                    "已驗證收費"
                } else {
                    "" // 上述皆不符合則回傳空字串
                }
            }

            else -> "" // 上述皆非則回傳空字串
        }

    }

    private fun checkSource(source: String?): Int = when (source) {
        null, "" -> 1 //abc
        "32", "HAA" -> 2//外部車源
        "12", "13" -> 3 //簡易刊登
        else -> 1
    }

    fun getTitle(it: CarEdit): String = "${it.seriesName} ${it.categoryName} ${it.manufactureYear}"

    // 檢查是否超過4月
    private fun isOverApril(info: CarEdit): Boolean {
        // 尚未有上架時間
        if (info.startDate == "0001-01-01T00:00:00") return true

        val date = dateTool.formatterToDate(info.startDate)

        val theDate = Calendar.getInstance().apply {
            set(Calendar.MONTH, 5) // 五月
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return date?.after(theDate) ?: true
    }

    class CarInfoViewHolder(
        val binding: AdapterCarInfoItemBinding
    ) : ViewHolder(binding.root)
}