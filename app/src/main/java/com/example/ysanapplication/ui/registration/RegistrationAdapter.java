package com.example.ysanapplication.ui.registration;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ysanapplication.data.model.Registration;
import com.example.ysanapplication.databinding.ItemRegistrationBinding;

import java.util.List;

public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder> {

    private List<Registration> registrations;

    public RegistrationAdapter(List<Registration> registrations) {
        this.registrations = registrations;
    }

    @NonNull
    @Override
    public RegistrationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRegistrationBinding binding = ItemRegistrationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RegistrationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationViewHolder holder, int position) {
        Registration reg = registrations.get(position);
        holder.binding.textRegEventTitle.setText(reg.getEventTitle());
        holder.binding.textRegParticipantName.setText(reg.getParticipantName());
        holder.binding.textRegTimestamp.setText(reg.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return registrations != null ? registrations.size() : 0;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
        notifyDataSetChanged();
    }

    static class RegistrationViewHolder extends RecyclerView.ViewHolder {
        final ItemRegistrationBinding binding;

        RegistrationViewHolder(ItemRegistrationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}