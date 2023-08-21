import org.junit.Test
import java.lang.RuntimeException
import java.util.LinkedList
import java.util.Stack

class TestEval {
    @Test
    fun test() {
        /*
        Calculator.exec("4+(3*(3-1)+2)/2") // = 8
        Calculator.exec("4 + (-3 * ( 3 - 1 ) + 2)") // = 0
        Calculator.exec("4 +-/ (-3 * ( 3 - 1 ) + 2)") // incorrect expression!
        Calculator.exec("4.5+(3.2+3)/2") // = 7.6
        Calculator.exec("4.5+(3.2:3)/2") // incorrect expression!
        Calculator.exec("-4.5+(3.2-3)/2") // = -4.4
        */
        Calculator.exec("4+(3*(3-1)/2", mapOf())
        // Calculator.exec("a + 1 * 2", mapOf("a" to 5.0))

    }
}

object Calculator {

    fun exec(expression: String, varMap: Map<String, Double>) {
        try {
            if (expression.isEmpty()) {
                throw IllegalArgumentException("Blank Expression!")
            }
            var currentOperator: Char
            var opStackTop: Char
            val numStack = Stack<Double>()
            val opStack = Stack<Char>()
            opStack.push(TerminateTokens.startEndMark)
            val tokens = Tokenizer.exec(expression + TerminateTokens.startEndMark)
            var i = 0
            while (i < tokens.size) {
                val tk = tokens[i]
                if (tk is String) {
                    numStack.push(varMap[tk])
                } else if (tk is Double) {
                    numStack.push(tk)
                } else if (tk is Char) {
                    currentOperator = tk
                    opStackTop = opStack.peek()
                    when (CalculateMode.getRule(currentOperator, opStackTop)) {
                        '>' -> {
                            numStack.push(Calculate.exec(opStack.pop(), numStack.pop(), numStack.pop()))
                            i--
                        }
                        '<' -> opStack.push(currentOperator)
                        '=' -> {
                            if (currentOperator == TerminateTokens.startEndMark) {
                                println("$expression = ${numStack.peek()}")
                            } else if (currentOperator == ')') {
                                opStack.pop()
                            }
                        }
                    }
                }
                i++
            }
        } catch (e: Throwable) {
            println("Incorrect Expression: $expression - Error: ${e.message}")
        }
    }
}

object Tokenizer {

    private val buffer = StringBuilder()
    private var prevChar: Char? = null

    private fun clean() {
        buffer.clear()
        prevChar = null
    }

    private fun processNegativeNumber(exp: String, index: Int): Boolean {
        val c = exp[index]
        if ((c == '+' || c == '-')
            && (prevChar == null || TerminateTokens.getNegativeNumSensitiveToken().contains(prevChar))
            && !TerminateTokens.isTerminateToken(exp[index + 1])
        ) {
            buffer.append(c)
            return true
        }
        return false
    }

    fun exec(expression: String): List<Any> {
        clean()
        val exp = expression.replace(" ", "")
        val result = LinkedList<Any>()
        for (i in exp.indices) {
            val c = exp[i]
            if (TerminateTokens.isTerminateToken(c)) {
                if (processNegativeNumber(exp, i)) {
                    continue
                }
                if (buffer.isNotEmpty()) {
                    val d = buffer.toString().toDoubleOrNull()
                    if (d == null) {
                        result.add(buffer.toString())
                    } else {
                        result.add(d)
                    }
                    buffer.clear()
                }
                result.add(c)
            } else {
                buffer.append(c)
            }
            prevChar = c
        }
        return result
    }
}

object TerminateTokens {

    const val startEndMark = '#'
    private val tokens = mapOf(
        '+' to 0,
        '-' to 1,
        '*' to 2,
        '/' to 3,
        '(' to 4,
        ')' to 5,
        startEndMark to 6
    )
    private val negativeNumSensitive = mutableSetOf<Char>()

    @Synchronized
    fun getNegativeNumSensitiveToken(): Set<Char> {
        if (negativeNumSensitive.isEmpty()) {
            negativeNumSensitive.addAll(tokens.keys - ')')
        }
        return negativeNumSensitive
    }

    fun isTerminateToken(token: Char): Boolean =
        tokens.keys.contains(token)

    fun getTokenId(token: Char): Int =
        tokens[token] ?: -1

}

object CalculateMode {

    private val rules = arrayOf(
        //       +    -    *    /    (    )    #
        arrayOf('>', '>', '<', '<', '<', '>', '>'), // +
        arrayOf('>', '>', '<', '<', '<', '>', '>'), // -
        arrayOf('>', '>', '>', '>', '<', '>', '>'), // *
        arrayOf('>', '>', '>', '>', '<', '>', '>'), // /
        arrayOf('<', '<', '<', '<', '<', '=', 'o'), // (
        arrayOf('>', '>', '>', '>', 'o', '>', '>'), // )
        arrayOf('<', '<', '<', '<', '<', 'o', '=')  // #
    )

    fun getRule(currentOperator: Char, opStackTop: Char): Char =
        try {
            rules[TerminateTokens.getTokenId(opStackTop)][TerminateTokens.getTokenId(currentOperator)]
        } catch (e: Throwable) {
            throw RuntimeException("No rules wre defined for some token!")
        }

}

object Calculate {
    fun exec(operator: Char, right: Double, left: Double): Double =
        when (operator) {
            '+' -> left + right
            '-' -> left - right
            '*' -> left * right
            '/' -> left / right
            else -> throw IllegalArgumentException("Unsupported operator: $operator")
        }

}
