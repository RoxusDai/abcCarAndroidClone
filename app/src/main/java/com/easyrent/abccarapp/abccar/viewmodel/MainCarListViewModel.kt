package com.easyrent.abccarapp.abccar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.easyrent.abccarapp.abccar.manager.unit.EditCarPageInfo
import com.easyrent.abccarapp.abccar.manager.EditListRefreshManager
import com.easyrent.abccarapp.abccar.manager.editor.colum.CarInfoPair
import com.easyrent.abccarapp.abccar.remote.ApiClient
import com.easyrent.abccarapp.abccar.remote.ApiResponse
import com.easyrent.abccarapp.abccar.remote.request.BatchUpdateCarAdvertisingBoardReq
import com.easyrent.abccarapp.abccar.remote.request.GetCarBrandSeriesCategoryReq
import com.easyrent.abccarapp.abccar.remote.request.GetMainCarListReq
import com.easyrent.abccarapp.abccar.remote.request.UpdateMemberCarsStatusReq
import com.easyrent.abccarapp.abccar.repository.mainCarList.MainCarListRepository
import com.easyrent.abccarapp.abccar.repository.mainCarList.MainCarListRepositoryImp
import com.easyrent.abccarapp.abccar.source.main.carList.MainCarListSourceImp
import com.easyrent.abccarapp.abccar.ui.fragment.state.CarInfoListState
import com.easyrent.abccarapp.abccar.ui.fragment.state.FilterListState
import kotlinx.coroutines.launch

