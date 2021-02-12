package com.example.kotlincashloan.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.GeneralDialogAdapter
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.example.kotlincashloan.adapter.general.ListenerGeneralDialog
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_general_dialog.*


class GeneralDialogFragment(var listener: ListenerGeneralResult, var list: ArrayList<GeneralDialogModel>, var position: Int) : BottomSheetDialogFragment(),
    ListenerGeneralDialog {
    private var adapterDialog = GeneralDialogAdapter(position,this)

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
    }

    private fun initClick() {
        home_enter.setOnClickListener {
            dismiss()
        }
    }

    //Инцелезация recycler
    private fun initRecycler() {
        adapterDialog.update(list)
        recycler_dialog.adapter = adapterDialog
    }

    //Слушатель адаптера
    override fun listenerClickDialog(position: Int, name: String, key: String) {
//        AppPreferences.position = position.toString()
//        AppPreferences.key = key
        listener.listenerClickResult(list.get(position))
        dismiss()
    }

    //Предает даологовому окуну нужный нам стиль.
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme;
    }
}