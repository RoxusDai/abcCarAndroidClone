package com.easyrent.abccarapp.abccar.ui.fragment.detail

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.FragmentCarDetailBinding
import com.easyrent.abccarapp.abccar.manager.editor.basic.BasicInfoTable
import com.easyrent.abccarapp.abccar.manager.editor.photo.PhotoInfoTable
import com.easyrent.abccarapp.abccar.ui.adapter.unit.PhotoItem
import com.easyrent.abccarapp.abccar.ui.fragment.BaseFragment
import com.easyrent.abccarapp.abccar.ui.fragment.detail.state.DetailApiStatus
import com.easyrent.abccarapp.abccar.viewmodel.detail.CarDetailViewModel

class CarDetailFragment : BaseFragment() {
    private var _binding: FragmentCarDetailBinding? = null
    private val binding get() = _binding!!

    private val detailViewModel : CarDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCarDetailBinding.inflate(inflater, container, false)

        //設定basic info
        val carid = requireArguments().getInt("carId")
        detailViewModel.setID(carid)
        val basicinfo = BasicInfoTable(
            false,
            carid,
        )
        detailViewModel.setBasicInfoTable(basicinfo)
        detailViewModel.onlineDescribe = requireArguments().getInt("onlineDescription")
        detailViewModel.warnStatus = requireArguments().getString("warnStatus","")
        detailViewModel.viewCount = requireArguments().getInt("viewCount",0)
        detailViewModel.favoriteCount = requireArguments().getInt("favoriteCount",0)

        //設定照片list
        val photoList = mutableListOf<PhotoItem>()
        photoList.add(PhotoItem(requireArguments().getString("imageUrl",""),true))
        detailViewModel.setPhotoInfoTable(
            PhotoInfoTable(
                false,
                photoList
                ,requireArguments().getString("videourl","")
            ))

        initMenu()
        initView()
        initLiveData()

        return binding.root
    }

    private fun initLiveData() {
        detailViewModel.tableLiveData.observe(viewLifecycleOwner){
            when(it){
                is DetailApiStatus.InitFail->{
//                    findNavController().popBackStack()
                }
                else -> {}
            }
        }

    }

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

    private fun initView(){
        //設定fragment navgation
        val navHostFragment = childFragmentManager.findFragmentById(R.id.detail_fragment_container)as NavHostFragment
        val navController = navHostFragment.findNavController()
        val navOption = navOptions {
            this.launchSingleTop = true
        }

        binding.rbDetail1.setOnCheckedChangeListener { buttonView, isChecked ->
            //調整顯示
            radioButtonViewChange(buttonView,isChecked)
            if(isChecked){
                navController.popBackStack()
                navController.navigate(R.id.DetailAccessoriesFragment, null ,navOption)
            }
        }
        binding.rbDetail2.setOnCheckedChangeListener { buttonView, isChecked ->
            //調整顯示
            radioButtonViewChange(buttonView,isChecked)
            if(isChecked){
                navController.popBackStack()
                navController.navigate(R.id.DetailDescribeFragment,null , navOption )
            }
        }
        binding.rbDetail3.setOnCheckedChangeListener { buttonView, isChecked ->
            //調整顯示
            radioButtonViewChange(buttonView,isChecked)
            if(isChecked){
                navController.popBackStack()
                navController.navigate(R.id.DetailContactFragment,null,navOption)
            }
        }

        binding.btLineAdd.setOnClickListener {
            val lineUrl = "https://line.me/R/ti/p/@791tjewa"
            val intent = Intent.parseUri(lineUrl,Intent.URI_INTENT_SCHEME)
            startActivity(intent)
        }
    }

    private fun radioButtonViewChange( buttonView : CompoundButton, isChecked : Boolean){
        if (isChecked) {
            buttonView.background = resources.getDrawable(R.drawable.rb_checked)
            buttonView.setTextColor(resources.getColor(R.color.black))
        } else {
            buttonView.background = null
            buttonView.setTextColor(resources.getColor(R.color.mid_gray))
        }
    }
}