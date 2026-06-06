package cn.afeibaili.gl.render.camera

import cn.afeibaili.gl.render.setUniform
import cn.afeibaili.gl.render.shader.Program
import org.joml.Matrix4f

/**
 * # 相机类
 *
 * @author AfeiBaili
 * @version 2026/6/6 16:59
 */

class Camera(val projectionName: String, val viewName: String) {
    val projection: Matrix4f = Matrix4f().ortho(0f, 10f, 0f, 10f, -1f, 1f)
    val view = Matrix4f()

    fun scale(x: Float, y: Float, z: Float): Camera {
        view.scale(x, y, z)
        return this
    }

    fun translate(x: Float, y: Float, z: Float): Camera {
        view.translate(x, y, z)
        return this
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float): Camera {
        view.rotate(angle, x, y, z)
        return this
    }

    fun apply(program: Program) {
        uploadUniform(program, projectionName, viewName)
    }

    fun uploadUniform(program: Program, projectionName: String = "projection", viewName: String = "view") {
        setUniform(program, projectionName, m4f = projection)
        setUniform(program, viewName, m4f = view)
    }
}