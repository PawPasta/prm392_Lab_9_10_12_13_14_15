package com.prm392_sp26.se182138_lab10.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.prm392_sp26.se182138_lab10.dao.PersonDao;
import com.prm392_sp26.se182138_lab10.model.Person;

@Database(entities = {Person.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PersonDao personDao();
}
