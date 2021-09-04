package com.fyp.smartbus.api

import java.util.*


//enum class AccountType {
//    STUDENT, DRIVER
//}

data class User (
        var email: String,
        var password: String="",
        var username: String?=null,
        var busno: String? = null,
        var usertype: String?=null
)
data class Bus(
        var email: String,
        var isonline : Boolean? = null,
        var currentloc : String? = null,
        var busno: String? = null
)
//{
//
//    companion object {
//        // Used in HomeFragment for TodayScheduleRvAdapter
//        @JvmStatic
//        fun getAccountFromString(accType: String?): AccountType? {
//            if (accType == null) return null
//            val acc = accType.lowercase(Locale.getDefault())[0]
//            return when (acc) {
//                'd' -> AccountType.DRIVER
//                's' -> AccountType.STUDENT
//                else -> null
//            }
//        }
//    }
//}