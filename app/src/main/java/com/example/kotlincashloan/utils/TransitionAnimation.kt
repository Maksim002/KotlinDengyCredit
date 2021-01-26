package com.example.kotlincashloan.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kotlincashloan.R

class TransitionAnimation(var activity: AppCompatActivity) {
    
    fun transitionRight(layout: LinearLayout){
        val animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_from_right)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                layout.setVisibility(View.VISIBLE)
            }
        })
        layout.startAnimation(animation)
    }

    fun transitionRightActivity(layout: ConstraintLayout){
        val animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_from_right)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                layout.setVisibility(View.VISIBLE)
            }
        })
        layout.startAnimation(animation)
    }
    
    fun transitionLeft(layout: LinearLayout){
        val animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_from_left)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                layout.setVisibility(View.VISIBLE)
            }
        })
        layout.startAnimation(animation)
    }

    fun transitionLeftActivity(layout: ConstraintLayout){
        val animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_from_left)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                layout.setVisibility(View.VISIBLE)
            }
        })
        layout.startAnimation(animation)
    }
}