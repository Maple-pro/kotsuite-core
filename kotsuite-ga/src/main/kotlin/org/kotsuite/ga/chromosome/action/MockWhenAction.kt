package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.ga.chromosome.parameter.Parameter
import org.kotsuite.soot.MockWhenActionType
import org.kotsuite.soot.Visibility
import soot.SootMethod

class MockWhenAction(
    val mockWhenActionType: MockWhenActionType,
    val mockObject: Variable,
    val methodVisibility: Visibility,
    val mockMethod: SootMethod,
    parameter: List<Parameter>,
    val returnValue: Parameter,
): Action(parameter) {
        override fun toString(): String {
            val mockObjectName = mockObject.localName
            val mockMethodName = mockMethod.name
            val returnValueString = returnValue.toString()

            return "when($mockObjectName, $methodVisibility, $mockMethodName).thenReturn($returnValueString)"
        }
}
