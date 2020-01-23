package com.mirvahidagha.betterbet.Dao;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static CharSequence fromString(String value) {
        return value == null ? null : (CharSequence) value;
    }

    @TypeConverter
    public static String toString(CharSequence date) {
        return date == null ? null : date.toString();
    }
}
