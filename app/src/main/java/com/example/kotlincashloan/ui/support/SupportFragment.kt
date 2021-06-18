package com.example.kotlincashloan.ui.support

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.support.SupportAdapter
import com.example.kotlincashloan.adapter.support.SupportListener
import com.example.kotlincashloan.extension.animationNoMargaritas
import com.example.kotlincashloan.extension.listListResult
import com.example.kotlincashloan.service.model.support.ListFaqResultModel
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_support.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*


class SupportFragment : Fragment(), SupportListener {
    private var myAdapter = SupportAdapter(this)
    private var viewModel = SupportViewModel()
    private val map = HashMap<String, String>()
    val handler = Handler()
    private var list: ArrayList<ListFaqResultModel> = arrayListOf()
    private var heightRecycler = 0
    private var heightLiner = 0
    private var primaryInput = false
    private var genAnim = false

    //    private var refresh = false
    private var errorCode = ""
    private var foresight = false

    var firstStart = false
    var heightSize = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        requireActivity().onBackPressedDispatcher.addCallback(this) {}
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())

        profile_recycler.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS;

        setTitle("FAQ", resources.getColor(R.color.whiteColor))

        iniClick()
        initRefresh()
    }

    fun setTitle(title: String?, color: Int) {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.setTitle(title, color)
        }
    }

    private fun initRestart() {
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            support_no_connection.visibility = View.VISIBLE
            support_constraint.visibility = View.GONE
            support_not_found.visibility = View.GONE
            support_technical_work.visibility = View.GONE
            layout_access_restricted.visibility = View.GONE
            shimmer_support.visibility = View.GONE
            errorCode = "601"
            viewModel.error.value = null
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            if (viewModel.listFaqDta.value == null) {
                requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (!viewModel.refreshCode) {
                    swipe()
                } else {
                    swipe()
                }
            } else {
                if (viewModel.error.value != null) {
                    viewModel.listFaqDta.postValue(null)
                    viewModel.error.value = null
                }
                viewModel.listFaq(map)
            }
        }
    }

    private fun isRestart() {
         ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            support_no_connection.visibility = View.VISIBLE
            support_constraint.visibility = View.GONE
            support_not_found.visibility = View.GONE
            support_technical_work.visibility = View.GONE
            layout_access_restricted.visibility = View.GONE
            shimmer_support.visibility = View.VISIBLE
            errorCode = "601"
            viewModel.error.value = null
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            if (viewModel.listFaqDta.value == null) {
                viewModel.refreshCode = false
                viewModel.listFaqDta.value = null
                viewModel.error.value = null
                viewModel.listFaq(map)
                initRecycler()
            } else {
                viewModel.refreshCode = false
                viewModel.listFaqDta.value = null
                viewModel.error.value = null
                viewModel.listFaq(map)
                initRecycler()
            }
        }
    }

    // отправлет model и возврощает ответ
    private fun swipe() {
        viewModel.refreshCode = false
        viewModel.error.value = null
        viewModel.listFaq(map)
        initRecycler()
    }

    private fun iniClick() {
        no_connection_repeat.setOnClickListener {
            isRestart()
        }

        access_restricted.setOnClickListener {
            isRestart()
        }

        technical_work.setOnClickListener {
            isRestart()
        }

        not_found.setOnClickListener {
            isRestart()
        }

        support_button_res.setOnClickListener {
            findNavController().navigate(R.id.chat_navigation)
        }

        support_button_lay.setOnClickListener {
            findNavController().navigate(R.id.chat_navigation)
        }
    }

    private fun initRefresh() {
        support_swipe_layout.setOnRefreshListener {
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                foresight = false
                viewModel.refreshCode = true
//                refresh = true
                isRestart()
            }, 500)
        }
        support_swipe_layout.setColorSchemeResources(android.R.color.holo_orange_dark)
    }

    private fun initRecycler() {
        try {
            ObservedInternet().observedInternet(requireContext())
            if (!AppPreferences.observedInternet) {
                support_no_connection.visibility = View.VISIBLE
                support_constraint.visibility = View.GONE
                support_not_found.visibility = View.GONE
                support_technical_work.visibility = View.GONE
                layout_access_restricted.visibility = View.GONE
                shimmer_support.visibility = View.VISIBLE
                viewModel.error.value = null
            } else {
                viewModel.listFaqDta.observe(viewLifecycleOwner, Observer { result ->
                    try {
                        if (result.result != null) {
                            list = result.result
                            myAdapter.update(list)
                            profile_recycler.adapter = myAdapter
//                            initVisibilities()
                            layout_support_null.visibility = View.GONE
                            if (genAnim){
                                shimmer_support.visibility = View.GONE
                            }else{
                                shimmer_support.visibility = View.VISIBLE
                            }
                            support_constraint.visibility = View.VISIBLE
                            if (!genAnim) {
                                //генерирует анимацию перехода
                                animationNoMargaritas(shimmer_support, handler, requireActivity())
                                genAnim = true
                            }
                            errorCode = result.code.toString()
                            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            try {
                                support_swipe_layout.isRefreshing = false
                            }catch (e: Exception){
                                e.printStackTrace()
                            }
                        } else {
                            if (result.error.code != null) {
                                errorCode = result.error.code.toString()

                                listListResult(result.error.code!!.toInt(),
                                    support_technical_work as LinearLayout,
                                    support_no_connection as LinearLayout,
                                    support_constraint,
                                    layout_access_restricted as LinearLayout,
                                    support_not_found as LinearLayout,
                                    requireActivity(), true)
                                shimmer_support.visibility = View.GONE
                                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                try {
                                    support_swipe_layout.isRefreshing = false
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

                viewModel.error.observe(viewLifecycleOwner, Observer { error ->
                    if (error != null) {
                        errorCode = error

                        listListResult(error.toString(), support_technical_work as LinearLayout,
                            support_no_connection as LinearLayout,
                            support_constraint,
                            layout_access_restricted as LinearLayout,
                            support_not_found as LinearLayout,
                            requireActivity(), true)
                        shimmer_support.visibility = View.GONE
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        support_swipe_layout.isRefreshing = false
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClickListener(item: ListFaqResultModel) {
//        try {
//            if (!item.clicked) {
//                foresight = true
//                if (!primaryInput) {
//                    handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                        isSwitchingFalse()
//                        primaryInput = true
//                    }, 600)
//                } else {
//                    isSwitchingFalse()
//                }
//            } else {
//                foresight = false
//                isSwitchingTrue()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

//    //Метод запрашивает и сравнивает размеры окон
//    fun fitView(recyclerView: RecyclerView? = null) {
//        // запрашивает размер NestedScrollView
//        layout_liner.post {
//            heightLiner = layout_liner.height
//            // запрашивает размер RecyclerView
//            recyclerView!!.post {
//                heightRecycler = profile_recycler.height
//                // проверяет если это первичный старт
//                // Отрезмера NestedScrollView отнемает -240
//                if (!firstStart) {
//                    heightSize = heightLiner - 240
//                    firstStart = true
//                }
//                // Сравнивает размер окон
//                if (heightRecycler >= heightSize) {
//                    isSwitchingFalse()
//                } else {
//                    isSwitchingTrue()
//                }
//            }
//        }
//    }

//    private fun isSwitchingTrue() {
//        support_button_res.visibility = View.GONE
//        support_button_lay.visibility = View.VISIBLE
//    }
//
//    private fun isSwitchingFalse() {
//        support_button_res.visibility = View.VISIBLE
//        support_button_lay.visibility = View.GONE
//    }

//    fun initVisibilities() {
//        support_no_connection.visibility = View.GONE
//        layout_access_restricted.visibility = View.GONE
//        support_technical_work.visibility = View.GONE
//        support_not_found.visibility = View.GONE
//        if (!foresight) {
//            if (myAdapter.itemCount == list.size) {
//                fitView(profile_recycler)
//            }
//        } else {
//            if (myAdapter.itemCount == list.size) {
//                handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                    fitView(profile_recycler)
//                }, 300)
//            }
//        }
//    }

    override fun onStart() {
        super.onStart()
        shimmer_support.startShimmerAnimation()
        if (viewModel.listFaqDta.value != null) {
            initRecycler()
        } else {
            viewModel.refreshCode = false
            initRestart()
        }
    }

    override fun onResume() {
        super.onResume()
        //меняет цвета навигационной понели
        ColorWindows(activity as AppCompatActivity).noRollback()
    }
}