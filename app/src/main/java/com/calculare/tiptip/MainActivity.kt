package com.calculare.tiptip

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 10

class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var tvTotalPerPerson: TextView // static text
    private lateinit var tvTTP: TextView // total per person $
    private lateinit var cbSplitBill: CheckBox
    private lateinit var spClientSelect: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,
                systemBars.bottom)
            insets
        }

        etBaseAmount = findViewById<EditText>(R.id.etBaseAmount)
        seekBarTip = findViewById<SeekBar>(R.id.seekBarTip)
        tvTipPercentLabel = findViewById<TextView>(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById<TextView>(R.id.tvTipAmount)
        tvTotalAmount = findViewById<TextView>(R.id.tvTotalAmount)
        tvTipDescription = findViewById<TextView>(R.id.tvTipDescription)
        tvTotalPerPerson = findViewById<TextView>(R.id.tvTotalPerPerson)
        tvTTP = findViewById<TextView>(R.id.tvTTP)
        cbSplitBill = findViewById<CheckBox>(R.id.cbSplitBill)
        spClientSelect = findViewById<Spinner>(R.id.spClientSelect)

        var numOfPayers = 1

        fun computeTipAndTotal() {
            if(etBaseAmount.text.isEmpty())
            {
                tvTipAmount.text = ""
                tvTotalAmount.text = ""
                return
            }

            val baseAmount = etBaseAmount.text.toString().toDouble()
            val tipPercent = seekBarTip.progress

            val tipAmount = baseAmount * tipPercent/100
            val totalAmount = baseAmount + tipAmount

            tvTipAmount.text = "%.2f".format(tipAmount)
            tvTotalAmount.text = "%.2f".format(totalAmount)

            var totalPerPersonAmount = totalAmount/numOfPayers
            tvTTP.text = "%.2f".format(totalPerPersonAmount)
        }

        fun updateTipDescription(tipPercent: Int) {
            val tipDescription = when(tipPercent){
                in 0..9 -> "Chitros"
                in 10..14 -> "Cat de cat"
                in 15 ..  20 -> "Decent"
                else -> "In bani"
                // :p
            }
            tvTipDescription.text = tipDescription

            val color = android.animation.ArgbEvaluator().evaluate(
                tipPercent.toFloat()/seekBarTip.max,
                ContextCompat.getColor(this, R.color.color_worst_tip),
                ContextCompat.getColor(this, R.color.color_best_tip),
            ) as Int
            tvTipDescription.setTextColor(color)
        }

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)

        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int,fromUser: Boolean) {
                //Log.i(TAG, "onProgressChanged $progress")
                tvTipPercentLabel.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        etBaseAmount.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                computeTipAndTotal()
            }
        })

        val numbers = (2..8).toList()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            numbers
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spClientSelect.adapter = adapter

        spClientSelect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedNumber = parent?.getItemAtPosition(position) as Int
                numOfPayers = selectedNumber
                computeTipAndTotal()
                Toast.makeText(this@MainActivity, "Selected $selectedNumber payers", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        cbSplitBill.setOnCheckedChangeListener { _, isChecked ->
            spClientSelect.visibility = if (isChecked) View.VISIBLE else View.GONE
            tvTotalPerPerson.visibility = if(isChecked) View.VISIBLE else View.GONE
            tvTTP.visibility = if(isChecked) View.VISIBLE else View.GONE
        }
    }
}