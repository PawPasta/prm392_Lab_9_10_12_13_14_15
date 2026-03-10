package com.prm392_sp26.se182138_lab10.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.prm392_sp26.se182138_lab10.model.Person;

import java.util.List;

@Dao
public interface PersonDao {
    @Query("SELECT * FROM person ORDER BY id DESC")
    List<Person> getAll();

    @Query("SELECT * FROM person WHERE id = :personId LIMIT 1")
    Person loadPersonById(int personId);

    @Insert
    void insert(Person person);

    @Update
    void update(Person person);

    @Delete
    void delete(Person person);
}
