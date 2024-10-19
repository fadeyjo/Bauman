package com.example.lw_4

class Laptop {
    var ID: Int = 0
    var manufacturerName: String = ""
    var HDDVolume: Int = 0
    var SSDPresent: Boolean = false
    var RAMVolume: Int = 0
    var isFHD: Boolean = false
    var screenTime: Int = 0

    constructor(ID: Int, manufacturerName: String, HDDVolume: Int, SSDPresent: Boolean, RAMVolume: Int, isFHD: Boolean, screenTime: Int) {
        this.ID = ID
        this.manufacturerName = manufacturerName
        this.HDDVolume = HDDVolume
        this.SSDPresent = SSDPresent
        this.RAMVolume = RAMVolume
        this.isFHD =isFHD
        this.screenTime = screenTime
    }
}