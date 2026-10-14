package org.schabi.newpipe.brave.feature.logcat

import com.github.logviewer.LogFileFormat
import com.github.logviewer.LogFileName
import com.github.logviewer.LogItem
import com.github.logviewer.Settings
import java.io.BufferedWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.schabi.newpipe.App
import org.schabi.newpipe.BuildConfig

object BraveLogcatUiSetup {

    fun setLogcatOutputFileFormat() {
        ensureLogFolderMarker(App.instance)
        Settings.Default.update { current ->
            current.copy(
                logfileFormat = BraveLogFileFormat(),
                logFileName = BraveLogFileName(),
                logCleanupStrategy = commonCleanupStrategy(),
                logStorageLocation = commonStorageLocation()
            )
        }
    }
}

/**
 * have a more github friendly logcat format
 */
class BraveLogFileFormat : LogFileFormat {
    override fun writeLogs(
        logFileName: String,
        logs: Array<LogItem>,
        writer: BufferedWriter
    ) {
        if (logs.isNotEmpty()) {
            writer.write("<details><summary><b>Logcat: $logFileName")
            writer.write("</b>")
            writer.write("</summary><p>\n")
            writer.write("\n```\n")
            for (log in logs) {
                writer.write(log.origin + "\n")
            }
            writer.write("\n```\n")
            writer.write("</details>\n")
            writer.write("<hr>\n")
        }
    }
}

open class BraveLogFileName : LogFileName {
    protected val dateFormat =
        SimpleDateFormat("'${BuildConfig.FLAVOR}_'yyyy-MM-dd_HH-mm-ss-SSS'.log'", Locale.ROOT)

    override fun getLogFileName(): String {
        return dateFormat.format(Date())
    }
}
