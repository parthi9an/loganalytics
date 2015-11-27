package com.metron.util;

import java.util.HashSet;

/**
 * @author satheesh
 */

public class NodeUtil {

	public static HashSet<String> getUnwantedKeys() {
		HashSet<String> unwantedKeys = new HashSet<String>();
		unwantedKeys.add("@fieldTypes");
		unwantedKeys.add("@type");
		unwantedKeys.add("@rid");
		unwantedKeys.add("@version");
		unwantedKeys.add("out_likes");
		unwantedKeys.add("in_likes");
		unwantedKeys.add("out_comments");
		unwantedKeys.add("in_comments");
		return unwantedKeys;
	}

}
