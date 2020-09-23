package com.example.kotlincashloan.ui.Loans

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.main.registration.login.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import kotlinx.android.synthetic.main.fragment_loans_details.*
import java.lang.Exception
import java.util.*

class LoansDetailsFragment : Fragment() {
    private var viewModel = LoansViewModel()
    private var isNews: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loans_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        iniArgument()
        initRequest()
    }

    private fun iniArgument() {
        isNews = try {
            requireArguments().getInt("idNews")
        }catch (e: Exception){
            0
        }
    }

    private fun initRequest() {
        val map = HashMap<String, String>()
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        map.put("id", isNews.toString())
        MainActivity.alert.show()
        viewModel.getNews(map).observe(viewLifecycleOwner, Observer{ result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result == null){
                        if (data.error.code == 403){
                            loans_detail_no_connection.visibility = View.GONE
                            loans_detail_access_restricted.visibility = View.VISIBLE
                            loans_detail_layout.visibility = View.GONE
                        }else if (data.error.code == 404){
                            loans_detail_no_connection.visibility = View.GONE
                            loans_detail_not_found.visibility = View.VISIBLE
                            loans_detail_layout.visibility = View.GONE
                        }else if (data.error.code == 401){
                            initAuthorized()
                        }else{
                            loans_detail_no_connection.visibility = View.GONE
                            loans_detail_technical_work.visibility = View.VISIBLE
                            loans_detail_layout.visibility = View.GONE
                        }
                    }else{
                        loans_details_name.setText(data.result.name)
                        loans_details_description.setText(data.result.description)
                        loans_details_text.setMarkDownText(data.result.text)
                        Glide
                            .with(loans_details_image)
                            .load(data.result.thumbnail)
                            .into(loans_details_image)

                        loans_detail_layout.visibility = View.VISIBLE
                        loans_detail_no_connection.visibility = View.GONE
                    }
                }
                Status.ERROR -> {
                    if (msg == "403"){
                        loans_detail_no_connection.visibility = View.GONE
                        loans_detail_access_restricted.visibility = View.VISIBLE
                        loans_detail_layout.visibility = View.GONE
                    }else if (msg == "404"){
                        loans_detail_no_connection.visibility = View.GONE
                        loans_detail_not_found.visibility = View.VISIBLE
                        loans_detail_layout.visibility = View.GONE
                    }else if (msg == "401"){
                        initAuthorized()
                    }else{
                        loans_detail_no_connection.visibility = View.GONE
                        loans_detail_technical_work.visibility = View.VISIBLE
                        loans_detail_layout.visibility = View.GONE
                    }
                }
                Status.NETWORK -> {
                    loans_detail_layout.visibility = View.GONE
                    loans_detail_access_restricted.visibility = View.GONE
                    loans_detail_not_found.visibility = View.GONE
                    loans_detail_technical_work.visibility = View.GONE
                    loans_detail_no_connection.visibility = View.VISIBLE
                }
            }
            MainActivity.alert.hide()
        })
    }
    private fun initAuthorized(){
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }
}