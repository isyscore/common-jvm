@file:Suppress("PropertyName", "unused")

package com.isyscore.kotlin.common

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

enum class CommandProgressType { START, READLINE, READERROR, COMPLETE }
data class CommandResult(val output: String, val error: String)

class Command {
    var commands = mutableListOf<String>()
    var timeout = 3000L
    var workDir: String? = null

    var _progress: (CommandProgressType, String) -> Unit = { _, _ -> }
    var _result: (String, String) -> Unit = { _, _ -> }

    fun progress(p: (type: CommandProgressType, value: String) -> Unit) {
        _progress = p
    }

    fun result(r: (output: String, error: String) -> Unit) {
        _result = r
    }
}

fun runCommand(init: Command.() -> Unit): CommandResult {
    val c = Command().apply { init() }
    return CommandOperations.runCommand(c.commands, c.workDir, c.timeout, c._progress, c._result)
}

fun runCommand(cmd: String, timeOut: Long = 3000L): CommandResult = runCommand {
    commands.addAll(cmd.split(" "))
    timeout = timeOut
}

private object CommandOperations {
    fun runCommand(command: MutableList<String>, workDir: String?, timeout: Long, progress: (CommandProgressType, String) -> Unit, result: (String, String) -> Unit): CommandResult {
        var output = ""
        var outError = ""
        var process: Process? = null
        var procOutOs: BufferedReader? = null
        var procErrOs: BufferedReader? = null
        progress(CommandProgressType.START, "")
        try {
            process = Runtime.getRuntime().exec(command.toTypedArray(), null, if (workDir == null) null else File(workDir))
            procOutOs = BufferedReader(InputStreamReader(process.inputStream))
            procErrOs = BufferedReader(InputStreamReader(process.errorStream))
            val outStr = StringBuffer()
            val errStr = StringBuffer()

            var line: String?
            val start = System.currentTimeMillis()
            while (true) {
                if (procOutOs.ready()) {
                    line = procOutOs.readLine()
                    if (line != null) {
                        outStr.append("$line\n")
                        progress(CommandProgressType.READLINE, line)
                        continue
                    }
                }
                if (procErrOs.ready()) {
                    line = procErrOs.readLine()
                    if (line != null) {
                        errStr.append("$line\n")
                        progress(CommandProgressType.READERROR, line)
                        continue
                    }
                }

                if (process != null) {
                    try {
                        process.exitValue()
                        break
                    } catch (_: Exception) {
                    }
                }
                if (System.currentTimeMillis() - start > timeout) {
                    errStr.append("Timeout\n")
                    break
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500)
                } catch (_: Exception) {
                }

            }

            // process.waitFor()
            output = outStr.toString().trim()
            outError = errStr.toString().trim()
        } catch (e: Exception) {
            if (e.message != null) {
                outError = e.message!!
            }
        } finally {
            procOutOs?.close()
            procErrOs?.close()
            if (process != null) {
                try {
                    process.destroy()
                } catch (_: Throwable) {

                }
            }
        }
        progress(CommandProgressType.COMPLETE, "")
        result(output, outError)
        return CommandResult(output, outError)

    }
}