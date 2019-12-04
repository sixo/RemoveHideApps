package eu.sisik.removehideapps

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Copyright (c) 2019 by Roman Sisik. All rights reserved.
 */
class DevAdminReceiver: DeviceAdminReceiver() {
    override fun onEnabled(context: Context?, intent: Intent?) {
        super.onEnabled(context, intent)
        Log.d(TAG, "Device Owner Enabled")
    }

    companion object {
        const val TAG = "DevAdminReceiver"
    }
}