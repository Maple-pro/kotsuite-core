package org.kotsuite.ga.chromosome.value

import soot.ArrayType

class ArrayChromosomeValue<T : Any>(val arrayValue: Array<T>, val arrayType: ArrayType): ChromosomeValue()