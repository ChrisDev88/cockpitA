package de.datatrain.cockpita.app

import android.content.Context
import android.content.Intent
import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler
import java.lang.Exception
import java.util.*

import java.util.LinkedList;
import java.util.Queue;

class ErrorPresenterByNotification(currentContext: Context): ErrorPresenter {

    private val context: Context = currentContext.applicationContext

    override fun presentError(errorTitle: String, errorDetail: String, exception: Exception?, isFatal: Boolean) {
        val startNotification = Intent(context, ErrorNotificationDialog::class.java)
        startNotification.putExtra(ErrorNotificationDialog.TITLE, errorTitle)
        startNotification.putExtra(ErrorNotificationDialog.MSG, errorDetail)
        var logString = "$errorTitle: $errorDetail"
        if(isFatal) logString = "Fatal - $logString"
        startNotification.putExtra(ErrorNotificationDialog.FATAL, isFatal)
        startNotification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        synchronized(notificationIntentQueue){
            if(!isErrorDialogShowing){
                context.startActivity(startNotification)
                isErrorDialogShowing = true
            }
            else notificationIntentQueue.add(startNotification)
        }
    }

    companion object {
        private var notificationIntentQueue: Queue<Intent> = LinkedList<Intent>()
        private var isErrorDialogShowing = false

        fun errorDialogDismissed(){
            synchronized(notificationIntentQueue) {
                val nextIntent = notificationIntentQueue.poll()
                if (nextIntent != null) {
                    AppLifecycleCallbackHandler.getInstance().activity!!.startActivity(nextIntent)
                } else {
                    isErrorDialogShowing = false
                }
            }
        }
    }
}