package com.example.myapplication

class Example {
    fun add(a: Int, b: Int): Int {
        return a + b
    }

    fun bar(x: Int, b: Boolean): Int {
        return if (b) {
            x + 10
        } else {
            x - 10
        }
    }
}