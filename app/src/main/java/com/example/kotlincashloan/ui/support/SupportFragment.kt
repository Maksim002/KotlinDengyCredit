package com.example.kotlincashloan.ui.support

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.ProfileAdapter
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.main.registration.login.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import kotlinx.android.synthetic.main.fragment_support.*

class SupportFragment : Fragment() {
    private var myAdapter = ProfileAdapter()
    private var viewModel = SupportViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        initRecycler()
    }

    private fun initRecycler() {
        MainActivity.alert.show()
        val map = HashMap<String, String>()
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        viewModel.listFaq(map).observe(viewLifecycleOwner, Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    myAdapter.update(data!!.result)
                    profile_recycler.adapter = myAdapter
                }
                Status.ERROR -> {
                    loadingMistake(activity as AppCompatActivity)

                }
                Status.NETWORK ->{
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            }
            MainActivity.alert.hide()
        })
    }
}