package com.example.kotlincashloan.extension

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.Loans.MyImageModel
import com.example.kotlincashloan.ui.loans.SharedViewModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.timelysoft.tsjdomcom.service.AppPreferences

val hashMapBitmap: HashMap<String, Bitmap> = HashMap()

fun getListsFifth(bitmap: HashMap<String, Bitmap>, id: ImageView, idIc: ImageView ,imageViewModel: SharedViewModel, activity: Activity, updateKey: String, idKey: String){
    if (bitmap[idKey] != null) {
        hashMapBitmap.put(updateKey, bitmap[idKey]!!)
        id.setImageBitmap(bitmap[idKey])
        imageViewModel.updateKey(updateKey)
        changeImage(idIc, true, activity)
    }
}

//Срабатывает в случае ошибки если
fun errorImageRus(idKey: ImageView? = null, idImage: ImageView? = null ,imageString: HashMap<String, MyImageModel> = hashMapOf(), imageKey: String? = null, activity: Activity){
    idKey!!.setImageResource(0)
    imageString.remove(imageKey)
    idImage!!.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_add_image))
}


fun listListResult(result: Int, technical_work: LinearLayout, no_connection: LinearLayout, layout_res: LinearLayout, access_restricted: LinearLayout, not_found: LinearLayout, activity: Activity){
    if (result == 400 || result == 500 || result == 409 || result == 429 || result == 601) {
        technical_work.visibility = View.VISIBLE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
        access_restricted.visibility = View.GONE
        not_found.visibility = View.GONE
    } else if (result == 403) {
        access_restricted.visibility = View.VISIBLE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
        not_found.visibility = View.GONE
    } else if (result == 404) {
        not_found.visibility = View.VISIBLE
        access_restricted.visibility = View.GONE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
    } else if (result == 401) {
        initAuthorized(activity)
    }
}

fun listListResult(result: Int, technical_work: LinearLayout, no_connection: LinearLayout, layout_res: SwipeRefreshLayout, access_restricted: LinearLayout, not_found: LinearLayout, activity: Activity){
    if (result == 400 || result == 500 || result == 409 || result == 429 || result == 601) {
        technical_work.visibility = View.VISIBLE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
        access_restricted.visibility = View.GONE
        not_found.visibility = View.GONE
    } else if (result == 403) {
        access_restricted.visibility = View.VISIBLE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
        not_found.visibility = View.GONE
    } else if (result == 404) {
        not_found.visibility = View.VISIBLE
        access_restricted.visibility = View.GONE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
    } else if (result == 401) {
        initAuthorized(activity)
    }
}

fun listListResult(result: Int, technical_work: LinearLayout, no_connection: LinearLayout, layout_res: NestedScrollView, access_restricted: LinearLayout, not_found: LinearLayout, activity: Activity){
    if (result == 400 || result == 500 || result == 409 || result == 429 || result == 601) {
        technical_work.visibility = View.VISIBLE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
        access_restricted.visibility = View.GONE
        not_found.visibility = View.GONE
    } else if (result == 403) {
        access_restricted.visibility = View.VISIBLE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
        not_found.visibility = View.GONE
    } else if (result == 404) {
        not_found.visibility = View.VISIBLE
        access_restricted.visibility = View.GONE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
    } else if (result == 401) {
        initAuthorized(activity)
    }
}

fun listListResult(result: Int, technical_work: LinearLayout, no_connection: LinearLayout, layout_res: ConstraintLayout, access_restricted: LinearLayout, not_found: LinearLayout, activity: Activity, boolean: Boolean? = null){
    if (result == 400 || result == 500 || result == 409 || result == 429 || result == 601) {
        technical_work.visibility = View.VISIBLE
        no_connection.visibility = View.GONE
        if (boolean!!){
            layout_res.visibility = View.GONE
        }
        access_restricted.visibility = View.GONE
        not_found.visibility = View.GONE
    } else if (result == 403) {
        access_restricted.visibility = View.VISIBLE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        if (boolean!!){
            layout_res.visibility = View.GONE
        }
        not_found.visibility = View.GONE
    } else if (result == 404) {
        not_found.visibility = View.VISIBLE
        access_restricted.visibility = View.GONE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        if (boolean!!){
            layout_res.visibility = View.GONE
        }
    } else if (result == 401) {
        initAuthorized(activity)
    }
}

