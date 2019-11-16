package me.toptas.fancyshowcase

import org.junit.Test

class PropertiesTest {

    @Test
    fun testInitialValues() {
        val props = Properties()
        assert(props.fancyId == null)
    }

}