package com.example.ysanapplication.data.model;

public class Registration {
    private int id;
    private int eventId;
    private String eventTitle;
    private String participantName;
    private String contact;
    private String timestamp;

    public Registration() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}