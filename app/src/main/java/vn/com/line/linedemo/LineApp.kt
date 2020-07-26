package vn.com.line.linedemo

import android.content.Context

class LineApp : androidx.multidex.MultiDexApplication() {


    init {
        instance = this
    }

    companion object {
        private var instance: LineApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}