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

class MaDashboard : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ma_dashboard)
        setSupportActionBar(toolbar)
    }

}
