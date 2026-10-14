package org.schabi.newpipe.brave.feature.logcat

import android.content.Context
import androidx.preference.PreferenceManager
import com.github.logviewer.LogcatDumper
import java.util.Date
import org.schabi.newpipe.App
import org.schabi.newpipe.R
import org.schabi.newpipe.brave.feature.logcat.BraveLogcatDumper.Companion.CAPTURE_PERIOD_BEFORE_TIMESTAMP

/**
 * Dump the logcat entries for [CAPTURE_PERIOD_BEFORE_TIMESTAMP] ms before a given timestamp.
 *
 * The timestamp is the point in time at which the crash occurred.
 *
 * Used in case of ACRA catches a crash to fetch the current logcat entries that might help
 * to actually see what happened before the crash in [org.schabi.newpipe.error.ErrorActivity]
 */
class BraveLogcatDumper {

    /**
     * we want the timestamp to set the filename.
     */
    class BraveLogFileNameErrorInfo : BraveLogFileName(), LogcatDumper.LogFileNameFromTimestamp {

        private var timestamp: Long = 0

        override fun getLogFileName(): String {
            return dateFormat.format(Date(timestamp))
        }

        override fun setTimestamp(timestamp: Long) {
            this.timestamp = timestamp
        }
    }

    companion object {
        val logcatDump = LogcatDumper(
            App.instance,
            BraveLogFileNameErrorInfo(),
            BraveLogFileFormat(),
            logCleanupStrategy = commonCleanupStrategy(),
            logStorageLocation = commonStorageLocation()
        )

        private const val CAPTURE_PERIOD_BEFORE_TIMESTAMP: Long = 30 * 1000

        fun triggerLogCapture(): Long {
            val time = System.currentTimeMillis()
            if (isLogcatDumperEnabled()) {
                ensureLogFolderMarker(App.instance)
                logcatDump.dump(time, CAPTURE_PERIOD_BEFORE_TIMESTAMP)
            }
            return time
        }

        fun isLogcatDumperEnabled(context: Context = App.instance) = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(
                context.getString(R.string.brave_settings_debug_logcat_dumper_key),
                true
            )
    }
}
