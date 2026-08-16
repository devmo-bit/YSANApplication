package com.example.ysanapplication.ui.management;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ysanapplication.R;
import com.example.ysanapplication.data.DatabaseHelper;
import com.example.ysanapplication.data.model.Event;
import com.example.ysanapplication.databinding.FragmentEventManagementBinding;

import java.util.Calendar;

public class EventManagementFragment extends Fragment {

    private FragmentEventManagementBinding binding;
    private DatabaseHelper dbHelper;
    private int eventId = -1;
    private Event currentEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventManagementBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        setupSpinners();
        setupDatePicker();

        if (getArguments() != null) {
            eventId = getArguments().getInt("event_id", -1);
            if (eventId != -1) {
                loadEventData();
            }
        }

        binding.buttonSaveEvent.setOnClickListener(v -> saveEvent());
        
        binding.buttonViewParticipants.setOnClickListener(v -> {
            if (currentEvent != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("event_id", currentEvent.getId());
                bundle.putString("event_title", currentEvent.getTitle());
                Navigation.findNavController(v).navigate(R.id.action_nav_event_management_to_nav_participant_list, bundle);
            }
        });

        return binding.getRoot();
    }

    private void setupSpinners() {
        String[] categories = {
                getString(R.string.category_soccer),
                getString(R.string.category_athletics),
                getString(R.string.category_basketball),
                getString(R.string.category_netball),
                getString(R.string.category_fitness)
        };
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(categoryAdapter);

        String[] statuses = {
                getString(R.string.status_active),
                getString(R.string.status_full),
                getString(R.string.status_cancelled),
                getString(R.string.status_completed)
        };
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerStatus.setAdapter(statusAdapter);
    }

    private void setupDatePicker() {
        binding.editEventDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String date = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        binding.editEventDate.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void loadEventData() {
        currentEvent = dbHelper.getEventById(eventId);
        if (currentEvent != null) {
            binding.editEventTitle.setText(currentEvent.getTitle());
            binding.editEventDate.setText(currentEvent.getDate());
            binding.editEventVenue.setText(currentEvent.getVenue());
            binding.editEventCapacity.setText(String.valueOf(currentEvent.getCapacity()));
            
            // Set spinner selections
            setSpinnerSelection(binding.spinnerCategory, currentEvent.getCategory());
            setSpinnerSelection(binding.spinnerStatus, currentEvent.getStatus());
            
            binding.buttonSaveEvent.setText("Update Event");
            binding.buttonViewParticipants.setVisibility(View.VISIBLE);
        }
    }

    private void setSpinnerSelection(android.widget.Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void saveEvent() {
        String title = binding.editEventTitle.getText().toString().trim();
        String date = binding.editEventDate.getText().toString().trim();
        String venue = binding.editEventVenue.getText().toString().trim();
        String capacityStr = binding.editEventCapacity.getText().toString().trim();
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String status = binding.spinnerStatus.getSelectedItem().toString();

        if (title.isEmpty() || date.isEmpty() || venue.isEmpty() || capacityStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacity = Integer.parseInt(capacityStr);

        if (currentEvent == null) {
            currentEvent = new Event();
        }
        
        currentEvent.setTitle(title);
        currentEvent.setCategory(category);
        currentEvent.setDate(date);
        currentEvent.setVenue(venue);
        currentEvent.setCapacity(capacity);
        currentEvent.setStatus(status);

        if (eventId == -1) {
            long id = dbHelper.addEvent(currentEvent);
            if (id > 0) {
                Toast.makeText(getContext(), "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).navigateUp();
            } else {
                Toast.makeText(getContext(), "Error creating event", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rows = dbHelper.updateEvent(currentEvent);
            if (rows > 0) {
                Toast.makeText(getContext(), "Event Updated Successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).navigateUp();
            } else {
                Toast.makeText(getContext(), "Error updating event", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}