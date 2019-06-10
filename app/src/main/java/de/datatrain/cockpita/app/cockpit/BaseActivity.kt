package de.datatrain.cockpita.app.cockpit

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.sap.cloud.android.odata.datrain_bc_srv_entities.Tile
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar
import com.sap.cloud.mobile.odata.DataQuery
import de.datatrain.cockpita.R
import de.datatrain.cockpita.app.SAPWizardApplication
import de.datatrain.cockpita.app.cockpit.home.HomeActivity
import de.datatrain.cockpita.app.cockpit.modules.ma.MaDashboard

import kotlinx.android.synthetic.main.activity_base.*

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuEntries()
        menuInflater.inflate(de.datatrain.cockpita.R.menu.header, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        de.datatrain.cockpita.R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            true
        }

        de.datatrain.cockpita.R.id.action_user_notifications -> {
            // User chose the "Favorite" action, mark the current item
            // as a favorite..
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    fun getMenuEntries() {
        var tiles2: List<Tile> = listOf()
        Log.d(this.javaClass.name, "blubber")
        val sapServiceManager = (application as SAPWizardApplication).sapServiceManager
        var bcService = sapServiceManager.dATRAIN_BC_SRV_Entities
        val query = DataQuery().orderBy(Tile.sortID)
        bcService?.getTilesAsync(query, { tiles: List<Tile> ->
            setMenu(tiles)
        }, { re: RuntimeException -> Log.d("myDebug", "An error occurred when querying for tiles:  " + re.message) })

    }

    fun setMenu(tiles: List<Tile>) {
        val toolbar = findViewById(de.datatrain.cockpita.R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            Toast.makeText(applicationContext, "Toolbar", Toast.LENGTH_SHORT).show()
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->

                Log.d("myDebug", item.title.toString())
                when (item.title) {
                    "Leistungen" -> Toast.makeText(applicationContext, "Toolbar", Toast.LENGTH_SHORT).show()
                    "Meine Aufgaben" -> startActivityMeineAufgaben()
                    "Startseite" -> startActivityHome()
                }
                false
            }


            val inflater = popupMenu.menuInflater
            val menu: Menu
            inflater.inflate(de.datatrain.cockpita.R.menu.menu, popupMenu.menu)

            popupMenu.menu.add("Startseite")
            for (tile in tiles) {
                popupMenu.menu.add(tile.title)
            }
            popupMenu.show()
        }
    }

    private fun startActivityMeineAufgaben() {
        val intent = Intent(this, MaDashboard::class.java)
        startActivityForResult(intent,1)
    }

    private fun startActivityHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivityForResult(intent,1)
    }

}
