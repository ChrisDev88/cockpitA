package de.datatrain.cockpita.app.cockpit.modules.mk

import android.annotation.TargetApi
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
import android.widget.*
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar
import de.datatrain.cockpita.R
import de.datatrain.cockpita.app.cockpit.BaseActivity
import kotlinx.android.synthetic.main.activity_tenant_search.*
import kotlinx.android.synthetic.main.activity_tenant_search2.*
import kotlinx.android.synthetic.main.activity_tenant_search2.toolbar
import java.util.*

class TenantSearch : BaseActivity() {

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.N)
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
            val dpd : DatePickerDialog = DatePickerDialog(this,R.style.MySpinnerDatePickerStyle, DatePickerDialog.OnDateSetListener{ view: DatePicker, i: Int, i1: Int, i2: Int ->

                c.set(Calendar.YEAR, i)
                c.set(Calendar.MONTH, i1)
                c.set(Calendar.DAY_OF_MONTH, i2)
                val myFormat : String = "dd.MM.yyyy"
                val sdf : SimpleDateFormat = SimpleDateFormat(myFormat,Locale.GERMANY)
                inputDate.setText(sdf.format(c.getTime()))
            },year,month,day)

            dpd.show()
        }
    }

    fun showMoreFields(view: View){
        var inputPostCode: EditText = findViewById(de.datatrain.cockpita.R.id.inputPostCode)
        inputPostCode.visibility = View.VISIBLE

        var inputCity: EditText = findViewById(de.datatrain.cockpita.R.id.inputCity)
        inputCity.visibility = View.VISIBLE

        var inputBukrs: EditText = findViewById(de.datatrain.cockpita.R.id.inputBukrs)
        inputBukrs.visibility = View.VISIBLE

        var inputSwenr: EditText = findViewById(de.datatrain.cockpita.R.id.inputSwenr)
        inputSwenr.visibility = View.VISIBLE

        var inputRecnDate: EditText = findViewById(de.datatrain.cockpita.R.id.inputRecnDate)
        inputRecnDate.visibility = View.VISIBLE

        var radioGroup: RadioGroup = findViewById(de.datatrain.cockpita.R.id.radioGroup)
        radioGroup.visibility = View.VISIBLE

        var labelText: TextView = findViewById(de.datatrain.cockpita.R.id.textView4)
        labelText.visibility = View.VISIBLE

        var cbWohn: CheckBox = findViewById(de.datatrain.cockpita.R.id.checkBoxRecnTypeWohn)
        cbWohn.visibility = View.VISIBLE

        var cbStell: CheckBox = findViewById(de.datatrain.cockpita.R.id.checkBoxRecnTypeStell)
        cbStell.visibility = View.VISIBLE

        var cbKautGew: CheckBox = findViewById(de.datatrain.cockpita.R.id.checkBoxRecnTypeKautGew)
        cbKautGew.visibility = View.VISIBLE

        var cbKautWoh: CheckBox = findViewById(de.datatrain.cockpita.R.id.checkBoxRecnTypeKautWoh)
        cbKautWoh.visibility = View.VISIBLE

        var bMoreFields: Button = findViewById(de.datatrain.cockpita.R.id.buttonShowMoreFields)
        bMoreFields.visibility = View.GONE
    }
}



