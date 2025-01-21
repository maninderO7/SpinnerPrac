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
import android.widget.TextView
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

    var spinnerItems = mutableListOf<String>("default", "one", "two", "three")
    var currentSelectedItem:Int = 0
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
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long){
                currentSelectedItem = p2
                val selectedItem = binding.spDynamic.selectedItem as String
                Toast.makeText(this@MainActivity, "$selectedItem", Toast.LENGTH_SHORT).show()

                if(selectedItem == "default"){
                    return
                }


                val ops = arrayOf("Delete", "Update")

                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle("Select Action")

                    setSingleChoiceItems(ops, -1, { dialog, which ->
                        val index = p2
                        if(which == 0){
                            spinnerItems.removeAt(index)

                        }else if(which == 1){
                            openDialog(this@MainActivity, {_, value ->
                                spinnerItems[index] = value

                            }, "Update", hideIndexEdt = true)
                            Toast.makeText(this@MainActivity, "$index", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    })

                    show()

                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.btnAdd.setOnClickListener {

            openDialog(this@MainActivity, {position, value ->
                if(value.length > 0 && position > 0) {
                    spinnerItems.add(position, value)
                }else{
                    Toast.makeText(this, "Nothing to add", Toast.LENGTH_SHORT).show()
                }
            }, "Add")

        }

        binding.btnUpdate.setOnClickListener {

            openDialog(this@MainActivity, {position, value ->
                if(value.length > 0 && position <= spinnerItems.size && position > 0) {
                    spinnerItems.set(position, value)
                }else{
                    Toast.makeText(this, "Check position or Nothing to Update", Toast.LENGTH_SHORT).show()
                }
            }, "Update")

        }

        binding.btnDelete.setOnClickListener {

            openDialog(this@MainActivity, {position, _ ->
                if(position <= spinnerItems.size && position > 0) {
                    spinnerItems.removeAt(position)
                }else{
                    Toast.makeText(this, "Check position or Nothing to Update", Toast.LENGTH_SHORT).show()
                }
            }, "Delete",hideValueEdt = true)

        }



        binding.spStatic.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.genders_list,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
                .also { adapter ->
                    adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
                    binding.spStatic.adapter = adapter
             }

        binding.spStatic.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedValue = binding.spStatic.selectedItem as String
                binding.tvGenderResult.text = selectedValue
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun openDialog( context: Context,
                            operation: Operation, action: String,
                            hideIndexEdt: Boolean = false,
                            hideValueEdt: Boolean = false){

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
        val actionTv = dialog.findViewById<TextView>(R.id.tvActionHeader)

        if(hideValueEdt){
            valueEdt.visibility = View.GONE
        }
        if(hideIndexEdt){
            indexEdt.visibility = View.GONE
        }

        actionTv.text = action


        okayBtn.setOnClickListener{
            val value = valueEdt.text.toString()
            val position = indexEdt.text.toString().toIntOrNull() ?: 0
            operation(position, value)
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }

}