package com.oleksandr.validator.constant;

import com.oleksandr.entity.Employee;
import org.springframework.validation.Errors;

import java.util.regex.Pattern;

/**
 * Created by nuts on 25.01.17.
 */
public class EmployeeConstant {
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String ID = "Id";
}
