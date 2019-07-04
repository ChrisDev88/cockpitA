package de.datatrain.cockpita.app.cockpit.modules.mk

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import de.datatrain.cockpita.R
import de.datatrain.cockpita.app.cockpit.BaseActivity
import kotlinx.android.synthetic.main.activity_tenant_search2.*

class TenantSearch : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_search)
        setSupportActionBar(toolbar)
    }
}
