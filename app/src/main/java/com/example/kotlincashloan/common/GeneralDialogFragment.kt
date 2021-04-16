package com.example.kotlincashloan.common

import android.os.Bundle
import android.view.*
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.GeneralDialogAdapter
import com.example.kotlincashloan.adapter.general.ListenerGeneralDialog
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.example.kotlincashloan.utils.KeyboardUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_general_dialog.*


class GeneralDialogFragment(var listener: ListenerGeneralResult, var list: ArrayList<GeneralDialogModel>, var position: String, var title: String, var idText: AutoCompleteTextView) : BottomSheetDialogFragment(), ListenerGeneralDialog {
    private var adapterDialog = GeneralDialogAdapter(position, this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_general_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initClick()
        setText()
    }


    private fun setText() {
        bottom_dialog_title.text = title
    }

    private fun initClick() {
        home_enter.setOnClickListener {
            idText.isEnabled = true
            dismiss()
        }

        dialog_text.addTextChangedListener {
            val converter  = it.toString().toLowerCase()
            adapterDialog.filter(converter);
        }
    }

    //Инцелезация recycler
    private fun initRecycler() {
        if (list.size <= 10){
            dialog_text.visibility = View.GONE
        }

        val params: ViewGroup.LayoutParams = recycler_dialog.getLayoutParams()
        if (list.size == 2){
            params.height = 360
        }else if (list.size == 3){
            params.height = 620
        }else{
            params.height = 580
        }
        recycler_dialog.layoutParams = params

        adapterDialog.update(list)
        recycler_dialog.adapter = adapterDialog
    }

    //Слушатель адаптера
    override fun listenerClickDialog(position: Int, name: String, key: String) {
        listener.listenerClickResult(list.get(position))
        dismiss()
    }

    //Предает даологовому окуну нужный нам стиль.
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme;
    }

    override fun onStart() {
        super.onStart()
        KeyboardUtil(requireActivity(), view)
    }
}