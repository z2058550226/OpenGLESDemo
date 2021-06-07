package com.bybutter.glesdemo.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MyGlSurfaceView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {
    init {
        setEGLContextClientVersion(2)
    }
}