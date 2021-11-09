package com.wangxingxing.anr

import android.os.Build
import android.os.FileObserver
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File

@RequiresApi(Build.VERSION_CODES.Q)
class ANRFileObserver constructor(file: File) : FileObserver(file, ALL_EVENTS) {

    override fun onEvent(event: Int, path: String?) {
        when (event) {
            ACCESS -> Log.i("wxx", "ACCESS: $path")
            ATTRIB -> Log.i("wxx", "ATTRIB: $path")
            CLOSE_NOWRITE -> Log.i("wxx", "CLOSE_NOWRITE: $path")
            CLOSE_WRITE -> Log.i("wxx", "CLOSE_WRITE: $path")
            CREATE -> Log.i("wxx", "CREATE: $path")
            DELETE -> Log.i("wxx", "DELETE: $path")
            DELETE_SELF -> Log.i("wxx", "DELETE_SELF: $path")
            MODIFY -> Log.i("wxx", "MODIFY: $path")
            MOVE_SELF -> Log.i("wxx", "MOVE_SELF: $path")
            MOVED_FROM -> Log.i("wxx", "MOVED_FROM: $path")
            MOVED_TO -> Log.i("wxx", "MOVED_TO: $path")
            OPEN -> Log.i("wxx", "OPEN: $path")
            else ->
                //ALL_EVENTS ： 包括上面的所有事件
                Log.i("wxx", "DEFAULT($event): $path")
        }
    }
}