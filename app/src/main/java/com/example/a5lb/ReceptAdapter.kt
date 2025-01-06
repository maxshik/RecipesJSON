package com.example.a5lb

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.a5lb.databinding.ReceptCardBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import java.io.File
import java.lang.StringBuilder

class ReceptAdapter(private val context: Context) : RecyclerView.Adapter<ReceptAdapter.ReceptHolder>() {
    class ReceptHolder(item: View) : RecyclerView.ViewHolder(item), View.OnCreateContextMenuListener {
        val binding = ReceptCardBinding.bind(item)

         init {
             item.setOnCreateContextMenuListener(this)
         }

        fun bind(product: Product) {
            with(binding) {
                Picasso.get().load(product.dishImg).into(dishImage)
                Log.i("test", product.dishImg)
                dishName.text = product.name
                shortDesc.text = product.shortDescription
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
            val inflater = MenuInflater(v.context)
            inflater.inflate(R.menu.context_menu, menu)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceptHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recept_card, parent, false)
        return ReceptHolder(view)
    }

    private fun getReceptsFile(): File {
        val filename = "recepts.json"
        val file = File(context.filesDir, filename)

        if (!file.exists()) {
            file.writeText("[]")
        }

        return file
    }


    override fun getItemCount(): Int {
        val file = getReceptsFile()
        val jsonFromFile = file.readText()
        val savedRecepts: ArrayList<Product> = Gson().fromJson(jsonFromFile, object : TypeToken<List<Product>>() {}.type)
        return savedRecepts.size
    }


    override fun onBindViewHolder(holder: ReceptHolder, position: Int) {
        val file = getReceptsFile()
        val jsonFromFile = file.readText()
        val savedRecepts: ArrayList<Product> = Gson().fromJson(jsonFromFile, object : TypeToken<List<Product>>() {}.type)
        val recipe = savedRecepts[position]

        holder.bind(recipe)

        val isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        holder.itemView.setOnClickListener {
            val selectedRecepts = savedRecepts[position]
            val prodForRecept = StringBuilder()
            selectedRecepts.products.forEach {
                prodForRecept.append("$it \n")
            }

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            val detailFragment = ReceptDetailsFragment.newInstance(
                selectedRecepts.name,
                selectedRecepts.kitchen,
                selectedRecepts.steps,
                selectedRecepts.shortDescription,
                prodForRecept.toString(),
                selectedRecepts.dishImg
            )

            if (isLandscape) {
                transaction.replace(R.id.detail_fragment_container, detailFragment)
            } else {
                transaction.replace(R.id.place_holder, detailFragment)
            }
            transaction.addToBackStack(null)
            transaction.commit()
        }


        holder.itemView.setOnCreateContextMenuListener { menu, _, _ ->
            val inflater = MenuInflater(context)
                inflater.inflate(R.menu.context_menu, menu)
            menu.findItem(R.id.menu_edit)?.setOnMenuItemClickListener {
                val i = Intent(context, EditRecept::class.java)
                i.putExtra("receptPosition", position)
                context.startActivity(i)
                true
            }
            menu.findItem(R.id.menu_delete)?.setOnMenuItemClickListener {
                savedRecepts.removeAt(position)
                val updatedJson = Gson().toJson(savedRecepts)
                file.writeText(updatedJson)
                notifyDataSetChanged()
                true
            }
        }

        holder.bind(savedRecepts[position])
    }
}
