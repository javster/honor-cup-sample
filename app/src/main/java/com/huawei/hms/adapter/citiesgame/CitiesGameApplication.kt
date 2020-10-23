package com.huawei.hms.adapter.citiesgame

import android.app.Application

class CitiesGameApplication : Application() {

    var hiAnalytics: HiAnalyticsInstance? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this) == HuaweiConnectionResult.SUCCESS) {

        }
    }

    companion object {
        lateinit var instance: CitiesGameApplication
    }
}