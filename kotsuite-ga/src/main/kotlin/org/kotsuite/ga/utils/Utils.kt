package org.kotsuite.ga.utils

import org.slf4j.LoggerFactory
import soot.Local
import soot.SootClass
import soot.SootMethod

object Utils {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun getLocalByName(sootMethod: SootMethod, localName: String): Local {
        val body = sootMethod.activeBody
        var local: Local? = null
        for (l in body.locals) {
            if (l.name == localName) {
                local = l
                break
            }
        }

        if (local == null) {
            logger.error("Can't get local: $localName")
            throw Exception("Can't get local: $localName")
        }

        return local
    }

    fun getConstructor(sootClass: SootClass): SootMethod {
        return try {
            sootClass.getMethod("void <init>()")
        } catch (ex: RuntimeException) {
            sootClass.getMethodByName("<init>")
        }
    }

}