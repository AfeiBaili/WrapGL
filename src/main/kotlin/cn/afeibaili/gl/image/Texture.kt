package cn.afeibaili.gl.image

import cn.afeibaili.gl.exception.ImageException
import org.lwjgl.opengl.GL45C.*
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.io.Closeable
import java.nio.ByteBuffer

/**
 * # 纹理
 *
 * @author AfeiBaili
 * @version 2026/6/5 23:25
 */

class Texture(val bufferedImage: BufferedImage) : Closeable {
    val textureLocation: Int = glCreateTextures(GL_TEXTURE_2D)
    val data: ByteBuffer =
        MemoryUtil.memAlloc(bufferedImage.width * bufferedImage.height * Float.SIZE_BYTES)
    val width: Int = bufferedImage.width
    val height: Int = bufferedImage.height
    var isUpload = false


    fun upload() {
        glTextureStorage2D(textureLocation, 1, GL_RGBA8, bufferedImage.width, bufferedImage.height)
        glTextureParameteri(textureLocation, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTextureParameteri(textureLocation, GL_TEXTURE_MIN_FILTER, GL_NEAREST)

        val pixels = IntArray(width * height)
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = pixels[y * width + x]
                //A00000000 R00000000 G00000000 B00000000
                data.put(((pixel shr 16) and 0xff).toByte())
                data.put(((pixel shr 8) and 0xff).toByte())
                data.put(((pixel shr 0) and 0xff).toByte())
                data.put(((pixel shr 24) and 0xff).toByte())
            }
        }

        data.flip()

        glTextureSubImage2D(
            textureLocation, 0, 0, 0,
            width, height,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            data
        )

        isUpload = true
    }

    fun bind() {
        if (!isUpload) throw ImageException("纹理并未上传至GPU")
        glBindTextureUnit(0, textureLocation)
    }

    override fun close() {
        glDeleteTextures(textureLocation)
        MemoryUtil.memFree(data)
    }
}