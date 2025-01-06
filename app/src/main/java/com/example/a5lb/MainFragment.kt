package com.example.a5lb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a5lb.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    lateinit var bindingClass : FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        bindingClass = FragmentMainBinding.inflate(inflater, container, false)
        return bindingClass.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val adapter = ReceptAdapter(requireContext())
        bindingClass.apply {
            rcView.layoutManager = LinearLayoutManager(requireContext())
            rcView.adapter = adapter
        }
    }

    companion object {
        @JvmStatic fun newInstance() = MainFragment()
    }
}