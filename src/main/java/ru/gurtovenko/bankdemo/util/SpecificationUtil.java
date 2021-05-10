package ru.gurtovenko.bankdemo.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.gurtovenko.jwt.dto.payload.AccountInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SpecificationUtil {

    public static <T> Specification<T> createSpecification(List<String> filters,
                                                           Class<T> entityClass,
                                                           Authentication authentication) {
        AccountInfo adminInfo = (AccountInfo) authentication.getPrincipal();

        Specification<T> result = null;

        if (filters != null) {
            List<KeyOpValueCriteria> criteriaList = createCriteriaForOperatorIn(filters);

            for (KeyOpValueCriteria criteria : criteriaList) {
                Specification<T> newSpec = null;

                String criteriaKey = criteria.getKey();
                Object criteriaValue = criteria.getValue();

                newSpec = createSpecification(criteriaKey, criteria.getOperation(), (String) criteriaValue);
                result = addSpecification(criteria.getOperation(), newSpec, result);
            }
        }

        return result;
    }

    /**
     * Create criteria for IN operator by using filters
     */
    private static List<KeyOpValueCriteria> createCriteriaForOperatorIn(List<String> filters) {
        List<KeyOpValueCriteria> criteriaList = new ArrayList<>();

        for (String filter : filters) {
            KeyOpValueCriteria newCriteria = new KeyOpValueCriteria(filter);

            KeyOpValueCriteria inOperatorCriteria = criteriaList.stream().filter(keyOpValueCriteria ->
                    keyOpValueCriteria.getKey().equals(newCriteria.getKey())
                            && keyOpValueCriteria.getOperation().equals("in"))
                    .findFirst().orElse(null);

            if (inOperatorCriteria != null) {
                Object newValue = inOperatorCriteria.getValue() + "," + newCriteria.getValue();
                inOperatorCriteria.setValue(newValue);
            } else {
                criteriaList.add(newCriteria);
            }
        }

        return criteriaList;
    }

    private static <T> Specification<T> addSpecification(String operation,
                                                         Specification<T> newSpec,
                                                         Specification<T> result) {
        if (newSpec != null) {
            return result == null ? newSpec :
                    operation.equals("or") ? Specification.where(result).or(newSpec) :
                            Specification.where(result).and(newSpec);
        }

        return result;
    }

    /**
     * Convert quick filter value
     */
    public static <T> String convertValueAsLikeComparison(String value) {
        return "%" + value + "%";
    }

    /**
     * Create quick filter specification
     */
    public static <T> Specification<T> createQuickFilterSpecification(String quickFilterValue, Class<T> entityClass) {
        List<String> quickFilterSearchFields = AnnotationUtils.getQuickFilterSearchFields(entityClass);

        Specification<T> result = null;

        for (String quickFilterSearchField : quickFilterSearchFields) {
            Specification<T> newSpec = createSpecification(quickFilterSearchField, "like", quickFilterValue);
            result = result == null ? newSpec : Specification.where(result).or(newSpec);
        }

        return result;
    }

    /**
     * Create provider filter specification
     */
    public static <T> Specification<T> createProviderFilterSpecification(Class<T> entityClass,
                                                                         ProviderInfo providerInfo,
                                                                         List<Role> roles) {

        Specification<T> specification = null;

        List<Field> fields = ReflectionUtils.getDeclaredFields(entityClass);

        String adminProviderId = String.valueOf(providerInfo.getId());

        for (Field field : fields) {
            String fieldName = AnnotationUtils.getFieldNameForFilterSpecific(field, roles);

            if (fieldName != null) {
                specification = createSpecification(fieldName, "=", adminProviderId);
            }
        }

        return specification;
    }

    /**
     * Create specification based on filter.
     * Can be overwritten to implement custom logic.
     */
    public static <T> Specification<T> createSpecification(String field, String operator, String value) {
        String actualValue = value;

        if (operator.equals("like")) {
            actualValue = convertValueAsLikeComparison(value);
        }

        KeyOpValueCriteria criteria = new KeyOpValueCriteria(field, operator, actualValue);
        return new KeyOpValueSpecification<>(criteria);
    }

}
