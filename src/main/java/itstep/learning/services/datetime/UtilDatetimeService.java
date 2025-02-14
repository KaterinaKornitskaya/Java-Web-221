package itstep.learning.services.datetime;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class UtilDatetimeService implements DatetimeService{

    private String currDatetime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    @Override
    public String getCurrentDateTime() {
        return currDatetime;
    }
}