class MainCarListViewModel(
    private val repository: MainCarListRepository
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<CarInfoListState>()
    val stateLiveData: LiveData<CarInfoListState> get() = _stateLiveData


    //liveData for filter
    private val _filterLiveData = MutableLiveData<FilterListState>()
    val filterLiveData: LiveData<FilterListState> get() = _filterLiveData

    private val editCarListManager = EditListRefreshManager()
    private var refreshLock = false // API刷新鎖，避免Load More重複呼叫

    //過濾器清單
    var brandList :MutableList<CarInfoPair> = mutableListOf<CarInfoPair>()
    var categoryList :MutableList<CarInfoPair> = mutableListOf<CarInfoPair>()

    val statusShowList = arrayListOf("選擇狀態","上架中","待售","過戶","下架")
    // List下拉更新，重設所有狀態
    fun refreshCarList() {
        editCarListManager.reset()
        getCarList(editCarListManager.getPageIndex())
    }

    // 載入更多，觸發EditCarManager，
    fun loadMoreCarList() {
        editCarListManager.apply {
            // 如果還有下一頁
            if (haveNextPage()) {
                this.increasePage() // 頁數++
                getCarList(this.getPageIndex()) // 用新的頁數Index呼叫API
            }
        }
    }

    // 檢查是否載入更多車輛Item
    fun isMoreEditCarInfo(
        currentListPosition: Int,
        currentListLastIndex: Int,
    ): Boolean {
        val pageInfo = editCarListManager.getPageInfo()
        return currentListPosition == currentListLastIndex &&
                pageInfo.totalPages >= pageInfo.pageIndex
    }

    //取得車輛規格資訊
    private fun getCarList(
        pageIndex: Int
    ) {
        if (refreshLock) return // 如果鎖定，則跳過API呼叫

        refreshLock = true // API呼叫鎖定

        viewModelScope.launch {
            _stateLiveData.value = CarInfoListState.Loading

            val resp = repository.getCarInfoList(
                GetMainCarListReq(
                    event = 0,
                    pageIndex = pageIndex,
                    pageSize = 600
                )
            )

            when(resp) {
                is ApiResponse.Success -> {
                    // 更新API List status
                    resp.data.pagination?.let {
                        editCarListManager.setPageInfo(
                            EditCarPageInfo(
                                pageIndex = it.page,
                                totalPages = it.totalPage,
                                totalCount = it.totalCount
                            )
                        )
                    }
                    // 將新載入的List
                    _stateLiveData.value = CarInfoListState.GetList(resp.data.carEditList)

                }
                is ApiResponse.ApiFailure -> {
                    _stateLiveData.value = CarInfoListState.Error(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }

                is ApiResponse.NetworkError -> {
                    _stateLiveData.value = CarInfoListState.Error(
                        errorCode = resp.statusCode,
                        message = resp.message
                    )
                }
            }
            refreshLock = false // 完成API呼叫程序，則釋放狀態鎖
        }
    }


    private fun getCarBrandCategory(brandID : Int){
        viewModelScope.launch {
            val resp = repository.getCarBrandSeriesCategory(
                GetCarBrandSeriesCategoryReq(brandID,0)
            )

            when(resp) {
                is ApiResponse.Success -> {
                    //取得車廠
                    if(brandID == 0){
                        val spinner_list = arrayListOf<String>()
                        brandList.add(CarInfoPair(0,"全部"))
                        spinner_list.add("選擇車廠")
                        resp.data.let {
                            it.forEach {
                                brandList.add(CarInfoPair(it.brandID,it.brandName))
                                spinner_list.add(it.brandName)
                            }
                        }
                        _filterLiveData.value = FilterListState.GetBrandList(spinner_list)
                    }
                    //取得車型,車款
                    else{
                        val spinner_list = arrayListOf<String>()
                        categoryList.add(CarInfoPair(0,"全部"))
                        spinner_list.add("選擇車款")
                        resp.data.let {
                            it.forEach {
                                categoryList.add(CarInfoPair(it.seriesID,it.seriesName))
                                spinner_list.add(it.seriesName)
                            }
                        }
                        _filterLiveData.value = FilterListState.GetCategoryList(spinner_list)
                    }

                }
                is ApiResponse.ApiFailure -> {
                    _filterLiveData.value = FilterListState.Error(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }
                is ApiResponse.NetworkError -> {
                    _filterLiveData.value = FilterListState.Error(
                        errorCode = resp.statusCode,
                        message = resp.message
                    )
                }
            }
        }
    }

    //取得過濾器list
    fun getBrandFilterList(){
        getCarBrandCategory(0)
    }
    fun getCategoryList(carBrand : Int){
        getCarBrandCategory(carBrand)
    }

    fun updateMemberCarsStatus(req: BatchUpdateCarAdvertisingBoardReq) {
        viewModelScope.launch {
            //status跟actionstatus相同 上架1 下架-15
            val resp = repository.batchUpdateCarAdvertisingBoard(req)

            _stateLiveData.value = when(resp) {
                is ApiResponse.Success -> {
                    CarInfoListState.PublishDone(resp.data)
                }
                is ApiResponse.ApiFailure -> {
                    CarInfoListState.Error(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }
                is ApiResponse.NetworkError -> {
                    CarInfoListState.Error(
                        errorCode = resp.statusCode,
                        message = resp.message
                    )
                }
            }
        }
    }

    fun batchUpdateCarAdvertisingBoard(req: BatchUpdateCarAdvertisingBoardReq){
        viewModelScope.launch {
            val resp = repository.batchUpdateCarAdvertisingBoard(req)
            _stateLiveData.value= when(resp){
                is ApiResponse.Success->{
                    CarInfoListState.ReserveDone(req.OnlineDescription)
                }
                is ApiResponse.ApiFailure->{
                    CarInfoListState.Error(
                        errorCode = resp.errorCode,
                        message = resp.message
                    )
                }
                is ApiResponse.NetworkError->{
                    CarInfoListState.Error(
                        errorCode = resp.statusCode,
                        message = resp.message
                    )
                }
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val apiClient = ApiClient.instance
                val apiSource = MainCarListSourceImp(apiClient)
                val repository = MainCarListRepositoryImp(apiSource)

                return MainCarListViewModel(repository) as T
            }
        }
    }
}