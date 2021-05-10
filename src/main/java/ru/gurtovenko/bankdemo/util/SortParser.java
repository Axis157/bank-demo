package ru.gurtovenko.bankdemo.util;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortParser {

    private static final Pattern SORT_PATTERN_MATCHER = Pattern.compile(
            "^([0-9a-zA-Z._-]+)(;([Aa][Ss][Cc]|[Dd][Ee][Ss][Cc]))?$"
    );

    public static Sort parse(List<String> sorters) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sorters == null) {
            return Sort.by("id");
        }

        sorters.forEach(s -> {
            Matcher matcher = SORT_PATTERN_MATCHER.matcher(s);
            if (matcher.find()) {
                if (matcher.group(3) != null) {
                    Sort.Direction direction =
                            matcher.group(3).equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
                    orders.add(new Sort.Order(direction, matcher.group(1)));
                } else if (matcher.group(1) != null) {
                    // default sort order is ASC
                    orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    throw new IllegalArgumentException("Sort parse error: " + s);
                }
            } else {
                throw new IllegalArgumentException("Sort don't match:  " + s);
            }
        });


        return orders.isEmpty() ? null : Sort.by(orders);
    }
}
