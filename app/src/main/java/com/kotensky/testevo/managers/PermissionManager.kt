package com.kotensky.testevo.managers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import com.kotensky.testevo.R
import com.kotensky.testevo.application.TestEvoApplication
import javax.inject.Inject

class PermissionManager @Inject constructor(private val context: Context){

    companion object {
        const val PERMISSIONS_REQUEST_WRITE_EXTERNAL = 23
    }

    fun checkFileSystemPermissions(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true
        activity.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_WRITE_EXTERNAL)
        return false
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_WRITE_EXTERNAL -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(R.string.need_permission_title)
                    builder.setMessage(R.string.need_permission_message)
                    builder.setPositiveButton(R.string.need_permission_positive, { dialog, which ->
                        openSettings()
                        dialog.dismiss()
                    })
                    builder.setNegativeButton(R.string.need_permission_negative, { dialog, which ->
                        TestEvoApplication.applicationComponent.sharedPreferencesManager().setDeniedCallPermissionRequest()
                        dialog.dismiss()
                    })
                    builder.create().show()
                }
            }
        }
    }

    private fun openSettings () {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data = Uri.parse("package:" + context.packageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(intent)
    }
}