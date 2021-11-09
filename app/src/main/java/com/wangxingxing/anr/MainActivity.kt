package com.wangxingxing.anr

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.wangxingxing.anr.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    val TAG = "wxx"

    private val mBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        init()
    }

    private fun init() {
        mBinding.apply {
            btnInput.setOnClickListener {
                Thread.sleep(10000)
            }

            btnService.setOnClickListener {
                startService(Intent(this@MainActivity, MyService::class.java))
            }

            btnBroadcast.setOnClickListener {
                val intent = Intent("android.intent.action.DONGNAO_NING");
                intent.setPackage("com.wangxingxing.anr")
                sendBroadcast(intent)
            }

            btnContentProvider.setOnClickListener {
                contentResolver.query(URI, null, null, null, null)
            }
        }

        ANRWatchDog.start { stackTraceInfo ->
            Log.d(TAG, "应用程序没有响应...")
            Log.d(TAG, stackTraceInfo)
        }

        val dir = File("/data/anr/")
        Log.d("wxx", "exists:${dir.exists()}")
        /*dir.listFiles().forEach {
            Log.d("ning",it.name)
        }*/
//        fileObserver = ANRFileObserver(dir)
//        fileObserver.startWatching()
    }

    companion object {
        private const val AUTHORITY: String = "com.wangxingxing.student"
        val URI: Uri = Uri.parse("content://${Companion.AUTHORITY}/student")
    }
}