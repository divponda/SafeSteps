package com.safesteps.app

import com.safesteps.app.utils.TimerConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TimerTest {

    @Test
    fun minutesToSecondsConversion() {
        val minutes = 5
        val expectedSeconds = 300
        assertEquals(expectedSeconds, minutes * TimerConstants.SecondsPerMinute)
    }

    @Test
    fun remainingTimeDisplayFormat() {
        val totalSeconds = 125
        val minutes = totalSeconds / TimerConstants.SecondsPerMinute
        val seconds = totalSeconds % TimerConstants.SecondsPerMinute
        assertEquals(2, minutes)
        assertEquals(5, seconds)
    }

    @Test
    fun defaultDurationIsWithinValidRange() {
        assertTrue(TimerConstants.DefaultTimerDurationMinutes >= TimerConstants.MinimumTimerDurationMinutes)
        assertTrue(TimerConstants.DefaultTimerDurationMinutes <= TimerConstants.MaximumTimerDurationMinutes)
    }

    @Test
    fun sliderStepsMatchDurationRange() {
        val expectedSteps = TimerConstants.MaximumTimerDurationMinutes -
            TimerConstants.MinimumTimerDurationMinutes - 1
        assertEquals(expectedSteps, TimerConstants.SliderSteps)
    }
}
