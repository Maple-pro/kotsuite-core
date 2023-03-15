package com.example.myapplication

class Example {

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

        return if (side1 == side2 && side2 == side3) {
            "Equilateral triangle"
        } else if (side1 == side2 || side1 == side3 || side2 == side3) {
            "Isosceles triangle"
        } else {
            "Scalene triangle"
        }
    }

    fun isValidISBN(input: String): Boolean {
        // Check if input is null or empty
        if (input.isNullOrEmpty()) {
            return false
        }

        // Remove any leading or trailing spaces, as well as hyphens
        val cleanedInput = input.trim().replace("-", "")

        // Check that input consists of exactly 10 digits (or 9 digits followed by X)
        val pattern = "^\\d{9}[\\d|X]?$"
        if (!cleanedInput.matches(pattern.toRegex())) {
            return false
        }

        // Calculate the check digit
        var sum = 0
        for (i in 0 until 9) {
            val digit = Character.getNumericValue(cleanedInput[i])
            sum += (i + 1) * digit
        }
        val checkDigit = sum % 11
        return if (checkDigit == 10) {
            cleanedInput.endsWith("X")
        } else {
            checkDigit == Character.getNumericValue(cleanedInput[9])
        }
    }

    fun isValidChemicalId(input: String): Boolean {
        // check length
        if (input.length != 10) {
            return false
        }

        // check first two characters
        if (!input.substring(0, 2).matches(Regex("[A-Za-z]{2}"))) {
            return false
        }

        // check third character
        if (!input[2].isDigit()) {
            return false
        }

        // check fourth to sixth characters
        if (!input.substring(3, 6).matches(Regex("[A-Za-z]{3}"))) {
            return false
        }

        // check seventh to ninth characters
        if (!input.substring(6, 9).matches(Regex("\\d{3}"))) {
            return false
        }

        // check last character
        if (!input[9].isLetter()) {
            return false
        }

        return true
    }

}