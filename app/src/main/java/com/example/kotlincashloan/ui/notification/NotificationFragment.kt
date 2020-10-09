package com.example.kotlincashloan.ui.notification


import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.notification.NotificationAdapter
import com.example.kotlincashloan.adapter.profile.MyOperationModel
import kotlinx.android.synthetic.main.fragment_notification.*

class NotificationFragment : Fragment() {
    private var myAdapter = NotificationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()
        requireActivity().onBackPressedDispatcher.addCallback(this) {}
        initRecycler()

//        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        val clip = ClipData.newPlainText("label", "text_token.text.toString()")
//        clipboard.setPrimaryClip(clip)
//        Toast.makeText(context, "Сopied", Toast.LENGTH_LONG).show()
    }

    private fun initRecycler() {
        val list: ArrayList<MyOperationModel> = arrayListOf()
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))

        myAdapter.update(list)
        notification_recycler.adapter = myAdapter
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            requireActivity().getWindow()
                .setStatusBarColor(requireActivity().getColor(R.color.orangeColor))
            val decorView: View = (activity as AppCompatActivity).getWindow().getDecorView()
            var systemUiVisibilityFlags = decorView.systemUiVisibility
            systemUiVisibilityFlags =
                systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            decorView.systemUiVisibility = systemUiVisibilityFlags
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar);
            toolbar.setBackgroundDrawable(ColorDrawable(requireActivity().getColor(R.color.orangeColor)))
            toolbar.setTitleTextColor(requireActivity().getColor(R.color.whiteColor))
        }
    }
}