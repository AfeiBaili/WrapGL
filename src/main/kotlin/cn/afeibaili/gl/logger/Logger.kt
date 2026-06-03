package cn.afeibaili.gl.logger


/**
 * # 日志类
 *
 *@author AfeiBaili
 *@version 2026/6/2 21:47
 */

interface Logger {
    val name: String

    fun info(msg: Any)
    fun warn(msg: Any)
    fun error(msg: Any)
    fun debug(msg: Any)

    companion object {
        var printDebug = true
        var writeFile = true
    }
}