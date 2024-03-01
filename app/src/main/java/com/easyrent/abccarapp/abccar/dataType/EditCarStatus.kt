package com.easyrent.abccarapp.abccar.dataType

import com.easyrent.abccarapp.abccar.R

/**
 *  CarStatus 車輛狀態
 *
 *  將各狀態物件化，可作為一種Parser，使用when來判斷。
 */
sealed class EditCarStatus(
    val code: Int,
    val desc: String,
    val colorRes: Int = R.color.light_gray,
    val textColorRes: Int = R.color.black
) {
    class All : EditCarStatus(-999, "預設值")
    class SoldOutInPause : EditCarStatus(-21, "受訂中，排程過戶掃描下架") // 受訂中，排程過戶掃描下架
    class SoldPause : EditCarStatus(-20, "受訂中", R.color.main_red, R.color.white) // 受訂，暫停出售
    class ReviewFail : EditCarStatus(-19, "審查失敗")
    class SoldOutNotCharge : EditCarStatus(-18, "不收費、已下架超過兩個月", R.color.light_green)
    class SoldOutWithCarTransferOther : EditCarStatus(-17, "已成交-非成交下架車輛排程過戶掃描下架")
    class SoldOutWithCarTransfer : EditCarStatus(-16, "已成交-上架中車輛排程過戶掃描下架")
    class SoldOutWithUseOther : EditCarStatus(-15, "已成交-透過其他管道")
    class SoldOutWithUseAbcCar : EditCarStatus(-14, "已成交-透過abc好車網")
    class MemberDeletedCar : EditCarStatus(-13, "會員刪除車輛")
    class Sample : EditCarStatus(12, "範本")
    class FailureOfBid : EditCarStatus(-11, "流標")
    class DiscontinuedOrReEdit : EditCarStatus(-10, "車輛被下架或預覽後重新編輯")
    class Preview : EditCarStatus(-9, "預覽")
    class CarBlock : EditCarStatus(-8, "車輛停權")
    class CarFakeDelete : EditCarStatus(-7, "車輛假刪除")
    class MemberBlock : EditCarStatus(-6, "會員帳號停權")
    class Draft : EditCarStatus(-5, "草稿")
    class AuditFailure : EditCarStatus(-4, "審核失敗（退回）")
    class NonPayingBid : EditCarStatus(-3, "棄標")
    class ErrorVIN : EditCarStatus(-2, "車身號碼錯誤")
    class Pending : EditCarStatus(-1, "待審核")
    class Discontinued : EditCarStatus(0, "待售", R.color.yellow)
    class Already : EditCarStatus(1, "刊登中", R.color.green)
    class Sold : EditCarStatus(2, "售出")
    class AuctionClosed : EditCarStatus(3, "結標")
    class ReportDeal : EditCarStatus(99, "回報成交")
    class UnknownStatus : EditCarStatus(-99999, "未定義狀態")

    companion object {
        // parser 丟入 code 即可拿到對應的狀態物件
        fun parser(code: Int): EditCarStatus {
            return when(code) {
                -999 -> All()
                -21 -> SoldOutInPause()
                -20 -> SoldPause()
                -19 -> ReviewFail()
                -18 -> SoldOutNotCharge()
                -17 -> SoldOutWithCarTransferOther()
                -16 -> SoldOutWithCarTransfer()
                -15 -> SoldOutWithUseOther()
                -14 -> SoldOutWithUseAbcCar()
                -13 -> MemberDeletedCar()
                12 -> Sample()
                -11 -> FailureOfBid()
                -10 -> DiscontinuedOrReEdit()
                -9 -> Preview()
                -8 -> CarBlock()
                -7 -> CarFakeDelete()
                -6 -> MemberBlock()
                -5 -> Draft()
                -4 -> AuditFailure()
                -3 -> NonPayingBid()
                -2 -> ErrorVIN()
                -1 -> Pending()
                0 -> Discontinued()
                1 -> Already()
                2 -> Sold()
                3 -> AuctionClosed()
                99 -> ReportDeal()
                else -> UnknownStatus()
            }
        }
    }

// Backend 原始碼狀態
//enum CarStatus: Int, CaseIterable {
//    case a11 = -999//預設值（後台查詢預設值用）
//    case soldoutInpause = -21 //受訂中，排程過戶掃描下架
//    case soldpause = -20 //受訂，暫停出售
//    case reviewFai1 = -19 //審查失敗
//    case soldoutNotCharge = -18 //不收費、已下架超過兩個月
//    case soldoutwithCarTransferother = -17//已成交-非成交下架車輛排程過戶掃描下架
//    case soldoutwithCarTransfer = -16 //已成交-上架中車輛排程過戶掃描下架
//    case soldoutwithuseother = -15 //已成交-透過其他管道
//    case soldoutwithuseAbcCar = -14//已成交-透過abc好車網
//    case memberDeletedcar = -13//會員刪除車輛
//    case sample = 12 //範本
//    case failureofBid = -11//流標
//    case discontinuedorReEdit = -10 //車輛被下架或預覽後重新編輯
//    case preview = -9 //預覽
//    case carBlock = -8 //車輛停權
//    case carFakeDelete = -7//車輛假刪除
//    case memberBlock = -6 //會員帳號停權
//    case draft = -5 //草稿
//    case auditFailure = -4 //審核失敗（退回）
//    case nonPayingBid = -3 //棄標
//    case errorVIN = -2//車身號碼錯誤
//    case pending = -1//待審核
//    case discontinued = 0 //待售
//    case already = 1//刊登中
//    case sold = 2//售出
//    case auctionclosed = 3//結標
//    case reportDeal = 99//回報成交
}
