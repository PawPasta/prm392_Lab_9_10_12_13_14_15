package com.prm392_sp26.se182138_lab10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prm392_sp26.se182138_lab10.adapter.PersonAdapter;
import com.prm392_sp26.se182138_lab10.db.AppDatabase;
import com.prm392_sp26.se182138_lab10.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "lab10_prefs";
    private static final String PREFS_SEEDED = "seeded_persons";
    private FloatingActionButton fabAdd;
    private RecyclerView mRecyclerView;
    private PersonAdapter mAdapter;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, EditPersonActivity.class)));

        mRecyclerView = findViewById(R.id.rvPerson);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new PersonAdapter(this, new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);

        mDb = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").build();
        seedSampleDataIfNeeded();

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        int position = viewHolder.getBindingAdapterPosition();
                        List<Person> persons = mAdapter.getPersons();
                        if (position >= 0 && position < persons.size()) {
                            Person person = persons.get(position);
                            deletePerson(person);
                        }
                    }
                });
        helper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrievePersons();
    }

    private void retrievePersons() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<Person> persons = mDb.personDao().getAll();
            AppExecutors.getInstance().mainThread().execute(() -> mAdapter.setPersons(persons));
        });
    }

    private void seedSampleDataIfNeeded() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(PREFS_SEEDED, false)) {
            return;
        }
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<Person> persons = mDb.personDao().getAll();
            if (persons == null || persons.isEmpty()) {
                List<Person> samples = Arrays.asList(
                        new Person("An", "Nguyen"),
                        new Person("Binh", "Tran"),
                        new Person("Chi", "Le"),
                        new Person("Dung", "Pham")
                );
                for (Person person : samples) {
                    mDb.personDao().insert(person);
                }
                persons = mDb.personDao().getAll();
            }
            prefs.edit().putBoolean(PREFS_SEEDED, true).apply();
            List<Person> finalPersons = persons;
            AppExecutors.getInstance().mainThread().execute(() -> mAdapter.setPersons(finalPersons));
        });
    }

    private void deletePerson(Person person) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            mDb.personDao().delete(person);
            List<Person> persons = mDb.personDao().getAll();
            AppExecutors.getInstance().mainThread().execute(() -> mAdapter.setPersons(persons));
        });
    }
}
