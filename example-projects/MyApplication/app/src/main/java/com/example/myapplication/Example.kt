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

    fun foo(callee: Callee) {
        callee.printHello()
    }

    fun getTriangleType(side1: Double, side2: Double, side3: Double): String {
        if (side1 <= 0 || side2 <= 0 || side3 <= 0) {
            return "Invalid triangle: type 1"
        }

        if ((side1 + side2 <= side3) || (side1 + side3 <= side2) || (side2 + side3 <= side1)) {
            return "Invalid triangle: type2"
        }

        if (side1 == side2 && side2 == side3) {
            return "Equilateral triangle"
        } else if (side1 == side2 || side1 == side3 || side2 == side3) {
            return "Isosceles triangle"
        } else {
            return "Scalene triangle"
        }
    }
}