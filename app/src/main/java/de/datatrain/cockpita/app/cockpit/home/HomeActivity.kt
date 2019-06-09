package de.datatrain.cockpita.app.cockpit.home

import android.app.PendingIntent.getActivity
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
import android.view.Menu
import android.view.MenuItem
import android.support.v7.widget.Toolbar;
import com.sap.cloud.android.odata.datrain_bc_srv_entities.User
import com.sap.cloud.mobile.foundation.common.ClientProvider
import com.sap.cloud.mobile.foundation.user.UserInfo
import com.sap.cloud.mobile.foundation.user.UserRoles
import android.R
import android.support.v7.widget.PopupMenu
import android.view.MenuInflater
import android.widget.Toast
import com.sap.cloud.android.odata.datrain_bc_srv_entities.DATRAIN_BC_SRV_EntitiesMetadata.EntityTypes.tile


class HomeActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(de.datatrain.cockpita.R.layout.activity_home)
        getTiles()
        getUser()
    }

    fun getTiles() {
        Log.d(this.javaClass.name, "blubber")
        val sapServiceManager = (application as SAPWizardApplication).sapServiceManager
        var bcService = sapServiceManager.dATRAIN_BC_SRV_Entities
        val query = DataQuery().orderBy(Tile.sortID)
        bcService?.getTilesAsync(query, { tiles: List<Tile> ->
            var progressBar: FioriProgressBar = findViewById(de.datatrain.cockpita.R.id.progressBar)
            progressBar.visibility = View.GONE
            for (tile in tiles) {
                Log.d("myDebug", "${tile.id}")
            }
            showTiles(tiles);
            setMenu(tiles)
        }, { re: RuntimeException -> Log.d("myDebug", "An error occurred when querying for tiles:  " + re.message) })
    }

    fun showTiles(tiles: List<Tile>) {
        var recycler: RecyclerView = findViewById(de.datatrain.cockpita.R.id.tileView)
        recycler.setLayoutManager(GridLayoutManager(this, 4))
        recycler.adapter = TileAdapter(tiles)
    }

    fun getUserData(userId: String) {
        val sapServiceManager = (application as SAPWizardApplication).sapServiceManager
        var bcService = sapServiceManager.dATRAIN_BC_SRV_Entities
        val query = DataQuery().withKey(User.key(userId))

        bcService?.getUser1Async(query, { user: User ->
            Log.d("myDebug", "${user.nameFirst}")
            //var textView: TextView = findViewById(de.datatrain.cockpita.R.id.welcomeText)
            //val text: String = user.nameFirst.plus(" ").plus(user.nameLast)
            //textView.setText(text)
        }, { re: RuntimeException -> Log.d("myDebug", "An error occurred when querying for User:  " + re.message) })
    }

    private fun getUser() {
        val myTag = "myDebug"
        Log.d(myTag, "In getUser")
        val sapWizardApplication = application as SAPWizardApplication
        val settingsParameters = sapWizardApplication.settingsParameters
        val roles = UserRoles(ClientProvider.get(), settingsParameters!!)
        val callbackListener = object : UserRoles.CallbackListener {
            override fun onSuccess(ui: UserInfo) {
                Log.d(myTag, "User Name: " + ui.userName)
                Log.d(myTag, "User Id: " + ui.id)
                val roleList = ui.roles
                Log.d(myTag, "User has the following Roles")
                for (i in roleList!!.indices) {
                    Log.d(myTag, "Role Name " + roleList[i])
                }
                getUserData(ui.id)
            }

            override fun onError(throwable: Throwable) {
                Log.d(myTag, throwable.message)
            }
        }
        roles.load(callbackListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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

    fun setMenu(tiles:List<Tile>) {
        val toolbar = findViewById(de.datatrain.cockpita.R.id.my_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            Toast.makeText(applicationContext,"Toolbar", Toast.LENGTH_SHORT).show()
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->

                Log.d("myDebug", item.title.toString())
                when(item.title){
                    "Leistungen" -> Toast.makeText(applicationContext,"Toolbar", Toast.LENGTH_SHORT).show()
                }
                false
            }


            val inflater = popupMenu.menuInflater
            inflater.inflate(de.datatrain.cockpita.R.menu.menu, popupMenu.menu)

            for (tile in tiles) {
                popupMenu.menu.add(tile.title)
            }
            popupMenu.show()
        }
    }

}