package cn.afeibaili.gl

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL45
import org.lwjgl.opengl.GL45C
import java.io.Closeable


/**
 * # 应用程序窗口设置
 *
 *@author AfeiBaili
 *@version 2026/6/1 22:10
 */

class Window(
    var width: Int,
    var height: Int,
    var title: String,
    val windowLocation: Long,
    val clearColor: FloatArray,
) : Closeable {

    fun setViewport(width: Int, height: Int) {
        GL45C.glViewport(0, 0, width, height)
    }

    inline fun frameRender(action: () -> Unit) {
        while (!glfwWindowShouldClose(windowLocation)) {
            GL45C.glClearBufferfv(GL45C.GL_COLOR, 0, clearColor)
            action()
            glfwSwapBuffers(windowLocation)
            glfwPollEvents()
        }
    }

    override fun close() {
        glfwDestroyWindow(windowLocation)
        glfwTerminate()
    }

    companion object {
        fun builder() = WindowBuilder()
    }

    override fun toString(): String {
        return "Window(width=$width, height=$height, title='$title', windowLocation=$windowLocation)"
    }

}

class WindowBuilder() {
    var width = 800
    var height = 800
    var title = "Hello GL"
    var verticalSync = false
    var blocks = mutableListOf<() -> Unit>()
    var clearColor = floatArrayOf(1f, 1f, 1f, 1f)

    fun build(): Window {
        if (!isInitialised) {
            glfwInit()
        }
        val window: Long = glfwCreateWindow(width, height, title, 0, 0)
        glfwMakeContextCurrent(window)
        GL.createCapabilities()
        glfwSwapInterval(if (verticalSync) 1 else 0)

        GL45C.glEnable(GL45.GL_BLEND)
        GL45C.glBlendFunc(GL45.GL_SRC_ALPHA, GL45.GL_ONE_MINUS_SRC_ALPHA)

        blocks.forEach { it() }

        isInitialised = true
        return Window(width, height, title, window, clearColor)
    }

    fun withCustomBlock(block: () -> Unit): WindowBuilder {
        blocks.add(block)
        return this
    }

    fun withVerticalSync(boolean: Boolean): WindowBuilder {
        verticalSync = boolean
        return this
    }

    fun withClearColor(red: Float, green: Float, blue: Float, alpha: Float): WindowBuilder {
        clearColor = floatArrayOf(red, green, blue, alpha)
        return this
    }

    fun buildWidth(width: Int): WindowBuilder {
        this.width = width
        return this
    }

    fun buildHeight(height: Int): WindowBuilder {
        this.height = height
        return this
    }

    fun buildTitle(title: String): WindowBuilder {
        this.title = title
        return this
    }

    companion object {
        @JvmStatic
        private var isInitialised = false
    }
}