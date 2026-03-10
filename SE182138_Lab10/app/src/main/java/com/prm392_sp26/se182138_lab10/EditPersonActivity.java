package com.prm392_sp26.se182138_lab10;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.prm392_sp26.se182138_lab10.constants.Constants;
import com.prm392_sp26.se182138_lab10.db.AppDatabase;
import com.prm392_sp26.se182138_lab10.model.Person;

public class EditPersonActivity extends AppCompatActivity {
    private EditText etFirstName;
    private EditText etLastName;
    private Button btnSave;
    private int mPersonId;
    private Intent intent;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        mDb = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").build();

        intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.UPDATE_PERSON_ID)) {
            btnSave.setText(R.string.update);
            mPersonId = intent.getIntExtra(Constants.UPDATE_PERSON_ID, -1);
            loadPerson(mPersonId);
        }
    }

    private void initViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> onSaveButtonClicked());
    }

    private void onSaveButtonClicked() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        Person person = new Person(firstName, lastName);

        AppExecutors.getInstance().diskIO().execute(() -> {
            if (intent != null && intent.hasExtra(Constants.UPDATE_PERSON_ID)) {
                person.setId(mPersonId);
                mDb.personDao().update(person);
            } else {
                mDb.personDao().insert(person);
            }
            AppExecutors.getInstance().mainThread().execute(this::finish);
        });
    }

    private void loadPerson(int personId) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            Person person = mDb.personDao().loadPersonById(personId);
            AppExecutors.getInstance().mainThread().execute(() -> populateUI(person));
        });
    }

    private void populateUI(Person person) {
        if (person == null) {
            return;
        }
        etFirstName.setText(person.getFirstName());
        etLastName.setText(person.getLastName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
