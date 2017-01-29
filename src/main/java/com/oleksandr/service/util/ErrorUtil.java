package com.oleksandr.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nuts on 29.01.17.
 */

@Component
public class ErrorUtil {
    private final MessageSource messageSource;

    @Autowired
    public ErrorUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public List<String> getErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors()
                .stream()
                .map(e -> messageSource.getMessage(e, LocaleContextHolder.getLocale()))
                .collect(Collectors.toList());
    }
}
