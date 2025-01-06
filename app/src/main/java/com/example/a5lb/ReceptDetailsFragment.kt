package com.example.a5lb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.a5lb.databinding.FragmentMainBinding
import com.example.a5lb.databinding.FragmentReceptDetailsBinding
import com.squareup.picasso.Picasso

class ReceptDetailsFragment : Fragment() {
    private var dishName: String? = null
    private var kitchen: String? = null
    private var steps: String? = null
    private var shortDescr: String? = null
    private var products: String? = null
    private var img: String? = null

    lateinit var binding: FragmentReceptDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dishName = it.getString(ARG_DISH_NAME)
            kitchen = it.getString(ARG_KITCHEN)
            steps = it.getString(ARG_STEPS)
            shortDescr = it.getString(ARG_SHORT_DESCR)
            products = it.getString(ARG_PRODUCTS)
            img = it.getString(ARG_IMG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReceptDetailsBinding.inflate(inflater, container, false)

        binding.dishName.text = dishName
        binding.kitchen.text = kitchen
        binding.steps.text = steps
        binding.productForRecept.text = products
        Picasso.get().load(img).into(binding.dishImage)
        return binding.root
    }

    companion object {
        private const val ARG_DISH_NAME = "dishName"
        private const val ARG_KITCHEN = "kitchen"
        private const val ARG_STEPS = "steps"
        private const val ARG_SHORT_DESCR = "shortDescr"
        private const val ARG_PRODUCTS = "products"
        private const val ARG_IMG = "img"

        @JvmStatic
        fun newInstance(dishName: String, kitchen: String, steps: String, shortDescr: String, products: String, img: String) =
            ReceptDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DISH_NAME, dishName)
                    putString(ARG_KITCHEN, kitchen)
                    putString(ARG_STEPS, steps)
                    putString(ARG_SHORT_DESCR, shortDescr)
                    putString(ARG_PRODUCTS, products)
                    putString(ARG_IMG, img)
                }
            }
    }
}
