package cn.afeibaili.gl.logger

import cn.afeibaili.gl.logger.Logger.Companion.printDebug
import cn.afeibaili.gl.logger.Logger.Companion.writeFile
import kotlinx.coroutines.*
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * # 内部日志实现
 *
 *@author AfeiBaili
 *@version 2026/6/2 21:33
 */

internal class LoggerImpl(override val name: String) : Logger {

    override fun info(msg: Any) {
        print("I", msg)
    }

    override fun warn(msg: Any) {
        print("W", msg)
    }

    override fun error(msg: Any) {
        print("E", msg, errPrinter)
    }

    override fun debug(msg: Any) {
        if (printDebug) print("D", msg)
    }


    private fun print(level: String, msg: Any, printer: PrintWriter = outPrinter) {
        val log = "[$level] ${getDate(formatter)} $name: $msg"
        loggerScope.launch { printer.println(log) }
        if (writeFile) loggerScope.launch { filePrinter.println(log) }
    }


    companion object {
        val loggerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        private val fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

        val outPrinter = PrintWriter(System.out, true)
        val errPrinter = PrintWriter(System.err, true)
        val filePrinter =
            PrintWriter(FileWriter(File("${System.getProperty("user.dir")}/log/${getDate(fileNameFormatter)}.txt").also {
                it.parentFile.mkdirs()
            }), true)

        private fun getDate(formatter: DateTimeFormatter) = LocalDateTime.now().format(formatter)

        init {
            Runtime.getRuntime().addShutdownHook(Thread {
                outPrinter.close()
                errPrinter.close()
                filePrinter.close()
                outPrinter.println("logger is closed")
                loggerScope.cancel()
            })
        }
    }
}