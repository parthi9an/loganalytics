package com.metron.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author satheesh
 */

public class RegExpMatcher {

    public static Matcher findMatches(String text, String pattern) {
        // String to be scanned to find the pattern.
        // String line = "This order was placed for QT3000! OK?";
        // String pattern = "(.*)(\\d+)(.*)";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(text);
        if (m.find()) {
            System.out.println("Found value: " + m.group(0));
            System.out.println("Found value: " + m.group(1));
        } else {
            System.out.println("NO MATCH");
        }
        return m;
    }

    public static void main(String[] args) {
        System.out.println(RegExpMatcher.findMatches(
                "/lib/util/System.IllegalArgumentException: The path \"XXX\" is not correctly formatted. at AnonymousProcedure (line 3) at /lib/resource/ClearResourceCache",
                ".*System\\.(.*)Exception"));
        // System.out.println("User User has READ privileges for that resource. [repository-1900362]".replaceAll("\\[.*\\]",
        // "\\[XXX\\]"));
    }

}
