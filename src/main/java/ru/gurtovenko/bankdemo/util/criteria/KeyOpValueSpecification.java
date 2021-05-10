package ru.gurtovenko.bankdemo.util.criteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Supported operations:
 * eq       - Equals
 * gt       - Greater
 * ge      - Greater or equal
 * lt       - Less
 * le      - Less or equal
 * in         - IN (...)
 * like       - String like function
 */
public class KeyOpValueSpecification<T> implements Specification<T> {
    private final static Logger logger = LoggerFactory.getLogger(KeyOpValueSpecification.class);

    public static final String IN = "in";
    public static final String OR = "or";
    public static final String LIKE = "like";
    public static final String NOT_LIKE = "not like";
    public static final String IS_NOT_NULL = "is not null";
    public static final String IS_NULL = "is null";
    public static final String STARTS_WITH = "starts with";
    public static final String ENDS_WITH = "ends with";


    public static final List<String> SUPPORTED_OPERATORS = Arrays.asList(
            "=",
            "!=",
            ">=",
            ">",
            "<=",
            "<",
            IN,
            OR,
            LIKE,
            NOT_LIKE,
            IS_NOT_NULL,
            IS_NULL,
            STARTS_WITH,
            ENDS_WITH
    );

    protected KeyOpValueCriteria criteria;


    public KeyOpValueSpecification(String expr) {
        this(new KeyOpValueCriteria(expr));
    }

    public KeyOpValueSpecification(String key, String operation, Object value) {
        this(new KeyOpValueCriteria(key, operation, value));
    }

    public KeyOpValueSpecification(KeyOpValueCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        From<T, T> from = root;

        String key = criteria.getKey();
        Object value = criteria.getValue();

        List<String> parts = new LinkedList<>(Arrays.asList(key.split("\\.")));

        while (parts.size() > 1) {
            String path = parts.remove(0);
            from = from.join(path, JoinType.LEFT);
        }

        assert (parts.size() == 1);
        key = parts.get(0);

        Class<?> keyClass = null;
        try {
            keyClass = from.get(key).getJavaType();
        } catch (IllegalArgumentException e) {
            logger.debug(e.getMessage());
        }

        if (keyClass != null) {
            switch (criteria.getOperation()) {
                case "=":
                case OR:
                    if (keyClass == Boolean.class || keyClass == boolean.class) {
                        return criteriaBuilder.equal(from.<Boolean>get(key), getBooleanValue(value));
                    } else if (keyClass == Date.class) {
                        String timestamp = (String) value;

                        return criteriaBuilder.between(from.<Date>get(key),
                                new Date((Long.parseLong(timestamp) - 43200000)),
                                new Date((Long.parseLong(timestamp) + 43200000)));
                    } else {
                        return criteriaBuilder.equal(from.get(key), value);
                    }

                case "!=":
                    if (keyClass == Boolean.class || keyClass == boolean.class) {
                        return criteriaBuilder.notEqual(from.<Boolean>get(key), getBooleanValue(value));
                    } else if (keyClass == Date.class) {
                        String timestamp = (String) value;

                        return criteriaBuilder.between(from.<Date>get(key),
                                new Date((Long.parseLong(timestamp) - 43200000)),
                                new Date((Long.parseLong(timestamp) + 43200000)));
                    } else {
                        return criteriaBuilder.notEqual(from.get(key), value);
                    }
                case ">=":
                    if (keyClass == Date.class) {
                        return criteriaBuilder.greaterThanOrEqualTo(from.get(key), new Date(Long.parseLong((String) value)));
                    } else {
                        return criteriaBuilder.greaterThanOrEqualTo(from.get(key), (String) value);
                    }

                case ">":
                    if (keyClass == Date.class) {
                        return criteriaBuilder.greaterThan(from.get(key), new Date(Long.parseLong((String) value)));
                    } else {
                        return criteriaBuilder.greaterThan(from.get(key), (String) value);
                    }

                case "<=":
                    if (keyClass == Date.class) {
                        return criteriaBuilder.lessThanOrEqualTo(from.get(key), new Date(Long.parseLong((String) value)));
                    } else {
                        return criteriaBuilder.lessThanOrEqualTo(from.get(key), (String) value);
                    }

                case "<":
                    if (keyClass == Date.class) {
                        return criteriaBuilder.lessThan(from.get(key), new Date(Long.parseLong((String) value)));
                    } else {
                        return criteriaBuilder.lessThan(from.get(key), (String) value);
                    }

                case IN:
                    List<Integer> inValues = Arrays.stream(((String) value).split(",")).map(
                            Integer::valueOf).collect(Collectors.toList());

                    criteriaQuery.distinct(true);
                    return criteriaBuilder.in(from.get(key)).value(inValues);

                case STARTS_WITH:
                    if (keyClass == String.class) {
                        return criteriaBuilder.like(criteriaBuilder.upper(from.get(key)), ((String) value).toUpperCase() + "%");
                    } else {
                        return null;
                    }

                case ENDS_WITH:
                    if (keyClass == String.class) {
                        return criteriaBuilder.like(criteriaBuilder.upper(from.get(key)), "%" + ((String) value).toUpperCase());
                    } else {
                        return null;
                    }

                case LIKE:
                    return criteriaBuilder.like(criteriaBuilder.upper(from.get(key)), ((String) value).toUpperCase());

                case NOT_LIKE:
                    return criteriaBuilder.notLike(criteriaBuilder.upper(from.get(key)), ((String) value).toUpperCase());

                case IS_NOT_NULL:
                    return criteriaBuilder.isNotNull(from.get(key));

                case IS_NULL:
                    return criteriaBuilder.isNull(from.get(key));

                default:
                    throw new IllegalArgumentException("Unknown operation: " + criteria.getOperation());
            }
        }

        return null;
    }

    private Boolean getBooleanValue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return Boolean.valueOf((String) value);
        }
    }

}
