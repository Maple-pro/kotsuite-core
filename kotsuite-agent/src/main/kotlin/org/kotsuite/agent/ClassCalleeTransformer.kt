package org.kotsuite.agent

import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain

class ClassCalleeTransformer : ClassFileTransformer {

    override fun transform(
        loader: ClassLoader?,
        className: String?,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?,
        classfileBuffer: ByteArray?
    ): ByteArray {
        // TODO
        return super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer)
    }

}