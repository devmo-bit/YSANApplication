package com.example.ysanapplication.ui.overview;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ysanapplication.R;
import com.example.ysanapplication.data.DatabaseHelper;
import com.example.ysanapplication.data.model.Event;
import com.example.ysanapplication.databinding.FragmentEventOverviewBinding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventOverviewFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private FragmentEventOverviewBinding binding;
    private DatabaseHelper dbHelper;
    private EventAdapter adapter;
    private List<Event> allEvents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventOverviewBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireContext());
        
        setupRecyclerView();
        setupFilterChips();
        loadEventsWithAnimation();
        
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        final int spanCount = getResources().getConfiguration().screenWidthDp >= 900 ? 3 : 2;
        
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == EventAdapter.TYPE_HEADER ? spanCount : 1;
            }
        });
        
        binding.recyclerviewEvents.setLayoutManager(layoutManager);
        adapter = new EventAdapter(new ArrayList<>(), this);
        binding.recyclerviewEvents.setAdapter(adapter);
    }

    private void setupFilterChips() {
        binding.chipGroupCategories.setOnCheckedChangeListener((group, checkedId) -> {
            if (allEvents == null) return;
            
            String category = "";
            if (checkedId == R.id.chip_soccer) category = getString(R.string.category_soccer);
            else if (checkedId == R.id.chip_athletics) category = getString(R.string.category_athletics);
            else if (checkedId == R.id.chip_basketball) category = getString(R.string.category_basketball);
            else if (checkedId == R.id.chip_netball) category = getString(R.string.category_netball);
            else if (checkedId == R.id.chip_fitness) category = getString(R.string.category_fitness);

            filterAndGroupEvents(category);
        });
    }

    private void loadEventsWithAnimation() {
        binding.imageLoading.setVisibility(View.VISIBLE);
        binding.imageLoading.setBackgroundResource(R.drawable.loading_animation);
        AnimationDrawable frameAnimation = (AnimationDrawable) binding.imageLoading.getBackground();
        frameAnimation.start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            allEvents = dbHelper.getAllEvents();
            filterAndGroupEvents(""); 
            
            if (frameAnimation.isRunning()) frameAnimation.stop();
            binding.imageLoading.setVisibility(View.GONE);
        }, 800);
    }

    private void filterAndGroupEvents(String filterCategory) {
        List<Object> groupedItems = new ArrayList<>();
        Map<String, List<Event>> groupedMap = new LinkedHashMap<>();
        
        String[] categories = {
            getString(R.string.category_soccer),
            getString(R.string.category_athletics),
            getString(R.string.category_basketball),
            getString(R.string.category_netball),
            getString(R.string.category_fitness)
        };

        for (String cat : categories) {
            if (filterCategory.isEmpty() || cat.equalsIgnoreCase(filterCategory)) {
                groupedMap.put(cat, new ArrayList<>());
            }
        }

        for (Event e : allEvents) {
            if (groupedMap.containsKey(e.getCategory())) {
                groupedMap.get(e.getCategory()).add(e);
            }
        }

        for (Map.Entry<String, List<Event>> entry : groupedMap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                groupedItems.add(entry.getKey()); 
                groupedItems.addAll(entry.getValue()); 
            }
        }
        
        adapter.setItems(groupedItems);
    }

    @Override
    public void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putInt("event_id", event.getId());
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_event_overview_to_nav_participant_registration, bundle);
    }

    @Override
    public void onEditClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putInt("event_id", event.getId());
        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_event_management, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}