package com.metron.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author satheesh
 */

public class Utils {

    public static Date parseEventDate(String dateInString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = null;
        try {
            date = sdf.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    private static long getDateDiff(Date from, Date to, TimeUnit timeUnit) {
        long diffInMillies = to.getTime() - from.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
    
    public static long getDateDiffInMIllisec(Date from, Date to) {
        return getDateDiff(from, to, TimeUnit.MILLISECONDS);
    }

}
