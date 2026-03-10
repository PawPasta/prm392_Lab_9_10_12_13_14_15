package com.prm392_sp26.se182138_lab12;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class NotificationFragment extends Fragment {

    public NotificationFragment() {
        super(R.layout.fragment_notifications);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = view.findViewById(R.id.text_notifications);
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getCounter().observe(getViewLifecycleOwner(), value -> {
            int displayValue = value == null ? 0 : value;
            textView.setText(getString(R.string.notifications_counter_value, displayValue));
        });
    }
}
