package com.example.lw_4

import android.os.Parcel
import android.os.Parcelable

class Laptop: Parcelable {
    var ID: Int = 0
    var manufacturerName: String = ""
    var HDDVolume: Int = 0
    var SSDPresent: Boolean = false
    var RAMVolume: Int = 0
    var isFHD: Boolean = false
    var screenTime: Int = 0
    var count = 0

    constructor(ID: Int, manufacturerName: String, HDDVolume: Int, SSDPresent: Boolean, RAMVolume: Int, isFHD: Boolean, screenTime: Int) {
        this.ID = ID
        this.manufacturerName = manufacturerName
        this.HDDVolume = HDDVolume
        this.SSDPresent = SSDPresent
        this.RAMVolume = RAMVolume
        this.isFHD =isFHD
        this.screenTime = screenTime
    }

    constructor(ID: Int, manufacturerName: String, HDDVolume: Int, SSDPresent: Boolean, RAMVolume: Int, isFHD: Boolean, screenTime: Int, count: Int) {
        this.ID = ID
        this.manufacturerName = manufacturerName
        this.HDDVolume = HDDVolume
        this.SSDPresent = SSDPresent
        this.RAMVolume = RAMVolume
        this.isFHD =isFHD
        this.screenTime = screenTime
        this.count = count
    }

    constructor() {}

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readByte() != 0.toByte(), // для Boolean
        parcel.readInt(),
        parcel.readByte() != 0.toByte(), // для Boolean
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ID)
        parcel.writeString(manufacturerName)
        parcel.writeInt(HDDVolume)
        parcel.writeByte(if (SSDPresent) 1 else 0)
        parcel.writeInt(RAMVolume)
        parcel.writeByte(if (isFHD) 1 else 0)
        parcel.writeInt(screenTime)
        parcel.writeInt(count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Laptop> {
        override fun createFromParcel(parcel: Parcel): Laptop {
            return Laptop(parcel)
        }

        override fun newArray(size: Int): Array<Laptop?> {
            return arrayOfNulls(size)
        }
    }
}