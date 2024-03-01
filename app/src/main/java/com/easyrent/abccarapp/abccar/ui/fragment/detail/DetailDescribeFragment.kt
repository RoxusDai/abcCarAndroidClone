package com.easyrent.abccarapp.abccar.ui.fragment.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.easyrent.abccarapp.abccar.databinding.FragmentDetailDescribeBinding
import com.easyrent.abccarapp.abccar.ui.adapter.detail.DetailDescribePicAdapter
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.viewmodel.detail.CarDetailViewModel


class DetailDescribeFragment : BaseFragment() {
    var _binding : FragmentDetailDescribeBinding? = null
    private val binding get() = _binding!!
    private val shareViewModel : CarDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailDescribeBinding.inflate(inflater, container, false)
        initView()
        initLiveData()
        return binding.root
    }

    private fun initLiveData() {
    }

    private fun initView() {
        val descript = shareViewModel.getEditCarInfoResp()?.carInfo?.description ?: ""
        if(descript.isNotEmpty()){
            val matchres = parceUrls(descript)
            val doc = stringToHtml( removeUrls(descript).replace("\uFFFD", ""))

            if (matchres != null) {
//                Log.e("detaildescribepic",matchres.groupValues.toString())
//                Log.e("detaildescribe",doc.toString())
                val piclist = matchres.groupValues
                binding.rvDescribeImage.adapter = DetailDescribePicAdapter(piclist){
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    startActivity(intent)
                }
                binding.rvDescribeImage.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            }
            else{
                //no picture in describe
            }

            binding.tvDetailDescribe.text = doc?.filter{it.isLetterOrDigit()}
        }
        else binding.tvDetailDescribe.text = "尚未有任何描述"
    }


    fun stringToHtml(string: String?): Spanned? {
        return try {
            Html.fromHtml(string)
        } catch (e:Exception){
            e.message as Spanned
        }

    }
    fun parceUrls(text: String):MatchResult?  {
        val urlPattern = """(https?://\S+|www\.\S+)""".toRegex()
        return urlPattern.find(text)
    }
    fun removeUrls(text :String) :String{
        val urls = parceUrls(text)?.groupValues
        var retext = text
        if(!urls.isNullOrEmpty()){
         urls.forEach {
             retext.replace(it,"")
         }
        }
        return retext
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}