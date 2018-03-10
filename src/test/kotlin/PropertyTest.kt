package org.pyrhus.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.*

class PropertyTest {

    @Test
    fun testToStringMasksSecret() {
        assertThat(Property("key", "password", true).toString()).doesNotContain("password")
        assertThat(Property("key", "password", false).toString()).contains("password")
    }

}
