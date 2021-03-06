package com.bybutter.glesdemo

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bybutter.glesdemo.program.GoogleDemoProgram
import com.bybutter.glesdemo.widget.MyGlSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainActivity : AppCompatActivity() {
    private val glSurfaceView by lazy { MyGlSurfaceView(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(glSurfaceView)

        glSurfaceView.setRenderer(object : GLSurfaceView.Renderer {
            private val program = GoogleDemoProgram()
            override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
                program.doOnInit()
            }

            override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
                program.doOnResize(width, height)
            }

            override fun onDrawFrame(gl: GL10) {
                program.doOnGameLoop()
                glSurfaceView.requestRender()
            }
        })
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        glSurfaceView.requestRender()
    }
}