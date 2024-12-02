package web.util;

import java.util.Arrays;

public class CommonUtil {
	
	public static boolean contains(final int[] arr, final int key) {
	    return Arrays.stream(arr).anyMatch(i -> i == key);
	}
	
}
