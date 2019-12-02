package me.toptas.fancyshowcase

import me.toptas.fancyshowcase.internal.Properties
import org.junit.Test

class PropertiesTest {

    @Test
    fun testInitialValues() {
        val props = Properties()
        assert(props.fancyId == null)
    }

}