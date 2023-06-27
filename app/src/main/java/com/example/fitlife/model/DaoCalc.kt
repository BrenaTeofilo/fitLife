package com.example.fitlife.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoCalc {

    @Insert
    fun insert(calc: Calc)

    @Query("SELECT * FROM Calc WHERE type = :type")
    fun getRegisterByType(type: String) : List<Calc>
}