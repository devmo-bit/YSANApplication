package com.example.ysanapplication.ui.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ysanapplication.R;
import com.example.ysanapplication.data.DatabaseHelper;
import com.example.ysanapplication.data.model.Event;
import com.example.ysanapplication.data.model.Registration;
import com.example.ysanapplication.databinding.FragmentParticipantRegistrationBinding;

import java.util.ArrayList;
import java.util.List;

public class ParticipantRegistrationFragment extends Fragment {

    private FragmentParticipantRegistrationBinding binding;
    private DatabaseHelper dbHelper;
    private List<Event> eventList;
    private RegistrationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentParticipantRegistrationBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireContext());
        
        setupEventSpinner();
        setupRecyclerView();
        loadRegistrations();

        binding.buttonRegister.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.button_click);
            v.startAnimation(animation);
            registerParticipant();
        });

        binding.spinnerSelectEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateEventDetails(eventList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.cardEventDetails.setVisibility(View.GONE);
            }
        });

        if (getArguments() != null) {
            int selectedEventId = getArguments().getInt("event_id", -1);
            if (selectedEventId != -1) {
                for (int i = 0; i < eventList.size(); i++) {
                    if (eventList.get(i).getId() == selectedEventId) {
                        binding.spinnerSelectEvent.setSelection(i);
                        break;
                    }
                }
            }
        }

        return binding.getRoot();
    }

    private void setupEventSpinner() {
        eventList = dbHelper.getAllEvents();
        List<String> eventTitles = new ArrayList<>();
        for (Event e : eventList) {
            eventTitles.add(e.getTitle());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, eventTitles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSelectEvent.setAdapter(spinnerAdapter);
    }

    private void updateEventDetails(Event event) {
        binding.cardEventDetails.setVisibility(View.VISIBLE);
        
        // Using string resources with placeholders for better practices
        binding.textDetailDate.setText(getString(R.string.label_date_value, event.getDate()));
        binding.textDetailVenue.setText(getString(R.string.label_venue_value, event.getVenue()));
        
        int remaining = event.getCapacity() - event.getRegistrations();
        binding.textDetailCapacity.setText(getString(R.string.label_availability_value, remaining, event.getStatus()));
        
        if (event.getRegistrations() >= event.getCapacity() || "Full".equalsIgnoreCase(event.getStatus())) {
            binding.buttonRegister.setEnabled(false);
            binding.buttonRegister.setText(R.string.button_event_full);
        } else {
            binding.buttonRegister.setEnabled(true);
            binding.buttonRegister.setText(R.string.button_complete_registration);
        }
    }

    private void setupRecyclerView() {
        binding.recyclerviewPreviousRegs.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RegistrationAdapter(new ArrayList<>());
        binding.recyclerviewPreviousRegs.setAdapter(adapter);
    }

    private void loadRegistrations() {
        List<Registration> registrations = dbHelper.getAllRegistrations();
        adapter.setRegistrations(registrations);
    }

    private void registerParticipant() {
        if (eventList.isEmpty()) return;

        int selectedPos = binding.spinnerSelectEvent.getSelectedItemPosition();
        Event selectedEvent = eventList.get(selectedPos);
        
        String name = binding.editParticipantName.getText().toString().trim();
        String contact = binding.editContactInfo.getText().toString().trim();

        if (name.isEmpty() || contact.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.registerParticipant(selectedEvent.getId(), name, contact);
        
        if (success) {
            Toast.makeText(getContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();
            binding.editParticipantName.setText("");
            binding.editContactInfo.setText("");
            
            eventList = dbHelper.getAllEvents(); 
            updateEventDetails(eventList.get(selectedPos));
            loadRegistrations();
        } else {
            Toast.makeText(getContext(), "Registration Failed. Event might be full.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}