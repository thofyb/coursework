package com.example.staticization

object StatHelper {
    fun triangularNumber(n: Int): Int {
        var res = 0
        for (i in 0 until n) res += i
        return res
    }

    fun fibonacci(n: Int): Int {
        val cache = intArrayOf(1, 1)
        for (i in 2..n) {
            cache[i % 2] = cache[0] + cache[1]
        }
        return cache[n % 2]
    }

    fun gcd(v1: Int, v2: Int): Int {
        var a = v1
        var b = v2
        while (b != 0) {
            a %= b
            a += b
            b = a - b
            a -= b
        }
        return a
    }
}