package com.easyrent.abccarapp.abccar.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.easyrent.abccarapp.abccar.databinding.FragmentCarSortBinding

class CarSortFragment : BaseFragment() {
    private var _binding: FragmentCarSortBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarSortBinding.inflate(inflater, container, false)

        initMenu()
        initView()

        return binding.root
    }
    fun initView(){
        val navController = findNavController()
        val currMode  = arguments?.getInt("sortMode")
        when(currMode){
            1->{
                binding.ivSortLastdayChecked.visibility = View.VISIBLE
            }
            2->{
                binding.ivSortIdChecked.visibility = View.VISIBLE
            }
            3->{
                binding.ivSortPriceChecked.visibility = View.VISIBLE
            }
            4->{
                binding.ivSortStatusChecked.visibility = View.VISIBLE
            }
        }

        binding.llSortLastday.setOnClickListener {
            setFragmentResult("sortMode",bundleOf("sortMode" to 1))
            navController.popBackStack()
        }
        binding.llSortCarID.setOnClickListener {
            setFragmentResult("sortMode",bundleOf("sortMode" to 2))
            navController.popBackStack()
        }
        binding.llSortPrice.setOnClickListener {
            setFragmentResult("sortMode",bundleOf("sortMode" to 3))
            navController.popBackStack()
        }
        binding.llSortStatus.setOnClickListener {
            setFragmentResult("sortMode",bundleOf("sortMode" to 4))
            navController.popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}