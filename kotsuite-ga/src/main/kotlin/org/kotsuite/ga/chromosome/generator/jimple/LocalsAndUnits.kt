package org.kotsuite.ga.chromosome.generator.jimple

import soot.Local
import soot.Unit

data class LocalsAndUnits(val locals: List<Local>, val units: List<Unit>)
