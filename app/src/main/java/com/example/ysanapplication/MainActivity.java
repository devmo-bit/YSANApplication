package com.example.ysanapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ysanapplication.data.DatabaseHelper;
import com.example.ysanapplication.data.model.Event;
import com.example.ysanapplication.ui.overview.EventAdapter;
import com.example.ysanapplication.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private DatabaseHelper dbHelper;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        setupMainRecyclerView();

        setSupportActionBar(binding.appBarMain.toolbar);
        
        // Coordinator Action: Create Event
        if (binding.appBarMain.fab != null) {
            binding.appBarMain.fab.setOnClickListener(view -> {
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_event_management);
            });
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // Responsive Navigation Setup
        NavigationView navigationView = binding.navView;
        if (navigationView != null) {
            // Tablet/Drawer Layout
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_event_overview, R.id.nav_event_management, R.id.nav_participant_registration, R.id.nav_settings)
                    .setOpenableLayout(binding.drawerLayout)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        // Check if BottomNav exists in current layout configuration
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        if (bottomNavigationView != null) {
            // Mobile/Portrait Layout
            if (mAppBarConfiguration == null) {
                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_event_overview, R.id.nav_event_management, R.id.nav_participant_registration)
                        .build();
                NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            }
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show overflow menu if not in drawer mode
        if (findViewById(R.id.nav_view) == null) {
            getMenuInflater().inflate(R.menu.overflow, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupMainRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview_main);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            List<Event> events = dbHelper.getAllEvents();
            // Convert List<Event> to List<Object> for the adapter which supports headers
            List<Object> items = new ArrayList<>(events);
            adapter = new EventAdapter(items, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onEventClick(Event event) {
        // Handle event click, e.g., navigate to registration
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putInt("event_id", event.getId());
        navController.navigate(R.id.nav_participant_registration, bundle);
    }

    @Override
    public void onEditClick(Event event) {
        // Handle edit click, e.g., navigate to management
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putInt("event_id", event.getId());
        navController.navigate(R.id.nav_event_management, bundle);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}