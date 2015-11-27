package com.metron.util;


/**
 * @author satheesh
 */

public class TimeWindowUtil {

    public static enum DURATION {
        ONEMIN(1), FIVEMIN(5), ONEHOUR(60), ONEDAY(60 * 24);
        int min = 5;
        private DURATION(int min) {
            this.min = min;
        }

        public String getTable() {
            return "TimeWindow" + (min);
        }

        public int getMinutes() {
            return min;
        }
    }
}
