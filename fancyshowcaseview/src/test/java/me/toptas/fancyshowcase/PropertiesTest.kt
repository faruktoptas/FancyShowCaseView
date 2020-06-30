package me.toptas.fancyshowcase

import me.toptas.fancyshowcase.internal.Properties
import org.junit.Test

/**
 * Test for non-Android Properties
 */
class PropertiesTest {

    /**
     * Test initial values
     */
    @Test
    fun testInitialValues() {
        val props = Properties()
        assert(props.fancyId == null)
    }

}