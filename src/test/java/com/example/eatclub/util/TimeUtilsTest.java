package com.example.eatclub.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.eatclub.util.TimeUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class TimeUtilsTest {

    @Test
    @DisplayName("When convertToMinutes and the time is 3:00am then return 180")
    void convertToMinutesAMTest(){
        assertEquals(180, convertToMinutes("3:00am"));
    }

    @Test
    @DisplayName("When convertToMinutes and the time is 3:00pm then return 900")
    void convertToMinutesPMTest(){
        assertEquals(900, convertToMinutes("3:00pm"));
    }

    @Test
    @DisplayName("When convertToMinutes and the time is midnight then return 0")
    void convertToMinutesMidnightTest(){
        assertEquals(0, convertToMinutes("12:00am"));
    }

    @Test
    @DisplayName("When isTimeInRange and the target is in range then return true")
    void isTimeInRangeInRangeTest(){
        assertTrue(isTimeInRange(60, 60, 120));
    }

    @Test
    @DisplayName("When isTimeInRange and the target is out of range then return false")
    void isTimeInRangeOutOfRangeTest(){
        assertFalse(isTimeInRange(60, 0, 60));
    }

    @Test
    @DisplayName("When formatMinutesToTime and minutes is in AM time then return formatted time in AM")
    void formatMinutesToTimeAMTest(){
        assertEquals("3:00am", formatMinutesToTime(180));
    }

    @Test
    @DisplayName("When formatMinutesToTime and minutes is in PM time then return formatted time in PM")
    void formatMinutesToTimePMTest(){
        assertEquals("3:00pm", formatMinutesToTime(900));
    }

    @Test
    @DisplayName("When formatMinutesToTime and minutes is midnight time then return formatted time in AM")
    void formatMinutesToTimeMidnightTest(){
        assertEquals("12:00am", formatMinutesToTime(0));
    }

}