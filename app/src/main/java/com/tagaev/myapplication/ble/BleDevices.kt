package com.tagaev.myapplication.ble

enum class BleDevices(var address: String, var uuid: String) {
    EMULATOR1(address = "94:B9:7E:E8:E6:C2", uuid = "beb5483e-36e1-4688-b7f5-ea07361b26a8"),
    AUTONOMOUS1(address = "", uuid = "beb54202-36e1-4688-b7f5-ea07361b26a8"),
}