fun listListResult(error: String, technical_work: LinearLayout, no_connection: LinearLayout, layout_res: LinearLayout, access_restricted: LinearLayout, not_found: LinearLayout, activity: Activity){
    if (error == "400" || error == "500" || error == "600" || error == "429" || error == "409" || error == "601") {
        technical_work.visibility = View.VISIBLE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
        access_restricted.visibility = View.GONE
        not_found.visibility = View.GONE
    } else if (error == "403") {
        access_restricted.visibility = View.VISIBLE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
        not_found.visibility = View.GONE
    } else if (error == "404") {
        not_found.visibility = View.VISIBLE
        access_restricted.visibility = View.GONE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        layout_res.visibility = View.GONE
    }
//    else if (error == "601") {
//        no_connection.visibility = View.VISIBLE
//        layout_res.visibility = View.GONE
//        technical_work.visibility = View.GONE
//        access_restricted.visibility = View.GONE
//        not_found.visibility = View.GONE
//    }
    else if (error == "401") {
        initAuthorized(activity)
    }
}

fun listListResult(error: String, technical_work: LinearLayout, no_connection: LinearLayout, layout_res: ConstraintLayout, access_restricted: LinearLayout, not_found: LinearLayout, activity: Activity, boolean: Boolean? = null){
    if (error == "400" || error == "500" || error == "600" || error == "429" || error == "409" || error == "601") {
        technical_work.visibility = View.VISIBLE
        no_connection.visibility = View.GONE
        if (boolean!!){
            layout_res.visibility = View.GONE
        }
        access_restricted.visibility = View.GONE
        not_found.visibility = View.GONE
    } else if (error == "403") {
        access_restricted.visibility = View.VISIBLE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        if (boolean!!){
            layout_res.visibility = View.GONE
        }
        not_found.visibility = View.GONE
    } else if (error == "404") {
        not_found.visibility = View.VISIBLE
        access_restricted.visibility = View.GONE
        technical_work.visibility = View.GONE
        no_connection.visibility = View.GONE
        if (boolean!!){
            layout_res.visibility = View.GONE
        }
    }
//    else if (error == "601") {
//        no_connection.visibility = View.VISIBLE
//        if (boolean!!){
//            layout_res.visibility = View.GONE
//        }
//        technical_work.visibility = View.GONE
//        access_restricted.visibility = View.GONE
//        not_found.visibility = View.GONE
//    }
    else if (error == "401") {
        initAuthorized(activity)
    }
}

private fun initAuthorized(activity: Activity) {
    val intent = Intent(activity, HomeActivity::class.java)
    AppPreferences.token = ""
    activity.startActivity(intent)
}

fun listListResult(error: String,  activity: AppCompatActivity){
    if (error == "400" || error == "500" || error == "600" || error == "429" || error == "409" || error == "601") {
        loadingMistakeIm(activity)
    } else if (error == "403") {
        loadingMistakeIm(activity)
    } else if (error == "404") {
        loadingMistakeIm(activity)
    }
//    else if (error == "601") {
//        loadingConnectionIm(activity)
//    }
    else if (error == "401") {
        initAuthorized(activity)
    }
}

fun listListResult(error: Int,  activity: AppCompatActivity){
    if (error == 400 || error == 500 || error == 600 || error == 429 || error == 409 || error == 601) {
        loadingMistakeIm(activity)
    } else if (error == 403) {
        loadingMistakeIm(activity)
    } else if (error == 404) {
        loadingMistakeIm(activity)
    }
//    else if (error == 601) {
//        loadingConnectionIm(activity)
//    }
    else if (error == 401) {
        initAuthorized(activity)
    }
}

fun listListResultHome(error: String,  activity: AppCompatActivity){
    if (error == "400" || error == "500" || error == "600" || error == "429" || error == "409" || error == "601") {
        loadingMistake(activity)
    } else if (error == "403") {
        loadingMistake(activity)
    } else if (error == "404") {
        loadingMistake(activity)
    }
//    else if (error == "601") {
//        loadingConnection(activity)
//    }
    else if (error == "401") {
        initAuthorized(activity)
    }
}

fun listListResultHome(error: Int,  activity: AppCompatActivity){
    if (error == 400 || error == 500 || error == 600 || error == 429 || error == 409 || error == 601) {
        loadingMistake(activity)
    } else if (error == 403) {
        loadingMistake(activity)
    } else if (error == 404) {
        loadingMistake(activity)
    }
//    else if (error == 601) {
//        loadingConnection(activity)
//    }
    else if (error == 401) {
        initAuthorized(activity)
    }
}
