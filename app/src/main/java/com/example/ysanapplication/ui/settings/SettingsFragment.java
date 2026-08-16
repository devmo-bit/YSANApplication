package com.example.ysanapplication.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ysanapplication.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.switchStaffMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String message = isChecked ? "Staff Mode Enabled" : "Athlete Mode Enabled";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            // In a real app, this would persist in SharedPreferences
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}