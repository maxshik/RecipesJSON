package com.example.a5lb

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.a5lb.databinding.ActivityAddReceptBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class AddRecept : AppCompatActivity() {
    lateinit var bindingClass : ActivityAddReceptBinding
    lateinit var container: LinearLayout
    val products: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityAddReceptBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        container = bindingClass.prodConteiner
        products.add(0, "")
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
                    Log.i("Test", products.size.toString())
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

    fun addNewRecept(view: View) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Подтверждение")
            .setMessage("Вы уверены, что хотите удалить этот элемент?")
            .setPositiveButton("Да") { _, _ ->

                products[0] = bindingClass.firstProduct.text.toString()
                val prod = Product(bindingClass.dishName.text.toString(), bindingClass.kitchen.text.toString(),
                    bindingClass.description.text.toString(), bindingClass.steps.text.toString(),
                    bindingClass.imgPath.text.toString(), products)
                Log.i("Test", products.size.toString())
                val fileName = "recepts.json"
                val file = File(applicationContext.filesDir, fileName)

                val savedRecept: ArrayList<Product> = if (file.exists()) {
                    val json = file.readText()
                    Gson().fromJson(json, object : TypeToken<ArrayList<Product>>() {}.type)
                } else {
                    ArrayList()
                }

                savedRecept.add(prod)

                val updatedJson = Gson().toJson(savedRecept)

                file.writeText(updatedJson)
                Toast.makeText(this, "Рецепт успешно добавлен", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Отмена", null)
            .create()

        alertDialog.show()
    }
}