package com.example.ysanapplication.ui.overview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ysanapplication.R;
import com.example.ysanapplication.data.model.Event;
import com.example.ysanapplication.databinding.ItemEventBinding;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_EVENT = 1;

    private List<Object> items = new ArrayList<>();
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
        void onEditClick(Event event);
    }

    public EventAdapter(List<Object> items, OnEventClickListener listener) {
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        return TYPE_EVENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder((TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category_header, parent, false));
        } else {
            ItemEventBinding binding = ItemEventBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new EventViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).textView.setText((String) items.get(position));
        } else if (holder instanceof EventViewHolder) {
            Event event = (Event) items.get(position);
            EventViewHolder eventHolder = (EventViewHolder) holder;
            eventHolder.binding.textEventTitle.setText(event.getTitle());
            eventHolder.binding.textEventCategory.setText(event.getCategory());
            eventHolder.binding.textEventDate.setText(event.getDate());
            eventHolder.binding.textEventVenue.setText(event.getVenue());
            
            eventHolder.binding.textEventRegistrations.setText(
                    eventHolder.itemView.getContext().getString(R.string.label_registrations_count, 
                    event.getRegistrations(), event.getCapacity()));

            eventHolder.binding.chipStatus.setText(event.getStatus());
            
            eventHolder.itemView.setOnClickListener(v -> listener.onEventClick(event));
            
            eventHolder.binding.buttonEditEvent.setOnClickListener(v -> listener.onEditClick(event));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        final ItemEventBinding binding;

        EventViewHolder(ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

        HeaderViewHolder(TextView textView) {
            super(textView);
            this.textView = textView;
        }
    }
}