package cn.afeibaili.gl.image

import cn.afeibaili.gl.exception.ArrayException
import cn.afeibaili.gl.exception.ImageException
import cn.afeibaili.gl.logger.LoggerFactory
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt


/**
 * # 纹理图集
 *
 * @author AfeiBaili
 * @version 2026/6/4 20:55
 */

class TextureAtlas(val atlas: List<Atlas>) {

    fun getUv(id: String, outUv: FloatArray) {
        if (outUv.size != 4) throw ArrayException("UV数组大小不为4")
        val atlas: Atlas = getAtlas(id) ?: throw ImageException("图集为空，id不正确: $id")
        val (x, y) = atlas.nameMap[id]!!

        val atlasSideF = atlas.atlasSide.value.toFloat()
        val imageSideF = atlas.imageSide.value.toFloat()

        val texelClamp = 0.5f

        outUv[0] = (x.toFloat() + texelClamp) / atlasSideF
        outUv[1] = (atlasSideF - (y.toFloat() + imageSideF + texelClamp)) / atlasSideF
        outUv[2] = (x.toFloat() + imageSideF - texelClamp) / atlasSideF
        outUv[3] = (atlasSideF - y.toFloat() - texelClamp) / atlasSideF
    }

    fun getAtlas(id: String): Atlas? = atlas.find { it.nameMap[id] != null }

    companion object {
        private val logger = LoggerFactory.create("TextureAtlas")

        fun create(name: String, imageFiles: List<File>): TextureAtlas {
            val atlasMap = HashMap<Side, MutableList<Pair<String, BufferedImage>>>()  //根据图片大小分类图片
            imageFiles.forEach { file ->
                val name = runCatching {
                    file.name.split(".").first()
                }.getOrElse {
                    throw ImageException("无法获取文件名字: ${file.canonicalPath}。请用png格式")
                }
                val readImageBuffer: BufferedImage = runCatching {
                    ImageIO.read(file)
                }.getOrElse { throw ImageException("此文件无法转为图片: ${file.canonicalPath}") }
                val width: Int = readImageBuffer.width
                val height: Int = readImageBuffer.height
                if (width != height) throw ImageException("图片宽高不一致，请用正方形图片")
                val side = Side(width)
                val images = atlasMap[side]
                if (images != null) images.add(name to readImageBuffer)
                else atlasMap[side] = mutableListOf(name to readImageBuffer)
            }

            val atlases = mutableListOf<Atlas>()
            atlasMap.forEach { (side, images) ->
                val ceil = ceil(sqrt(images.size.toDouble())).toInt()
                val atlasSide: Int = ceil * side.value
                val atlasBufferImage = BufferedImage(atlasSide, atlasSide, BufferedImage.TYPE_INT_ARGB)
                val nameMap = HashMap<String, Pair<Int, Int>>()

                var currentX: Int
                var currentY: Int
                images.forEachIndexed { index, (name, image) ->
                    currentX = (index % ceil) * side.value
                    currentY = (index / ceil) * side.value
                    atlasBufferImage.graphics.drawImage(image, currentX, currentY, null)
                    nameMap[name] = currentX to currentY
                }
                atlasBufferImage.graphics.dispose()
                atlases.add(
                    Atlas(
                        atlasBufferImage,
                        nameMap,
                        Size(images.size),
                        side,
                        Side(atlasSide),
                        Texture(atlasBufferImage)
                    )
                )
                val atlasSize = "${atlasSide}x$atlasSide"

                val writeFile = File("${System.getProperty("user.dir")}/temp/$name-$atlasSize.png")
                ImageIO.write(atlasBufferImage, "png", writeFile)
                logger.info("make texture atlas, size: $atlasSize, size: ${images.size}")
            }

            return TextureAtlas(atlases)
        }
    }

    @JvmInline
    value class Side(val value: Int)

    @JvmInline
    value class Size(val value: Int)

    class Atlas(
        val image: BufferedImage,
        val nameMap: Map<String, Pair<Int, Int>>,
        val size: Size,
        val imageSide: Side,
        val atlasSide: Side,
        val texture: Texture,
    )
}