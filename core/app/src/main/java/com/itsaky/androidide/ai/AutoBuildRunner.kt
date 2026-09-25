package com.itsaky.androidide.ai

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Self-Healing Auto Build & Delivery Runner
 */
object AutoBuildRunner {

    fun locateDebugApk(projectRoot: File): File? {
        val standardPath = File(projectRoot, "app/build/outputs/apk/debug")
        if (standardPath.exists()) {
            val apk = standardPath.listFiles()?.firstOrNull { it.name.endsWith(".apk") }
            if (apk != null) return apk
        }
        val rootBuildPath = File(projectRoot, "build/outputs/apk/debug")
        if (rootBuildPath.exists()) {
            val apk = rootBuildPath.listFiles()?.firstOrNull { it.name.endsWith(".apk") }
            if (apk != null) return apk
        }
        return null
    }

    fun locateReleaseAab(projectRoot: File): File? {
        val aabPath = File(projectRoot, "app/build/outputs/bundle/release")
        if (aabPath.exists()) {
            return aabPath.listFiles()?.firstOrNull { it.name.endsWith(".aab") }
        }
        return null
    }

    fun launchPackageInstaller(context: Context, apk: File) {
        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apk
        )
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(installIntent)
    }

    fun shareAabBundle(context: Context, aab: File) {
        val aabUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            aab
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, aabUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share AAB Bundle"))
    }
}
