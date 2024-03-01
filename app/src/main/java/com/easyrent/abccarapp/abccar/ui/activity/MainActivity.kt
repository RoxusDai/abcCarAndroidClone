package com.easyrent.abccarapp.abccar.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.ActivityMainBinding

/**
 *  主頁Activity
 *   - 車輛清單 ( 預計 )
 *   - 帳號資訊 ( 預計使用BottomSheet )
 *   - 左側DrawerMenu
 *
 *  Navigation框架可參考
 *   - https://developer.android.com/guide/navigation
 */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val memberId = savedInstanceState?.getInt("memberId") ?:0
        Log.e("mainActivity",memberId.toString())
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        supportActionBar?.title = "車輛刊登管理"


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment
        navHostFragment.arguments?.putInt("memberId",memberId)
        val navController = navHostFragment.navController


        val appBarConf = AppBarConfiguration(navController.graph)

        binding.toolBar.setupWithNavController(navController, appBarConf)

    }

}