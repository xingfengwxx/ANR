package com.wangxingxing.anr

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.annotation.Nullable


class MyContentProvider : ContentProvider() {
    companion object {
        //这里的AUTHORITY就是我们在AndroidManifest.xml中配置的authorities，这里的authorities可以随便写
        private const val AUTHORITY = "com.wangxingxing.student"

        //匹配成功后的匹配码
        private const val MATCH_ALL_CODE = 1
        private const val MATCH_ONE_CODE = 2
        private val NOTIFY_URI = Uri.parse("content://$AUTHORITY/student")
        private var uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        //在静态代码块中添加要匹配的 Uri
        init {
            //匹配不成功返回NO_MATCH(-1)
            /**
             * uriMatcher.addURI(authority, path, code); 其中
             * authority：主机名(用于唯一标示一个ContentProvider,这个需要和清单文件中的authorities属性相同)
             * path:路径路径(可以用来表示我们要操作的数据，路径的构建应根据业务而定)
             * code:返回值(用于匹配uri的时候，作为匹配成功的返回值)
             */
            uriMatcher.addURI(AUTHORITY, "student", MATCH_ALL_CODE) // 匹配记录集合
            uriMatcher.addURI(AUTHORITY, "student/#", MATCH_ONE_CODE) // 匹配单条记录
        }
    }

    override fun onCreate(): Boolean {
        return false
    }

    @Nullable
    override fun query(
        uri: Uri,
        @Nullable projection: Array<String>?,
        @Nullable selection: String?,
        @Nullable selectionArgs: Array<String>?,
        @Nullable sortOrder: String?
    ): Cursor? {
        when (uriMatcher.match(uri)) {
            MATCH_ALL_CODE -> {
                Log.d("wxx", "queryAll")
                Thread.sleep(100000)
            }
            MATCH_ONE_CODE -> {
            }
            else -> {
            }
        }
        return null
    }

    @Nullable
    override fun getType(uri: Uri): String? {
        return null
    }

    /**
     * 插入 使用UriMatch的实例中的match方法对传过来的 Uri进行匹配。 这里通过ContentResolver传过来一个Uri，
     * 用这个传过来的Uri跟在ContentProvider中静态代码块中uriMatcher.addURI加入的Uri进行匹配
     * 根据匹配的是否成功会返回相应的值，在上述静态代码块中调用uriMatcher.addURI(AUTHORITY,
     * "student",MATCH_CODE)这里的MATCH_CODE
     * 就是匹配成功的返回值，也就是说假如返回了MATCH_CODE就表示这个Uri匹配成功了
     * ，我们就可以按照我们的需求就行操作了,这里uriMatcher.addURI(AUTHORITY,
     * "person/data",MATCH_CODE)加入的Uri为：
     * content://com.wangxingxing.studentProvider/student
     * ，如果传过来的Uri跟这个Uri能够匹配成功，就会按照我们设定的步骤去执行相应的操作
     */
    @Nullable
    override fun insert(uri: Uri, @Nullable values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            MATCH_ALL_CODE -> {
                val aaa = values!!["aaa"] as String
                Log.d("wxx", "insertAll$aaa")
                //通知ContentObserver数据发生变化了
                notifyDataChanged()
            }
            MATCH_ONE_CODE -> {
            }
            else -> {
            }
        }
        return null
    }

    override fun delete(
        uri: Uri,
        @Nullable selection: String?,
        @Nullable selectionArgs: Array<String>?
    ): Int {
        when (uriMatcher.match(uri)) {
            MATCH_ALL_CODE -> Log.d("wxx", "deleteAll")
            MATCH_ONE_CODE -> {
            }
            else -> {
            }
        }
        return 0
    }

    override fun update(
        uri: Uri,
        @Nullable values: ContentValues?,
        @Nullable selection: String?,
        @Nullable selectionArgs: Array<String>?
    ): Int {
        when (uriMatcher.match(uri)) {
            MATCH_ALL_CODE -> Log.d("wxx", "updateAll")
            MATCH_ONE_CODE -> {
            }
            else -> {
            }
        }
        return 0
    }

    //通知指定URI数据已改变
    private fun notifyDataChanged() {
        context!!.contentResolver.notifyChange(NOTIFY_URI, null)
    }
}
