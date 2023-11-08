package org.kotsuite.soot.extensions

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import soot.Body
import soot.Local

val log: Logger = LogManager.getLogger()

fun Body.getLocalByName(localName: String): Local? {
    for (local in this.locals) {
        if (local.name == localName) {
            return local
        }
    }

    log.error("Cannot get local: $localName")
    return null
}
