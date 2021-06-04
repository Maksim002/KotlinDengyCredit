package com.example.kotlincashloan.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.WindowManager
import com.facebook.shimmer.ShimmerFrameLayout

private lateinit var thread: Thread
private var isSecond = false

fun animationGenerator(id: ShimmerFrameLayout, handler: Handler, activity: Activity) {
    try {
        thread = Thread() {
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                id.stopShimmerAnimation()
                //In transition: (alpha from 1 to 0)
                id.alpha = 1f;
                id.visibility = View.VISIBLE;
                id.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            id.visibility = View.GONE
                            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });
            }, 700)
            thread.interrupt()
        }
        thread.start()

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun animationLoanGenerator(id: ShimmerFrameLayout, handler: Handler, activity: Activity) {
    try {
        thread = Thread() {
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                id.stopShimmerAnimation()
                //In transition: (alpha from 1 to 0)
                id.alpha = 1f;
                id.visibility = View.VISIBLE;
                id.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            id.visibility = View.GONE
                            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });
            }, 1000)
            thread.interrupt()
        }
        thread.start()

    } catch (e: Exception) {
        e.printStackTrace()
    }
}