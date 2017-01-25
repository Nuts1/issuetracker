package com.oleksandr.controller.admin;

/**
 * Created by Nuts on 1/13/2017
 * 2:06 PM.
 */
public enum EmployeeRole {
    ROLE_MANAGER {
        @Override
        public String toString() {
            return "ROLE_MANAGER";
        }
    }, ROLE_ADMIN {
        @Override
        public String toString() {
            return "ROLE_ADMIN";
        }
    }, ROLE_EMPLOYEE {
        @Override
        public String toString() {
            return "ROLE_EMPLOYEE";
        }
    }, ROLE_CUSTOMER {
        @Override
        public String toString() {
            return "ROLE_CUSTOMER";
        }
    }
}
