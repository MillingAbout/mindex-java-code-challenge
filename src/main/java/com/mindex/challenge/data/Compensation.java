package com.mindex.challenge.data;

import java.util.Date;

public class Compensation {
    String employee;
    int salary;
    Date effectiveDate;

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(final String employee) {
        this.employee = employee;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(final int salary) {
        this.salary = salary;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(final Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
