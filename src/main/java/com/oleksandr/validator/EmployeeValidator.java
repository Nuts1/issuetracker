package com.oleksandr.validator;

import com.oleksandr.dto.EmployeeDto;
import com.oleksandr.service.entity.EmployeeService;
import com.oleksandr.validator.constant.EmployeeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.regex.Pattern;


/**
 * Created by nuts on 25.01.17.
 */
@Component
public class EmployeeValidator {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeValidator(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void validateUpdate(EmployeeDto employeeDto, BindingResult bindingResult) {
        validateEmail(employeeDto, bindingResult);
        isEmailExist(employeeDto, bindingResult);

        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, EmployeeConstant.NAME, "employee.nameNull");
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, EmployeeConstant.SURNAME, "employee.surnameNull");
        ValidationUtils.rejectIfEmpty(bindingResult, EmployeeConstant.ID, "employee.nameNull");
    }

    public void validateSave(EmployeeDto employeeDto, BindingResult bindingResult) {
        validateEmail(employeeDto, bindingResult);
        isEmailExist(employeeDto, bindingResult);

        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, EmployeeConstant.NAME, "employee.nameNull");
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, EmployeeConstant.SURNAME, "employee.surnameNull");
    }

    private void validateEmail(EmployeeDto employee, Errors errors) {
        if (!Pattern.compile(EmployeeConstant.EMAIL_REGEX).matcher(employee.getEmail()).matches()) {
            errors.rejectValue(EmployeeConstant.EMAIL, "employee.emailNotValid");
        }
    }

    private void isEmailExist(EmployeeDto employee, Errors errors) {
        if (employeeService.getEmployeeByEmail(employee.getEmail()) != null) {
            errors.rejectValue(EmployeeConstant.EMAIL,"employee.emailAlreadyUse");
        }
    }
}
