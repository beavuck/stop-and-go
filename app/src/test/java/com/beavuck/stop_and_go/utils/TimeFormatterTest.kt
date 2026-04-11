package com.beavuck.stop_and_go.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatterTest {

    @Test
    fun `splitTime below 60 seconds uses S format`() {
        assertEquals(TimeComponents(0, 0, 0, TimeFormat.S), splitTime(0))
        assertEquals(TimeComponents(0, 0, 1, TimeFormat.S), splitTime(1))
        assertEquals(TimeComponents(0, 0, 59, TimeFormat.S), splitTime(59))
    }

    @Test
    fun `splitTime at 60 seconds uses MS format`() {
        assertEquals(TimeComponents(0, 1, 0, TimeFormat.MS), splitTime(60))
    }

    @Test
    fun `splitTime below one hour uses MS format`() {
        assertEquals(TimeComponents(0, 1, 1, TimeFormat.MS), splitTime(61))
        assertEquals(TimeComponents(0, 59, 59, TimeFormat.MS), splitTime(3599))
    }

    @Test
    fun `splitTime at exactly one hour uses HMS format`() {
        assertEquals(TimeComponents(1, 0, 0, TimeFormat.HMS), splitTime(3600))
    }

    @Test
    fun `splitTime above one hour uses HMS format`() {
        assertEquals(TimeComponents(1, 0, 1, TimeFormat.HMS), splitTime(3601))
        assertEquals(TimeComponents(1, 59, 59, TimeFormat.HMS), splitTime(7199))
        assertEquals(TimeComponents(2, 0, 0, TimeFormat.HMS), splitTime(7200))
    }
}
