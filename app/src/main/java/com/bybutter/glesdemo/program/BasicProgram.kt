package com.bybutter.glesdemo.program

import android.opengl.GLES20.*
import android.util.Log
import java.nio.FloatBuffer

/**
 * https://learnopengl-cn.readthedocs.io/zh/latest/01%20Getting%20started/04%20Hello%20Triangle/#_4
 */
class BasicProgram : Program {
    private var buffer: Int = 0
    private val vertexShaderSource = """
                attribute vec3 position;

                void main()
                {
                    gl_Position = vec4(position.x, position.y, position.z, 1.0);
                }
            """.trimIndent()
    private val fragmentShaderSource = """
                void main()
                {
                    gl_FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
                }
            """.trimIndent()

    private var program: Int = 0
    private val errorCode = intArrayOf(0)

    override fun doOnInit() {
        Log.e("SUIKA", "#1")
        val vertexShader = glCreateShader(GL_VERTEX_SHADER)
        Log.e("SUIKA", "#2")
        glShaderSource(vertexShader, vertexShaderSource)
        glCompileShader(vertexShader)
        glGetShaderiv(vertexShader, GL_COMPILE_STATUS, errorCode, 0)
        if (errorCode[0] == 0) {
            val infoLog = glGetShaderInfoLog(vertexShader)
            Log.e("SUIKA", "compile vertex shader fail: $infoLog")
        }

        val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragmentShader, fragmentShaderSource)
        glCompileShader(fragmentShader)
        glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, errorCode, 0)
        if (errorCode[0] == 0) {
            val infoLog = glGetShaderInfoLog(fragmentShader)
            Log.e("SUIKA", "compile fragment shader fail: $infoLog")
        }

        program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
        glGetProgramiv(program, GL_LINK_STATUS, errorCode, 0)
        if (errorCode[0] == 0) {
            val infoLog = glGetProgramInfoLog(program)
            Log.e("SUIKA", "link program fail: $infoLog")
        }

        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)

        val vertices = floatArrayOf(
                0.5f, -0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0f, 0.5f, 0f,
        )
        val verticesBuffer = FloatBuffer.wrap(vertices)
        val buffers = intArrayOf(0)
        glGenBuffers(1, buffers, 0)
        buffer = buffers[0]
        glBindBuffer(GL_ARRAY_BUFFER, buffer)
        glBufferData(GL_ARRAY_BUFFER, vertices.size, verticesBuffer, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3, 0)
        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0) // unbind

        glClearColor(0.375f, 0.8125f, 1.00f, 1.00f)
    }

    override fun doOnGameLoop() {
        glClear(GL_COLOR_BUFFER_BIT)

        glUseProgram(program)
        glBindBuffer(GL_ARRAY_BUFFER, buffer)
        glDrawArrays(GL_TRIANGLES, 0, 3)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun doOnResize(width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }
}