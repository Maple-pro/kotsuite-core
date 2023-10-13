package org.kotsuite.ga.chromosome.value

import soot.ArrayType

class ArrayValue<T>(val arrayValue: Array<T>, val arrayType: ArrayType): Value()