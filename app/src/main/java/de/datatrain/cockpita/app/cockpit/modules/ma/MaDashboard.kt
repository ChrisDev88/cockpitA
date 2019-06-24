package de.datatrain.cockpita.app.cockpit.modules.ma

import android.databinding.DataBindingUtil.setContentView
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar
import de.datatrain.cockpita.R
import de.datatrain.cockpita.app.cockpit.BaseActivity
import de.datatrain.cockpita.app.cockpit.home.HomeActivity

import kotlinx.android.synthetic.main.activity_ma_dashboard.*
import okhttp3.OkHttpClient
import com.sap.cloud.mobile.foundation.common.ClientProvider
import com.sap.cloud.mobile.odata.core.AndroidSystem
import com.sap.cloud.mobile.odata.offline.OfflineODataParameters
import com.sap.cloud.mobile.odata.offline.OfflineODataProvider
import java.net.URL
import com.sap.cloud.mobile.odata.offline.OfflineODataDefiningQuery


import de.datatrain.cockpita.app.SAPWizardApplication
import com.sap.cloud.mobile.odata.offline.OfflineODataException
import android.os.Looper
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import com.sap.cloud.android.odata.datrain_bc_srv_entities.DATRAIN_BC_SRV_Entities
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar


class MaDashboard : BaseActivity() {

    val SERVICE_URL = "https://mobile-a71f9a2af.hana.ondemand.com/"
    val CONNECTION_ID = "saperp_pp_bc"
    var myServiceContainer: DATRAIN_BC_SRV_Entities? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(de.datatrain.cockpita.R.layout.activity_ma_dashboard)
        setSupportActionBar(toolbar)
    }

    fun setupOfflineStore() {

        val okHttpClient = ClientProvider.get()

        AndroidSystem.setContext(applicationContext);

        val parameters = OfflineODataParameters();
        parameters.setPageSize(100);
        parameters.setEnableRepeatableRequests(true);

        val offlineODataProvider = OfflineODataProvider(URL(SERVICE_URL + "/" + CONNECTION_ID), parameters, okHttpClient, null, null)

        // Add defining queries
        offlineODataProvider.addDefiningQuery(
                OfflineODataDefiningQuery("Notifs",
                        "/notifications?\$filter=applicationId eq QA",
                        false))

        val sapServiceManager = (application as SAPWizardApplication).sapServiceManager
        var bcService = sapServiceManager.dATRAIN_BC_SRV_Entities

        offlineODataProvider.open({
            Log.d("myDebug", "Offline store opened")
            //toastAMessage("Offline store opened")
            var button: Button = findViewById(de.datatrain.cockpita.R.id.ofButton)
            val handler = Handler(Looper.getMainLooper())
            handler.post(Runnable { button.setEnabled(true) })
            myServiceContainer = DATRAIN_BC_SRV_Entities(offlineODataProvider)

        }, { offlineOdataException: OfflineODataException ->
            Log.d("myDebug", "Offline store did not open.", offlineOdataException)
            //toastAMessage("Offline store failed to open.  " + offlineOdataException.message)
        })


    }
}
