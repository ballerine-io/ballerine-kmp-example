package io.ballerine.kmp.example.android.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtil {

    /**
     * Check if Permission is granted
     *
     * @return true if specified permission is granted
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        val selfPermission = ContextCompat.checkSelfPermission(context, permission)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if Specified Permission is defined in AndroidManifest.xml file or not.
     * If permission is defined in manifest then return true else return false.
     *
     * @param context Application Context
     * @param permission String Permission Name
     *
     * @return true if permission defined in AndroidManifest.xml file, else return false.
     */
    fun isPermissionInManifest(context: Context, permission: String): Boolean {
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        )
        val permissions = packageInfo.requestedPermissions

        if (permissions.isNullOrEmpty())
            return false

        for (perm in permissions) {
            if (perm == permission)
                return true
        }

        return false
    }
}
