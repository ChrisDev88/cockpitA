package de.datatrain.cockpita.app.cockpit.modules.mk

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import de.datatrain.cockpita.R
import de.datatrain.cockpita.app.cockpit.BaseActivity
import kotlinx.android.synthetic.main.activity_tenant_search.*
import kotlinx.android.synthetic.main.activity_tenant_search2.*
import kotlinx.android.synthetic.main.activity_tenant_search2.toolbar
import java.util.*

class TenantSearch : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_search)
        setSupportActionBar(toolbar)

        val inputDate : EditText = findViewById(de.datatrain.cockpita.R.id.inputRecnDate)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        inputDate.setOnClickListener{
            val dpd : DatePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ view: DatePicker, i: Int, i1: Int, i2: Int ->
                inputDate.setText(view.getYear().toString() + "." + (view.getMonth()+1) + "." + view.getDayOfMonth())
            },year,month,day)
            dpd.show()
        }
    }
}



