package com.easyrent.abccarapp.abccar.ui.fragment.editor.state

import com.easyrent.abccarapp.abccar.remote.response.CountyItem
import com.easyrent.abccarapp.abccar.remote.response.DistrictItem
import com.easyrent.abccarapp.abccar.remote.response.GetContactPersonListResp

sealed class ContactEditorStatus {

    data object Loading : ContactEditorStatus()

    class DetailContactList(
        val list: List<GetContactPersonListResp>
    ) : ContactEditorStatus()

    class ContactList(
        val list: GetContactPersonListResp
    ) : ContactEditorStatus()

    class CountyList(
        val list: List<CountyItem>
    ) : ContactEditorStatus()

    class DistrictList(
        val list: List<DistrictItem>
    ) : ContactEditorStatus()

    class AddContact(
        val success: Boolean
    ) : ContactEditorStatus()

    data object AddCarFinish : ContactEditorStatus()

    data object AddCarAndPutOnAdvertisingBoardFinish : ContactEditorStatus()

    data object EditDone : ContactEditorStatus()

    class ErrorMessage(
        val errorCode: String,
        val message: String
    ) : ContactEditorStatus()

}