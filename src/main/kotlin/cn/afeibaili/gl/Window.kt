package cn.afeibaili.gl

import org.lwjgl.glfw.GLFW.*
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
) : Closeable {

    override fun close() {
        glfwDestroyWindow(windowLocation)
        glfwTerminate()
    }

    inline fun frameRender(action: () -> Unit) {
        while (!glfwWindowShouldClose(windowLocation)) {
            action()
            glfwSwapBuffers(windowLocation)
            glfwPollEvents()
        }
    }

    companion object {
        fun builder() = WindowBuilder()
    }
}

class WindowBuilder() {
    var width = 800
    var height = 800
    var title = "Hello GL"
    var verticalSync = false
    var blocks = mutableListOf<() -> Unit>()

    fun build(): Window {
        if (isInitialised) glfwInit()
        val window: Long = glfwCreateWindow(width, height, title, 0, 0)
        glfwSwapInterval(if (verticalSync) 1 else 0)

        blocks.forEach { it() }

        isInitialised = true
        return Window(width, height, title, window)
    }

    fun withCustomBlock(block: () -> Unit): WindowBuilder {
        blocks.add(block)
        return this
    }

    fun withVerticalSync(boolean: Boolean): WindowBuilder {
        verticalSync = boolean
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