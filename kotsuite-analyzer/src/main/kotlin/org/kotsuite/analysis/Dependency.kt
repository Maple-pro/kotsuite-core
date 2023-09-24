package org.kotsuite.analysis

import org.kotsuite.Configs
import java.io.File

object Dependency {
    /**
     * Get test framework dependencies from the libs directory,
     * junit-4.13.2.jar, hamcrest-core-1.3.jar,
     * mockito-core-5.5.0.jar, mockito-inline-5.5.0.jar, mockito-junit-jupiter-5.5.0.jar,
     * org.robolectric/4.10.3: robolectric-annotations-4.10.3.jar, robolectric-4.10.3.jar, etc.
     *
     */
    fun getTestFrameworkDependencies(): List<String> {
        return listOf(
            "junit-4.13.2.jar",
        ).map { Configs.libsPath }
    }
}