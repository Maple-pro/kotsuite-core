package org.kotsuite.utils.soot

import soot.tagkit.AnnotationClassElem
import soot.tagkit.AnnotationConstants
import soot.tagkit.AnnotationTag
import soot.tagkit.VisibilityAnnotationTag

object AnnotationUtils {
    fun generateTestAnnotation(): VisibilityAnnotationTag {
        val defaultAnnotationTag = VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE)
        val junitTestAnnotation = AnnotationTag("Lorg/junit/Test;")
        defaultAnnotationTag.addAnnotation(junitTestAnnotation)

        return defaultAnnotationTag
    }

    fun generateRunWithMockitoAnnotation(): VisibilityAnnotationTag {
        return generateRunWithAnnotation("Lorg/mockito/junit/MockitoJUnitRunner;")
    }

    fun generateRunWithRobolectricAnnotation(): VisibilityAnnotationTag {
        return generateRunWithAnnotation("Lorg/robolectric/RobolectricTestRunner;")
    }

    private fun generateRunWithAnnotation(elemDesc: String): VisibilityAnnotationTag {
        val defaultAnnotationTag = VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE)
        val mockitoAnnotationElem = AnnotationClassElem(elemDesc, 'c', "value")
        val runWithAnnotation = AnnotationTag("Lorg/junit/runner/RunWith;", listOf(mockitoAnnotationElem))
        defaultAnnotationTag.addAnnotation(runWithAnnotation)

        return defaultAnnotationTag
    }
}