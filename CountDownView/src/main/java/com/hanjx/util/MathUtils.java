package com.hanjx.util;

import com.blankj.utilcode.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public class MathUtils {
    public static int max(Integer... values) {
        return max(Arrays.asList(values));
    }

    public static int max(List<Integer> values) {
        if (CollectionUtils.isEmpty(values)) {
            return 0;
        }
        int max = values.get(0);
        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static int min(Integer... values) {
        return min(Arrays.asList(values));
    }

    public static int min(List<Integer> values) {
        int min = values.get(0);
        for (int value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }
}
