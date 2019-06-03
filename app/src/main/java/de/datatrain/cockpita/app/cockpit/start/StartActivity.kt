package de.datatrain.cockpita.app.cockpit.start

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.sap.cloud.android.odata.datrain_bc_srv_entities.Tile
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar
import com.sap.cloud.mobile.odata.DataQuery
import de.datatrain.cockpita.app.SAPWizardApplication
import android.support.v7.widget.GridLayoutManager
import android.widget.TextView
import com.sap.cloud.android.odata.datrain_bc_srv_entities.DATRAIN_BC_SRV_EntitiesMetadata.EntityTypes.tile
import com.sap.cloud.android.odata.datrain_bc_srv_entities.User
import de.datatrain.cockpita.R


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        getTiles()
        getUser()
    }

    fun getTiles() {
        Log.d(this.javaClass.name, "blubber")
        val sapServiceManager = (application as SAPWizardApplication).sapServiceManager
        var bcService = sapServiceManager.dATRAIN_BC_SRV_Entities
        val query = DataQuery().orderBy(Tile.sortID)
        bcService?.getTilesAsync(query, { tiles: List<Tile> ->
            var progressBar: FioriProgressBar = findViewById(R.id.progressBar)
            progressBar.visibility = View.GONE
            for (tile in tiles) {
                Log.d("myDebug", "${tile.id}")
            }
            showTiles(tiles);
        }, { re: RuntimeException -> Log.d("myDebug", "An error occurred when querying for tiles:  " + re.message) })
    }

    fun showTiles(tiles: List<Tile>) {
        var recycler: RecyclerView = findViewById(R.id.tileView)
        recycler.setLayoutManager(GridLayoutManager(this, 4))
        recycler.adapter = TileAdapter(tiles)
    }

    fun getUser() {
        val sapServiceManager = (application as SAPWizardApplication).sapServiceManager
        var bcService = sapServiceManager.dATRAIN_BC_SRV_Entities
        val query = DataQuery().withKey(User.key("P000001"))

        bcService?.getUser1Async(query, { user: User ->
            Log.d("myDebug", "${user.nameFirst}")
            var textView: TextView = findViewById(R.id.welcomeText)
            val text: String = user.nameFirst.plus(" ").plus(user.nameLast)
            textView.setText(text)
        }, { re: RuntimeException -> Log.d("myDebug", "An error occurred when querying for User:  " + re.message) })
    }
}