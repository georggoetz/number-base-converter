package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

val digits = (CharRange('0', '9') + CharRange('a', 'z')).toList()

fun String.toBigDecimal(base: Int, precision: Int = 5): BigDecimal {
    val b = BigDecimal(base)
    var ans = BigDecimal.ZERO
    var p = this.indexOf('.') - 1
    for (v in this.replace(".", "").map { digits.indexOf(it).toBigDecimal() }) {
        ans += v * if (p < 0) BigDecimal.ONE.divide(b.pow(-p), precision, RoundingMode.HALF_UP) else b.pow(p)
        p--
    }
    return ans
}

fun BigDecimal.toString(base: Int, precision: Int = 5): String {
    val integer = run {
        var s = ""
        val b = base.toBigInteger()
        var n = this.toBigInteger()
        while (n > BigInteger.ZERO) {
            s += digits[(n % b).toInt()]
            n /= b
        }
        s.reversed()
    }
    val fractional = run {
        var s = ""
        val b = base.toBigDecimal()
        var n = this % BigDecimal.ONE
        println(this)
        repeat(precision) {
            n = (n % BigDecimal.ONE) * b
            s += digits[n.toInt()]
        }
        s
    }
    return "$integer.$fractional"
}

fun convert(number: String, sourceBase: Int, targetBase: Int): String =
    if (number.contains('.')) {
        number.toBigDecimal(sourceBase).toString(targetBase)
    } else {
        BigInteger(number, sourceBase).toString(targetBase)
    }


fun main() {
    while (true) {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        val (sourceBase, targetBase) = when (val input = readln().trim().lowercase()) {
            "/exit" -> return
            else -> {
                try {
                    input.split("\\s+".toRegex()).map { it.toInt() }
                } catch (e: Throwable) {
                    System.err.println(e)
                    continue
                }
            }
        }
        while (true) {
            print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back) ")
            val number = when (val input = readln().trim().lowercase()) {
                "/back" -> break
                else -> {
                    try {
                        convert(input, sourceBase, targetBase)
                    } catch (e: Throwable) {
                        System.err.println(e)
                        e.printStackTrace()
                        continue
                    }
                }
            }
            println("Conversion result: $number")
            continue
        }
    }

}