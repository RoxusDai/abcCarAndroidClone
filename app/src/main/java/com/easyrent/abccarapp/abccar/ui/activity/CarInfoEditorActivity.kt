package com.easyrent.abccarapp.abccar.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.easyrent.abccarapp.abccar.databinding.ActivityCarInfoEditorBinding
import com.easyrent.abccarapp.abccar.viewmodel.editor.EditorActivityViewModel

/**
 *  車輛資訊上架與編輯Activity
 *
 *  使用 ShareViewModel 供底下 Fragment 整合表單資訊
 *
 *   - 上架 ( 空表格 )
 *   - 修改 ( 帶入表單資訊 )
 */

class CarInfoEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarInfoEditorBinding

    private val shareViewMode: EditorActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = intent.extras
        args?.let {
            val carId = it.getInt("carId", 0)
            val editmode = it.getInt("editmode",0)
            shareViewMode.setCarId(carId)
            shareViewMode.setEditMode(editmode)
        }

        binding = ActivityCarInfoEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}