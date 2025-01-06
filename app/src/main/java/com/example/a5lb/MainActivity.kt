package com.example.a5lb

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a5lb.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivityMainBinding
    private val adapter = ReceptAdapter(this)
    private var sortAsc = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        supportActionBar!!.setTitle("Мои рецепты")
        setMainFragment()
    }

    override fun onResume() {
        super.onResume()
        setMainFragment()
    }

    private fun setMainFragment() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val mainFragment = MainFragment()
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            transaction.replace(R.id.main_fragment_container, mainFragment)
        } else {
            transaction.replace(R.id.place_holder, mainFragment)
        }
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem = menu!!.findItem(R.id.searchRecept)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val searchText = query.toString().lowercase()
                val filename = "recepts.json"
                val file = File(filesDir, filename)
                val jsonFromFile = file.readText()
                val savedRecepts: MutableList<Product> =
                    Gson().fromJson(jsonFromFile, object : TypeToken<List<Product>>() {}.type)
                var match = true

                savedRecepts.forEach {
                    if (it.name.toString().lowercase() == searchText) {
                        val fragment = ReceptDetailsFragment.newInstance(
                            it.name, it.kitchen, it.steps, it.shortDescription,
                            it.products.joinToString(separator = "\n"), it.dishImg
                        )
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.place_holder, fragment)
                            .addToBackStack(null)
                            .commit()
                        match = false
                    }
                }

                if (match) Toast.makeText(this@MainActivity, "Такого рецепта нет", Toast.LENGTH_LONG).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_up -> {
                onBackPressed()
                return true
            }
            R.id.addRecept -> {
                val i = Intent(this, AddRecept::class.java)
                startActivity(i)
                return true
            }

            R.id.sortRecept -> {
                sortProducts()
                return true
            }

            R.id.searchRecept -> {

                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun sortProducts() {
        val filename = "recepts.json"
        val file = File(filesDir, filename)

        val jsonFromFile = file.readText()
        var savedRecepts: MutableList<Product> = Gson().fromJson(jsonFromFile, object : TypeToken<List<Product>>() {}.type)

        var sortedList = savedRecepts.toMutableList() // Создаем копию списка продуктов
        sortedList.sortWith { product1, product2 ->
            val name1 = product1.name.lowercase()
            val name2 = product2.name.lowercase()
            if (sortAsc) {
                name1.compareTo(name2)
            } else {
                name2.compareTo(name1)
            }
        }
        val updatedJson = Gson().toJson(sortedList)
        file.writeText(updatedJson)
        adapter.notifyDataSetChanged() 
        sortAsc = !sortAsc
    }

}
