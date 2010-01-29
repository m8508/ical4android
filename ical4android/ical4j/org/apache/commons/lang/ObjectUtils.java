package org.apache.commons.lang;

public class ObjectUtils {

	public static boolean equals(final Object object1, final Object object2) {
		if (object1 == object2) {
			return true;
		}
		if ((object1 == null) || (object2 == null)) {
			return false;
		}
		return object1.equals(object2);
	}
}
