package com.prm392_sp26.se182138_lab10.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392_sp26.se182138_lab10.EditPersonActivity;
import com.prm392_sp26.se182138_lab10.R;
import com.prm392_sp26.se182138_lab10.constants.Constants;
import com.prm392_sp26.se182138_lab10.model.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.MyViewHolder> {
    private final Context context;
    private List<Person> mPersonList;

    public PersonAdapter(Context context, List<Person> personList) {
        this.context = context;
        this.mPersonList = personList != null ? personList : new ArrayList<>();
    }

    public void setPersons(List<Person> personList) {
        this.mPersonList = personList != null ? personList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<Person> getPersons() {
        return mPersonList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_person, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Person person = mPersonList.get(position);
        holder.firstName.setText(person.getFirstName());
        holder.lastName.setText(person.getLastName());

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditPersonActivity.class);
            intent.putExtra(Constants.UPDATE_PERSON_ID, person.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mPersonList != null ? mPersonList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView firstName;
        TextView lastName;
        Button editButton;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.tvFirstName);
            lastName = itemView.findViewById(R.id.tvLastName);
            editButton = itemView.findViewById(R.id.btnEdit);
        }
    }
}
