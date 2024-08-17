package com.isyscore.kotlin.common

private const val ESCAPE = '\u001B'
private const val RESET = "$ESCAPE[0m"
private const val BG_JUMP = 10

enum class TerminalColor(color: Int) {
    BLACK(30),
    RED(31),
    GREEN(32),
    YELLOW(33),
    BLUE(34),
    MAGENTA(35),
    CYAN(36),
    LIGHT_GRAY(37),
    DARK_GRAY(90),
    LIGHT_RED(91),
    LIGHT_GREEN(92),
    LIGHT_YELLOW(93),
    LIGHT_BLUE(94),
    LIGHT_MAGENTA(95),
    LIGHT_CYAN(96),
    WHITE(97);

    val foreground: String = "$ESCAPE[${color}m"
    val background: String = "$ESCAPE[${color + BG_JUMP}m"
}

private fun String.color(ansiString: String) = "$ansiString$this$RESET"
fun String.foreground(color: TerminalColor) = color(color.foreground)
fun String.background(color: TerminalColor) = color(color.background)

fun String.black() = foreground(TerminalColor.BLACK)
fun String.red(): String = foreground(TerminalColor.RED)
fun String.green(): String = foreground(TerminalColor.GREEN)
fun String.yellow(): String = foreground(TerminalColor.YELLOW)
fun String.blue(): String = foreground(TerminalColor.BLUE)
fun String.magenta(): String = foreground(TerminalColor.MAGENTA)
fun String.cyan(): String = foreground(TerminalColor.CYAN)
fun String.lightGray() = foreground(TerminalColor.LIGHT_GRAY)
fun String.lightRed() = foreground(TerminalColor.LIGHT_RED)
fun String.lightGreen() = foreground(TerminalColor.LIGHT_GREEN)
fun String.lightYellow() = foreground(TerminalColor.LIGHT_YELLOW)
fun String.lightBlue() = foreground(TerminalColor.LIGHT_BLUE)
fun String.lightMagenta() = foreground(TerminalColor.LIGHT_MAGENTA)
fun String.lightCyan() = foreground(TerminalColor.LIGHT_CYAN)
fun String.white() = foreground(TerminalColor.WHITE)

fun String.bgBlack() = background(TerminalColor.BLACK)
fun String.bgRed() = background(TerminalColor.RED)
fun String.bgGreen() = background(TerminalColor.GREEN)
fun String.bgYellow() = background(TerminalColor.YELLOW)
fun String.bgBlue() = background(TerminalColor.BLUE)
fun String.bgMagenta() = background(TerminalColor.MAGENTA)
fun String.bgCyan() = background(TerminalColor.CYAN)
fun String.bgLightGray() = background(TerminalColor.LIGHT_GRAY)
fun String.bgLightRed() = background(TerminalColor.LIGHT_RED)
fun String.bgLightGreen() = background(TerminalColor.LIGHT_GREEN)
fun String.bgLightYellow() = background(TerminalColor.LIGHT_YELLOW)
fun String.bgLightBlue() = background(TerminalColor.LIGHT_BLUE)
fun String.bgLightMagenta() = background(TerminalColor.LIGHT_MAGENTA)
fun String.bgLightCyan() = background(TerminalColor.LIGHT_CYAN)
fun String.bgWhite() = background(TerminalColor.WHITE)