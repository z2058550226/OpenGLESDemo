package com.bybutter.glesdemo.program

import android.opengl.GLES20.*
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class GoogleDemoProgram : Program {
    private val triangle = Triangle()
    override fun doOnInit() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        triangle.init()
    }

    override fun doOnGameLoop() {
        glClear(GL_COLOR_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        triangle.draw(vPMatrix)
    }

    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun doOnResize(width: Int, height: Int) {
        glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }
}

fun loadShader(type: Int, shaderCode: String): Int {

    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    return glCreateShader(type).also { shader ->

        // add the source code to the shader and compile it
        glShaderSource(shader, shaderCode)
        glCompileShader(shader)
    }
}

// number of coordinates per vertex in this array
const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(     // in counterclockwise order:
        0.0f, 0.622008459f, 0.0f,      // top
        -0.5f, -0.311004243f, 0.0f,    // bottom left
        0.5f, -0.311004243f, 0.0f      // bottom right
)

class Triangle {

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
          gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    var program = 0


    fun init() {
        val vertexShader: Int = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        program = glCreateProgram().also {

            // add the vertex shader to program
            glAttachShader(it, vertexShader)

            // add the fragment shader to program
            glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            glLinkProgram(it)
        }
    }

    private var vertexBuffer: FloatBuffer =
            // (number of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
                // use the device hardware's native byte order
                order(ByteOrder.nativeOrder())

                // create a floating point buffer from the ByteBuffer
                asFloatBuffer().apply {
                    // add the coordinates to the FloatBuffer
                    put(triangleCoords)
                    // set the buffer to read the first coordinate
                    position(0)
                }
            }

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var vPMatrixHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    fun draw(vPMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        glUseProgram(program)

        // get handle to vertex shader's vPosition member
        positionHandle = glGetAttribLocation(program, "vPosition").also {

            // Enable a handle to the triangle vertices
            glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            glVertexAttribPointer(it, COORDS_PER_VERTEX, GL_FLOAT, false, vertexStride, vertexBuffer)

            // get handle to fragment shader's vColor member
            mColorHandle = glGetUniformLocation(program, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                glUniform4fv(colorHandle, 1, color, 0)
            }

            // get handle to shape's transformation matrix
            vPMatrixHandle = glGetUniformLocation(program, "uMVPMatrix").also { pMatrixHandle ->

                // Pass the projection and view transformation to the shader
                glUniformMatrix4fv(pMatrixHandle, 1, false, vPMatrix, 0)
            }

            // Draw the triangle
            glDrawArrays(GL_TRIANGLES, 0, vertexCount)

            // Disable vertex array
            glDisableVertexAttribArray(it)
        }
    }
}

// number of coordinates per vertex in this array
var squareCoords = floatArrayOf(
        -0.5f, 0.5f, 0.0f,      // top left
        -0.5f, -0.5f, 0.0f,      // bottom left
        0.5f, -0.5f, 0.0f,      // bottom right
        0.5f, 0.5f, 0.0f       // top right
)

class Square2 {

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    // initialize vertex byte buffer for shape coordinates
    private val vertexBuffer: FloatBuffer =
            // (# of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(squareCoords.size * 4).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(squareCoords)
                    position(0)
                }
            }

    // initialize byte buffer for the draw list
    private val drawListBuffer: ShortBuffer =
            // (# of coordinate values * 2 bytes per short)
            ByteBuffer.allocateDirect(drawOrder.size * 2).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(drawOrder)
                    position(0)
                }
            }
}