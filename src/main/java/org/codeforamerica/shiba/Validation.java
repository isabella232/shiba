package org.codeforamerica.shiba;


import org.apache.commons.validator.GenericValidator;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public enum Validation {
    NONE(strings -> true),
    NOT_BLANK(strings -> !String.join("", strings).isBlank()),
    SELECT_AT_LEAST_ONE(strings -> strings.size() > 0),
    SSN(strings -> List.of(0, 9).contains(String.join("", strings).length())),
    DATE(strings ->
            String.join("", strings).isBlank() ||
            GenericValidator.isDate(String.join("/", strings), "MM/dd/yyyy", true));

    private final Predicate<List<String>> rule;

    Validation(Predicate<List<String>> rule) {
        this.rule = rule;
    }

    public Boolean apply(List<String> value) {
        return this.rule.test(value);
    }

}