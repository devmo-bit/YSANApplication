package com.example.ysanapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ysanapplication.data.model.Event;
import com.example.ysanapplication.data.model.Registration;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ysan_db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_REGISTRATIONS = "registrations";

    // Common column names
    private static final String KEY_ID = "id";

    // EVENTS Table - column names
    private static final String KEY_EVENT_TITLE = "title";
    private static final String KEY_EVENT_CATEGORY = "category";
    private static final String KEY_EVENT_DATE = "date";
    private static final String KEY_EVENT_VENUE = "venue";
    private static final String KEY_EVENT_CAPACITY = "capacity";
    private static final String KEY_EVENT_STATUS = "status";

    // REGISTRATIONS Table - column names
    private static final String KEY_REG_EVENT_ID = "event_id";
    private static final String KEY_REG_PARTICIPANT_NAME = "participant_name";
    private static final String KEY_REG_CONTACT = "contact";
    private static final String KEY_REG_TIMESTAMP = "registration_timestamp";

    // Table Create Statements
    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_EVENT_TITLE + " TEXT,"
            + KEY_EVENT_CATEGORY + " TEXT,"
            + KEY_EVENT_DATE + " TEXT,"
            + KEY_EVENT_VENUE + " TEXT,"
            + KEY_EVENT_CAPACITY + " INTEGER,"
            + KEY_EVENT_STATUS + " TEXT" + ")";

    private static final String CREATE_TABLE_REGISTRATIONS = "CREATE TABLE " + TABLE_REGISTRATIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_REG_EVENT_ID + " INTEGER,"
            + KEY_REG_PARTICIPANT_NAME + " TEXT,"
            + KEY_REG_CONTACT + " TEXT,"
            + KEY_REG_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EVENTS);
        db.execSQL(CREATE_TABLE_REGISTRATIONS);
        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRATIONS);
        onCreate(db);
    }

    private void seedData(SQLiteDatabase db) {
        String[][] initialEvents = {
            {"Soccer Skills Clinic", "Soccer", "2023-11-15", "West Field", "30", "Active"},
            {"Athletics Summer Meet", "Athletics", "2023-12-05", "City Stadium", "100", "Active"},
            {"Basketball Workshop", "Basketball", "2023-11-20", "Indoor Court A", "20", "Full"},
            {"Netball Championship", "Netball", "2023-11-25", "Community Center", "40", "Active"},
            {"Fitness Bootcamp", "Fitness & Conditioning", "2023-12-01", "Central Park", "50", "Active"}
        };

        for (String[] event : initialEvents) {
            ContentValues values = new ContentValues();
            values.put(KEY_EVENT_TITLE, event[0]);
            values.put(KEY_EVENT_CATEGORY, event[1]);
            values.put(KEY_EVENT_DATE, event[2]);
            values.put(KEY_EVENT_VENUE, event[3]);
            values.put(KEY_EVENT_CAPACITY, Integer.parseInt(event[4]));
            values.put(KEY_EVENT_STATUS, event[5]);
            db.insert(TABLE_EVENTS, null, values);
        }
    }

    // --- Event Operations ---

    public long addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_TITLE, event.getTitle());
        values.put(KEY_EVENT_CATEGORY, event.getCategory());
        values.put(KEY_EVENT_DATE, event.getDate());
        values.put(KEY_EVENT_VENUE, event.getVenue());
        values.put(KEY_EVENT_CAPACITY, event.getCapacity());
        values.put(KEY_EVENT_STATUS, event.getStatus());
        return db.insert(TABLE_EVENTS, null, values);
    }

    public int updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_TITLE, event.getTitle());
        values.put(KEY_EVENT_CATEGORY, event.getCategory());
        values.put(KEY_EVENT_DATE, event.getDate());
        values.put(KEY_EVENT_VENUE, event.getVenue());
        values.put(KEY_EVENT_CAPACITY, event.getCapacity());
        values.put(KEY_EVENT_STATUS, event.getStatus());
        return db.update(TABLE_EVENTS, values, KEY_ID + " = ?", new String[]{String.valueOf(event.getId())});
    }

    public Event getEventById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT e.*, (SELECT COUNT(*) FROM " + TABLE_REGISTRATIONS + 
                             " r WHERE r." + KEY_REG_EVENT_ID + " = e." + KEY_ID + ") as reg_count FROM " + TABLE_EVENTS + 
                             " e WHERE e." + KEY_ID + " = ?";
        
        Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        if (c != null && c.moveToFirst()) {
            Event e = new Event();
            e.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
            e.setTitle(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_TITLE)));
            e.setCategory(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_CATEGORY)));
            e.setDate(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_DATE)));
            e.setVenue(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_VENUE)));
            e.setCapacity(c.getInt(c.getColumnIndexOrThrow(KEY_EVENT_CAPACITY)));
            e.setRegistrations(c.getInt(c.getColumnIndexOrThrow("reg_count")));
            e.setStatus(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_STATUS)));
            c.close();
            return e;
        }
        return null;
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String selectQuery = "SELECT e.*, (SELECT COUNT(*) FROM " + TABLE_REGISTRATIONS + 
                             " r WHERE r." + KEY_REG_EVENT_ID + " = e." + KEY_ID + ") as reg_count FROM " + TABLE_EVENTS + " e";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Event e = new Event();
                e.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                e.setTitle(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_TITLE)));
                e.setCategory(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_CATEGORY)));
                e.setDate(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_DATE)));
                e.setVenue(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_VENUE)));
                e.setCapacity(c.getInt(c.getColumnIndexOrThrow(KEY_EVENT_CAPACITY)));
                e.setRegistrations(c.getInt(c.getColumnIndexOrThrow("reg_count")));
                e.setStatus(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_STATUS)));
                events.add(e);
            } while (c.moveToNext());
        }
        c.close();
        return events;
    }

    // --- Registration Operations ---

    public boolean registerParticipant(int eventId, String name, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String countQuery = "SELECT COUNT(*) FROM " + TABLE_REGISTRATIONS + " WHERE " + KEY_REG_EVENT_ID + " = ?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{String.valueOf(eventId)});
            int currentRegs = 0;
            if (cursor.moveToFirst()) currentRegs = cursor.getInt(0);
            cursor.close();

            String capacityQuery = "SELECT " + KEY_EVENT_CAPACITY + " FROM " + TABLE_EVENTS + " WHERE " + KEY_ID + " = ?";
            cursor = db.rawQuery(capacityQuery, new String[]{String.valueOf(eventId)});
            int capacity = 0;
            if (cursor.moveToFirst()) capacity = cursor.getInt(0);
            cursor.close();

            if (currentRegs >= capacity) {
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(KEY_REG_EVENT_ID, eventId);
            values.put(KEY_REG_PARTICIPANT_NAME, name);
            values.put(KEY_REG_CONTACT, contact);
            db.insertOrThrow(TABLE_REGISTRATIONS, null, values);

            if (currentRegs + 1 >= capacity) {
                ContentValues statusValues = new ContentValues();
                statusValues.put(KEY_EVENT_STATUS, "Full");
                db.update(TABLE_EVENTS, statusValues, KEY_ID + " = ?", new String[]{String.valueOf(eventId)});
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public List<Registration> getAllRegistrations() {
        List<Registration> regs = new ArrayList<>();
        String selectQuery = "SELECT r.*, e." + KEY_EVENT_TITLE + " FROM " + TABLE_REGISTRATIONS + " r " +
                             "JOIN " + TABLE_EVENTS + " e ON r." + KEY_REG_EVENT_ID + " = e." + KEY_ID + " " +
                             "ORDER BY r." + KEY_REG_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Registration r = new Registration();
                r.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                r.setEventId(c.getInt(c.getColumnIndexOrThrow(KEY_REG_EVENT_ID)));
                r.setEventTitle(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_TITLE)));
                r.setParticipantName(c.getString(c.getColumnIndexOrThrow(KEY_REG_PARTICIPANT_NAME)));
                r.setContact(c.getString(c.getColumnIndexOrThrow(KEY_REG_CONTACT)));
                r.setTimestamp(c.getString(c.getColumnIndexOrThrow(KEY_REG_TIMESTAMP)));
                regs.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return regs;
    }

    public List<Registration> getRegistrationsByEventId(int eventId) {
        List<Registration> regs = new ArrayList<>();
        String selectQuery = "SELECT r.*, e." + KEY_EVENT_TITLE + " FROM " + TABLE_REGISTRATIONS + " r " +
                             "JOIN " + TABLE_EVENTS + " e ON r." + KEY_REG_EVENT_ID + " = e." + KEY_ID + " " +
                             "WHERE r." + KEY_REG_EVENT_ID + " = ? " +
                             "ORDER BY r." + KEY_REG_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(eventId)});

        if (c.moveToFirst()) {
            do {
                Registration r = new Registration();
                r.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                r.setEventId(c.getInt(c.getColumnIndexOrThrow(KEY_REG_EVENT_ID)));
                r.setEventTitle(c.getString(c.getColumnIndexOrThrow(KEY_EVENT_TITLE)));
                r.setParticipantName(c.getString(c.getColumnIndexOrThrow(KEY_REG_PARTICIPANT_NAME)));
                r.setContact(c.getString(c.getColumnIndexOrThrow(KEY_REG_CONTACT)));
                r.setTimestamp(c.getString(c.getColumnIndexOrThrow(KEY_REG_TIMESTAMP)));
                regs.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return regs;
    }
}