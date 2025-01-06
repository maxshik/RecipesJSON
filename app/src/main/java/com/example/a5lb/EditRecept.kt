package com.example.a5lb

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.a5lb.databinding.ActivityEditReceptBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class EditRecept : AppCompatActivity() {
    lateinit var bindingClass : ActivityEditReceptBinding
    val products: MutableList<String> = mutableListOf()
    lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityEditReceptBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        container = bindingClass.prodConteiner
        products.add(0, "")

        val filename = "recepts.json"
        val file = File(filesDir, filename)

        val jsonFromFile = file.readText()
        val savedRecepts: ArrayList<Product> = Gson().fromJson(jsonFromFile, object : TypeToken<List<Product>>() {}.type)
        val selectedRecept = savedRecepts[intent!!.getIntExtra("receptPosition", 0)]

        bindingClass.dishName.setText(selectedRecept.name)
        bindingClass.description.setText(selectedRecept.shortDescription)
        bindingClass.kitchen.setText(selectedRecept.kitchen)
        bindingClass.steps.setText(selectedRecept.steps)
        bindingClass.pathToImage.setText(selectedRecept.dishImg)

        selectedRecept.products.forEach {
            val newEditText = EditText(this)
            newEditText.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                50.dpToPx()
            )
            newEditText.hint = "Продукт"
            newEditText.setEms(12)
            newEditText.setMargins(0, 10.dpToPx(), 0, 0)
            newEditText.setText(it)
            container.addView(newEditText)

            newEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) { // Когда фокус потерян
                    val product = newEditText.text.toString()
                    if (product.isNotEmpty()) {
                        val position = (container.indexOfChild(newEditText) + 1)
                        products.add(position, product)
                        Log.i("Test", products.size.toString())
                    }
                }
            }
        }
    }

    fun addNewEditText(view: View) {
        val newEditText = EditText(this)
        newEditText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            50.dpToPx()
        )
        newEditText.hint = "Продукт"
        newEditText.setEms(12)
        newEditText.setMargins(0, 10.dpToPx(), 0, 0)

        container.addView(newEditText)

        newEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val product = newEditText.text.toString()
                if (product.isNotEmpty()) {
                    val position = (container.indexOfChild(newEditText) + 1)
                    products.add(position, product)
                }
            }
        }
    }

    fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(left, top, right, bottom)
        layoutParams = params
    }

    fun editRecept(view: View) {

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Подтверждение")
            .setMessage("Вы уверены, что хотите удалить этот элемент?")
            .setPositiveButton("Да") { _, _ ->
                val filename = "recepts.json"
                val file = File(filesDir, filename)

                val jsonFromFile = file.readText()
                var savedRecepts: ArrayList<Product> = Gson().fromJson(jsonFromFile, object : TypeToken<List<Product>>() {}.type)

                val rec = savedRecepts[intent!!.getIntExtra("receptPosition", 0)]
                rec.name = bindingClass.dishName.text.toString()
                rec.steps = bindingClass.steps.text.toString()
                rec.shortDescription = bindingClass.description.text.toString()
                rec.kitchen = bindingClass.kitchen.text.toString()
                rec.dishImg = bindingClass.pathToImage.text.toString()
                rec.products = products

                val updatedJson = Gson().toJson(savedRecepts)

                file.writeText(updatedJson)
                Toast.makeText(this, "Рецепт успешно обновлен", Toast.LENGTH_LONG).show()

            }
            .setNegativeButton("Отмена", null)
            .create()

        alertDialog.show()

    }
}