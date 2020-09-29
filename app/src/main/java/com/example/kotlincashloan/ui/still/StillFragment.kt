package com.example.kotlincashloan.ui.still

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import kotlinx.android.synthetic.main.fragment_still.*

class StillFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_still, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        initClick()
    }

    private fun initClick() {
        still_exit.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}