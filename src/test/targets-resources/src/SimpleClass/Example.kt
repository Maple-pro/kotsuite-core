package SimpleClass

class Example {
    fun foo() {
        val a = 0
        val b = 2
        bar(a, b)
    }

    fun bar(a: Int, b: Int) {
        val c = a * b
        val d = 0
        println(c)
    }
}