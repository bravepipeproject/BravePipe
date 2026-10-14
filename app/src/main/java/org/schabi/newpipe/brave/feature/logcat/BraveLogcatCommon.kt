package org.schabi.newpipe.brave.feature.logcat

import android.content.Context
import com.github.logviewer.ExportLogFileUtils
import com.github.logviewer.LogConfig
import com.github.logviewer.settings.CleanupConfig
import com.github.logviewer.settings.KeepLastNFilesStrategy
import java.io.File

/**
 * have same cleanup strategy within logcat-toolkit' UI calls and its logcat dumper.
 */
fun commonCleanupStrategy(): CleanupConfig = CleanupConfig(KeepLastNFilesStrategy(), 10)

/**
 * use common log storage location within logcat-toolkit' UI calls and its logcat dumper.
 */
fun commonStorageLocation(): ExportLogFileUtils.StorageLocation = ExportLogFileUtils.StorageLocation.CACHE_INTERNAL

/**
 * logcat-toolkit crashes if its log folder exists without the marker file, e.g. after cache trimming.
 */
fun ensureLogFolderMarker(context: Context) {
    val parentDir = if (commonStorageLocation() == ExportLogFileUtils.StorageLocation.CACHE_INTERNAL) {
        context.cacheDir
    } else {
        context.externalCacheDir
    } ?: return
    val logDir = File(parentDir, LogConfig.RELATIVE_LOG_DIR)
    if (logDir.isDirectory) {
        runCatching { File(logDir, LogConfig.LOGCAT_TOOLKIT_MARKER_FILE).createNewFile() }
    }
}
