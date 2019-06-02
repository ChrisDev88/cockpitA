package de.datatrain.cockpita.app.cockpit.start

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.sap.cloud.android.odata.datrain_bc_srv_entities.Tile
import de.datatrain.cockpita.R
import de.datatrain.cockpita.inflate
import kotlinx.android.synthetic.main.tile_template.view.*

class TileAdapter(private val tiles: List<Tile>) : RecyclerView.Adapter<TileAdapter.TileHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileHolder {
        val inflatedView = parent.inflate(R.layout.tile_template, false)
        return TileHolder(inflatedView)
    }

    override fun getItemCount() = tiles.size


    override fun onBindViewHolder(holder: TileHolder, position: Int) {
        val tile = tiles[position]
        holder.bindTile(tile)
    }

    class TileHolder(val v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
        private var tile: Tile? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("RecylerView", "Click")
        }

        companion object {
            private val TILE_KEY = "TILE"
        }

        fun bindTile(tile: Tile) {
            this.tile = tile
            view.tileName.text = tile.title
            view.tileDescription.text = tile.description

            when(tile.iconID){
                "app-icon-calendar" -> view.tileIcon.setImageResource(R.drawable.app_icon_calendar);
                "app-icon-charts" -> view.tileIcon.setImageResource(R.drawable.app_icon_charts);
                "app-icon-entry-sheets" -> view.tileIcon.setImageResource(R.drawable.app_icon_entry_sheets);
                "app-icon-feedback-tenant" -> view.tileIcon.setImageResource(R.drawable.app_icon_feedback_tenant);
                "app-icon-inspection-trees" -> view.tileIcon.setImageResource(R.drawable.app_icon_inspection_trees);
                "app-icon-insurance" -> view.tileIcon.setImageResource(R.drawable.app_icon_insurance);
                "app-icon-notifications" -> view.tileIcon.setImageResource(R.drawable.app_icon_notifications);
                "app-icon-property-data" -> view.tileIcon.setImageResource(R.drawable.app_icon_property_data);
                "app-icon-sla-monitoring-crm" -> view.tileIcon.setImageResource(R.drawable.app_icon_sla_monitoring_crm);
                "app-icon-statistics" -> view.tileIcon.setImageResource(R.drawable.app_icon_statistics);
                "app-icon-ticketcenter" -> view.tileIcon.setImageResource(R.drawable.app_icon_ticketcenter);
                "app-icon-verkehrssicherungspflichten" -> view.tileIcon.setImageResource(R.drawable.app_icon_verkehrssicherungspflichten);
                "app-icon-company-structure" -> view.tileIcon.setImageResource(R.drawable.app_icon_company_structure);
                else -> view.tileIcon.setImageResource(R.drawable.app_icon_calendar)
            }
        }
    }
}