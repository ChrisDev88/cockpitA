package de.datatrain.cockpita.service

import de.datatrain.cockpita.app.ConfigurationData
import com.sap.cloud.android.odata.datrain_bc_srv_entities.DATRAIN_BC_SRV_Entities
import com.sap.cloud.mobile.foundation.common.ClientProvider
import com.sap.cloud.mobile.odata.OnlineODataProvider
import com.sap.cloud.mobile.odata.http.OKHttpHandler

class SAPServiceManager(private val configurationData: ConfigurationData) {

    var dATRAIN_BC_SRV_Entities: DATRAIN_BC_SRV_Entities? = null
        private set
        get() {
            return field ?: throw IllegalStateException("SAPServiceManager was not initialized")
        }

    fun openODataStore(callback: () -> Unit) {
        if( configurationData.loadData() ) {
            configurationData.serviceUrl?.let { _serviceURL ->
                dATRAIN_BC_SRV_Entities = DATRAIN_BC_SRV_Entities (
                    OnlineODataProvider("SAPService", _serviceURL + CONNECTION_ID_DATRAIN_BC_SRV_ENTITIES).apply {
                        networkOptions.httpHandler = OKHttpHandler(ClientProvider.get())
                        serviceOptions.checkVersion = false
                        serviceOptions.requiresType = true
                        serviceOptions.cacheMetadata = false
                    }
                )
            } ?: run {
                throw IllegalStateException("ServiceURL of Configuration Data is not initialized")
            }
        }
        callback.invoke()
    }

    companion object {
        const val CONNECTION_ID_DATRAIN_BC_SRV_ENTITIES: String = "saperp_pp_bc"
    }
}
