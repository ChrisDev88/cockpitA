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
import de.datatrain.cockpita.R


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        getTiles()
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
                Log.d(this.javaClass.name, "${tile.id}")
            }
            showTiles(tiles);
        }, { re: RuntimeException -> Log.d(this.toString(), "An error occurred when querying for tiles:  " + re.message) })
    }

    fun showTiles(tiles: List<Tile>) {
        var recycler: RecyclerView = findViewById(R.id.tileView)
        recycler.setLayoutManager(GridLayoutManager(this, 4))
        recycler.adapter = TileAdapter(tiles)
    }
}