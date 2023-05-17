package org.kotsuite.agent

import java.lang.instrument.Instrumentation

class Main {
    companion object {
        @JvmStatic
        fun premain(agentArgs: String?, inst: Instrumentation) {
            println("premain: Agent Started.")

            inst.addTransformer(ClassCalleeTransformer())
        }

        @JvmStatic
        fun agentmain(agentArgs: String?, inst: Instrumentation) {
            println("agentmain: Agent Started.")
        }
    }
}