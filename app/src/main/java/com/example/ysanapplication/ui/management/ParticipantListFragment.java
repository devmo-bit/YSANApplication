package com.example.ysanapplication.ui.management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ysanapplication.data.DatabaseHelper;
import com.example.ysanapplication.data.model.Registration;
import com.example.ysanapplication.databinding.FragmentEventParticipantsBinding;
import com.example.ysanapplication.ui.registration.RegistrationAdapter;

import java.util.ArrayList;
import java.util.List;

public class ParticipantListFragment extends Fragment {

    private FragmentEventParticipantsBinding binding;
    private DatabaseHelper dbHelper;
    private RegistrationAdapter adapter;
    private int eventId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventParticipantsBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        if (getArguments() != null) {
            eventId = getArguments().getInt("event_id", -1);
            String eventTitle = getArguments().getString("event_title", "Event");
            binding.textEventName.setText(eventTitle);
        }

        setupRecyclerView();
        loadParticipants();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerviewParticipants.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RegistrationAdapter(new ArrayList<>());
        binding.recyclerviewParticipants.setAdapter(adapter);
    }

    private void loadParticipants() {
        // Efficiently query registrations for this specific event from the DB
        List<Registration> eventRegs = dbHelper.getRegistrationsByEventId(eventId);
        adapter.setRegistrations(eventRegs);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}