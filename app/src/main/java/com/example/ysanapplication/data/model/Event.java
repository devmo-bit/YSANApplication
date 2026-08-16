package com.example.ysanapplication.data.model;

public class Event {
    private int id;
    private String title;
    private String category;
    private String date;
    private String venue;
    private int capacity;
    private int registrations;
    private String status;

    public Event() {}

    public Event(int id, String title, String category, String date, String venue, int capacity, int registrations, String status) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.date = date;
        this.venue = venue;
        this.capacity = capacity;
        this.registrations = registrations;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getRegistrations() { return registrations; }
    public void setRegistrations(int registrations) { this.registrations = registrations; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}