package com.example.myapplication

class Callee(private val name: String) {
    fun printHello() {
        println("Hello, $name")
    }
}