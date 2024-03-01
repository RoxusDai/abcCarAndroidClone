package com.easyrent.abccarapp.abccar.ui.fragment.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.easyrent.abccarapp.abccar.databinding.FragmentDetailSpecBinding
import com.easyrent.abccarapp.abccar.manager.editor.basic.SpecTypesManager
import com.easyrent.abccarapp.abccar.ui.adapter.detail.DetailSpecAdapter
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.detail.state.DetailApiStatus
import com.easyrent.abccarapp.abccar.ui.fragment.editor.state.BasicEditorStatus
import com.easyrent.abccarapp.abccar.viewmodel.detail.CarDetailViewModel
import com.easyrent.abccarapp.abccar.viewmodel.detail.DetailSpecViewModel
import java.text.SimpleDateFormat


class DetailSpecFragment : BaseFragment() {
    var _binding : FragmentDetailSpecBinding? = null
    private val binding get() = _binding!!
    private val carSourceList = listOf("abc車源","外部車源","簡易刊登車源")
    private val shareViewModel : CarDetailViewModel by activityViewModels()
    private val viewModel: DetailSpecViewModel by viewModels(){
        DetailSpecViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailSpecBinding.inflate(inflater, container, false)
        initView()
        initLiveData()
        return binding.root
    }

    private fun initView() {
    }

    private fun initLiveData() {
        shareViewModel.tableLiveData.observe(viewLifecycleOwner){status->
            when(status){
                is DetailApiStatus.InitMedia->{
                    //car information
                    viewModel.initSpecTable(status.resp)
                    binding.tvDetailViewed.text = shareViewModel.viewCount.toString()
                    binding.tvDetailLike.text = shareViewModel.favoriteCount.toString()

                    binding.ivDetailLiked.setOnClickListener{}
                }
                else->{

                }
            }
        }
        viewModel.specLiveData.observe(viewLifecycleOwner){status->
            if(status is SpecTypesManager){
                binding.rvDetailSpec.adapter = DetailSpecAdapter(MaKeSpecList(status))
                binding.rvDetailSpec.layoutManager = GridLayoutManager(
                    requireContext(),3
                )
                shareViewModel.setSpecDone()
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "WeekBasedYear")
    private fun MaKeSpecList(manager : SpecTypesManager) : List<Pair<String, String>> {
        val info = shareViewModel.getEditCarInfoResp()!!.carInfo
        val list = mutableListOf<Pair<String, String>>()

        ///出廠年月

        val date = SimpleDateFormat("yyyy-MM").parse(info.manufactureDate.substring(0,7))

        list.add(Pair("出廠年月", "${date.year+1900}年${date.month+1}月"))
        //行駛里程
        list.add(Pair("行駛里程","${info.mileage} km"))


        ///avoid index out of bound
        if(info.gasTypeID<1) list.add(Pair("燃料/排氣","${manager.getGasTypes()[0].name} / ${info.engineDisplacement}"))
        else list.add(Pair("燃料/排氣","${manager.getGasTypes()[info.gasTypeID-1].name} / ${info.engineDisplacement}"))

        if(info.color<1)list.add(Pair("顏色","${manager.getColorTypes()[0].name}"))
        else list.add(Pair("顏色","${manager.getColorTypes()[info.color-1].name}"))

        if(info.gearTypeID <1) list.add(Pair("變速系統","${manager.getGearTypes()[0].name}"))
        else list.add(Pair("變速系統","${manager.getGearTypes()[info.gearTypeID-1].name}"))

        if(info.transmissionID <1) list.add(Pair("傳動系統","${manager.getTransmissionTypes()[0].name}"))
        else list.add(Pair("傳動系統","${manager.getTransmissionTypes()[info.transmissionID-1].name}"))

        list.add(Pair("乘坐人數","${info.passenger}人"))


        list.add(Pair("平輸車",if(isFlat(info.feature)) "是" else "不是"))

        list.add(Pair("車身號碼","${info.vehicleNo}"))

        list.add(Pair("車源","${carSourceList[info.carSourceType-1]}"))

        return list
    }

    fun isFlat(input : Int) : Boolean{
        val out = Integer.toBinaryString(input)
        return if(out.length>5)(out.get(out.length-6) == '1') else false///平輸車
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}