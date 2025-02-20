package itstep.learning.rest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeResponse {
    private LocalDateTime time;

    private String message;

    public LocalDateTime getTime() {
        return time;
    }

    public TimeResponse setTime() {
        this.time = LocalDateTime.now();
        return this;
    }

    public long getTimestamp(){
        return time.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public String getIsoTime(){
        return time.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public String getMessage() {
        return message;
    }

    public TimeResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
