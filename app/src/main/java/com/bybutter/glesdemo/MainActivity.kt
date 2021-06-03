package com.bybutter.glesdemo

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * https://learnopengl-cn.readthedocs.io/zh/latest/01%20Getting%20started/04%20Hello%20Triangle/#_4
 */
class MainActivity : AppCompatActivity() {
    private val glSurfaceView by lazy { GLSurfaceView(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(glSurfaceView)

        glSurfaceView.setRenderer(object : GLSurfaceView.Renderer {
            private var bufferId: Int = 0
            private val vertexShader = """
                #version 330 core

                layout (location = 0) in vec3 position;

                void main()
                {
                    gl_Position = vec4(position.x, position.y, position.z, 1.0);
                }
            """.trimIndent()
            private val fragmentShader = """
                #version 330 core

                out vec4 color;

                void main()
                {
                    color = vec4(1.0f, 0.5f, 0.2f, 1.0f);
                }
            """.trimIndent()

            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                // new a vertex buffer object
                val buffers = IntArray(1)
                GLES20.glGenBuffers(1, buffers, 0)
                Log.e("SUIKA", "${buffers[0]}")
                bufferId = buffers[0]
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferId)
                val triangleVertices = floatArrayOf(
                        0.5f, -0.5f, 0f,
                        -0.5f, -0.5f, 0f,
                        0f, 0.5f, 0f,
                )
                val verticesBuffer = FloatBuffer.wrap(triangleVertices)
                GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, triangleVertices.size, verticesBuffer, GLES20.GL_STATIC_DRAW)

                // create vertex shader
                val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
                GLES20.glShaderSource(vertexShader, this.vertexShader)
                GLES20.glCompileShader(vertexShader)
                // check shader compile info
                val compileStatus = IntArray(1)
                GLES20.glGetShaderiv(vertexShader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
                if (compileStatus[0] == 0) {
                    val shaderInfoLog = GLES20.glGetShaderInfoLog(vertexShader)
                    Log.e("SUIKA", "compile shader fail: $shaderInfoLog")
                }

                // create fragment shader
                val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
                GLES20.glShaderSource(fragmentShader, this.fragmentShader)
                GLES20.glCompileShader(fragmentShader)
                // check shader compile info
                GLES20.glGetShaderiv(fragmentShader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
                if (compileStatus[0] == 0) {
                    val shaderINfoLog = GLES20.glGetShaderInfoLog(fragmentShader)
                    Log.e("SUIKA", "compile shader fail: $shaderINfoLog")
                }

                // create program
                val program = GLES20.glCreateProgram()
                GLES20.glAttachShader(program, vertexShader)
                GLES20.glAttachShader(program, fragmentShader)
                GLES20.glLinkProgram(program)
                GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, compileStatus, 0)
                if (compileStatus[0] == 0) {
                    val linkInfoLog = GLES20.glGetProgramInfoLog(program)
                    Log.e("SUIKA", "link program fail: $linkInfoLog")
                }
            }

            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            }

            override fun onDrawFrame(gl: GL10?) {
//                GLES20.glGenBuffers()
                GLES20.glClearColor(1f, 1f, 0.0f, 1f)
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

            }
        })
    }
}