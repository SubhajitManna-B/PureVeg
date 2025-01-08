package com.example.pureveg.ui.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class OrderDetailsModel(): Serializable{
    var userId: String? = null
    var userName: String? = null
    var foodNames: MutableList<String>? = null
    var foodImages: MutableList<String>? = null
    var foodPrices: MutableList<String>? = null
    var foodQuantities: MutableList<Int>? = null
    var address: String? = null
    var phoneNo: String? = null
    var totalPrice: String? = null
    var orderAccepted: Boolean = false
    var paymentReceived: Boolean = false
    var itemPushKey: String? = null
    var currentTime: Long = 0

    constructor(parcel: Parcel) : this() {
        userId = parcel.readString()
        userName = parcel.readString()
        address = parcel.readString()
        phoneNo = parcel.readString()
        totalPrice = parcel.readString()
        orderAccepted = parcel.readByte() != 0.toByte()
        paymentReceived = parcel.readByte() != 0.toByte()
        itemPushKey = parcel.readString()
        currentTime = parcel.readLong()
    }

    constructor(
        userId: String,
        name: String,
        foodName: ArrayList<String>,
        foodImage: ArrayList<String>,
        foodPrice: ArrayList<String>,
        foodQuantity: ArrayList<Int>,
        address: String,
        phone: String,
        totalAmount: String,
        b: Boolean,
        b1: Boolean,
        itemPushKey: String?,
        time: Long
    ) : this(){
        this.userId = userId
        this.userName = name
        this.foodNames = foodName
        this.foodImages = foodImage
        this.foodPrices = foodPrice
        this.foodQuantities = foodQuantity
        this.address = address
        this.phoneNo = phone
        this.totalPrice = totalAmount
        this.orderAccepted = b
        this.paymentReceived = b1
        this.itemPushKey = itemPushKey
        this.currentTime = time
    }

    fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(address)
        parcel.writeString(phoneNo)
        parcel.writeString(totalPrice)
        parcel.writeByte(if (orderAccepted) 1 else 0)
        parcel.writeByte(if (paymentReceived) 1 else 0)
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

    fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetailsModel> {
        override fun createFromParcel(parcel: Parcel): OrderDetailsModel {
            return OrderDetailsModel(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetailsModel?> {
            return arrayOfNulls(size)
        }
    }
}
