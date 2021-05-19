package com.example.kotlincashloan.ui.support

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.chat.ChatAdapter
import com.example.kotlincashloan.utils.RequestGallery
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlinscreenscanner.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_chat.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class ChatFragment : Fragment() {
    private lateinit var webSocket: WebSocket
    private var SERVER_PATH = "wss://crm-api.molbulak.ru:2334/"
    private var chatAdapter = ChatAdapter()
    val CAMERA_PERM_CODE = 101
    private val IMAGE_PICK_CODE = 10
    lateinit var currentPhotoPath: String
    val handler = Handler()

    private var b = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {}
        RequestGallery(requireActivity())
        recyclerView.adapter = chatAdapter
        setTitle("Техподдержка", resources.getColor(R.color.whiteColor), "В сети")
        initiateSocketConnection()
        initClick()
    }

    //Осуществляет запрос на сервер
    private fun initiateSocketConnection() {
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(SERVER_PATH).build()
        webSocket = client.newWebSocket(request, SocketListener(requireActivity()))
    }

    private fun initClick() {
        chat_add_image.setOnClickListener {
            loadFiles()
        }

        val jsonObject = JSONObject()
        jsonObject.put("msg", "Привет как ваше нечего")
        jsonObject.put("isSent", true)
        chatAdapter.addItem(jsonObject)
        recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)

        handler.postDelayed(Runnable { // Do something after 5s = 500ms
            val jsonObject1 = JSONObject()
            jsonObject1.put("method", "Привет как ваше нечего")
            jsonObject1.put("isSent", false)
            chatAdapter.addItem(jsonObject1)
            recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }, 1000)
    }

    //Метод выгружает картинку с памяти телефона
    fun loadFiles() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERM_CODE
                )
            } else {
                getMyFile()
            }
        } else {
            getMyFile()
        }
    }

    private fun getMyFile() {
        try {
            val file = "photo"
            val dtoregDirectiry: File? =
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val files = File.createTempFile(file, ".jpg", dtoregDirectiry)
            currentPhotoPath = files.absolutePath
            val imagUri: Uri =
                FileProvider.getUriForFile(requireContext(), "com.example.kotlincashloan", files)
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = "image/*"
            val chooser = Intent.createChooser(pickIntent, "Some text here")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagUri)
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))
            startActivityForResult(chooser, IMAGE_PICK_CODE)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        RequestGallery(requireActivity()).activityResult(
            requestCode,
            resultCode,
            data,
            currentPhotoPath
        )
    }

    fun sendImage(image: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        val jsonObject = JSONObject()
        try {

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    //Передаёт в toolBar текст
    fun setTitle(title: String?, color: Int, textOnline: String? = null) {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.setTitle(title, color, textOnline)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onResume() {
        super.onResume()
        //меняет цвета навигационной понели
        ColorWindows(activity as AppCompatActivity).rollback()
        val backArrow = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        backArrow.setColorFilter(
            resources.getColor(android.R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )
        (activity as AppCompatActivity?)!!.supportActionBar!!.setHomeAsUpIndicator(backArrow)
    }
}