package com.o7.spinnerprac

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.o7.spinnerprac.databinding.ActivityMainBinding
import java.io.PipedReader


typealias Operation = (Int, String) -> Unit

class MainActivity : AppCompatActivity() {

    var spinnerItems = mutableListOf<String>("one", "two", "three")
    lateinit var arrayAdapter: ArrayAdapter<String>

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, spinnerItems)
        binding.spDynamic.adapter = arrayAdapter
        binding.spDynamic.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var selectedItem = binding.spDynamic.selectedItem as String
                Toast.makeText(this@MainActivity, "$selectedItem", Toast.LENGTH_SHORT).show()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.btnAdd.setOnClickListener {

            openDialog(this@MainActivity, {_, value ->
                if(value.length > 0) {
                    spinnerItems.add(value)
                }else{
                    Toast.makeText(this, "Nothing to add", Toast.LENGTH_SHORT).show()
                }
            })

        }

        binding.btnUpdate.setOnClickListener {

            openDialog(this@MainActivity, {index, value ->
                if(value.length > 0 && index <= spinnerItems.size && index > 0) {
                    spinnerItems.set(index-1, value)
                }else{
                    Toast.makeText(this, "Check position or Nothing to Update", Toast.LENGTH_SHORT).show()
                }
            })

        }

    }

    private fun openDialog(context: Context, operation: Operation){

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.value_index_dialog)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        val cancelBtn = dialog.findViewById<Button>(R.id.btnDialogCancel)
        val okayBtn = dialog.findViewById<Button>(R.id.btnDialogOk)
        val indexEdt = dialog.findViewById<EditText>(R.id.edtDialogIndex)
        val valueEdt = dialog.findViewById<EditText>(R.id.edtDialogValue)

        okayBtn.setOnClickListener{
            val value = valueEdt.text.toString()
            val index = indexEdt.text.toString().toIntOrNull() ?: 0
            operation(index, value)
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener{

            dialog.dismiss()
        }

        dialog.show()
    }

}