package org.codeforamerica.shiba.pages.config;


import org.apache.commons.validator.GenericValidator;
import org.codeforamerica.shiba.pages.emails.MailGunEmailClient;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

@SuppressWarnings("unused")
public enum Validation {
    NONE((strings, _previousInputs) -> true),
    NOT_BLANK((strings, _previousInputs) -> !String.join("", strings).isBlank()),
    SELECT_AT_LEAST_ONE((strings, _previousInputs) -> strings.size() > 0),
    SSN((strings, _previousInputs) -> String.join("", strings).replace("-", "").matches("\\d{9}")),
    DATE((strings, _previousInputs) -> {
        return GenericValidator.isDate(String.join("/", strings), "MM/dd/yyyy", true)
                || GenericValidator.isDate(String.join("/", strings), "M/dd/yyyy", true)
                || GenericValidator.isDate(String.join("/", strings), "M/d/yyyy", true)
                || GenericValidator.isDate(String.join("/", strings), "MM/d/yyyy", true);
    }),
    ZIPCODE((strings, _previousInputs) -> String.join("", strings).matches("\\d{5}")),
    STATE((strings, _previousInputs) -> Set.of("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "AS", "DC", "FM", "GU", "MH", "MP", "PR", "VI", "AB", "BC", "MB", "NB", "NF", "NS", "ON", "PE", "PQ", "SK")
            .contains(strings.get(0).toUpperCase())),
    PHONE((strings, _previousInputs) -> String.join("", strings).replaceAll("[^\\d]", "").matches("[2-9]\\d{9}")),
    PHONE_STARTS_WITH_ONE((strings, _previousInputs) -> !String.join("", strings).replaceAll("[^\\d]", "").startsWith("1")),
    MONEY((strings, _previousInputs) -> String.join("", strings).matches("\\d+(\\.\\d{1,2})?")),
    EMAIL((strings, previousInputs) -> {
        return MailGunEmailClient.validateEmailAddress(String.join("", strings), String.join("", previousInputs));
    });

    private final BiPredicate<List<String>, List<String>> rule;

    Validation(BiPredicate<List<String>, List<String>> rule) {
        this.rule = rule;
    }

    public Boolean apply(List<String> value, List<String> previousValue) {
        return this.rule.test(value, previousValue);
    }

}
