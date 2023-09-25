package com.mindex.challenge.data;

import java.util.Objects;

public class ReportingStructure {
    Employee employee;
    int numberOfReports;

    public ReportingStructure () {
    }

    public ReportingStructure (final Employee employee, final int numberOfReports) {
        Objects.requireNonNull(employee, "Employee must not be null");
        this.employee = employee;
        this.numberOfReports = numberOfReports;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(final Employee employee) {
        this.employee = employee;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public void setNumberOfReports(final int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }

    @Override
    public String toString() {
        return "ReportingStructure{" +
                "employee=" + employee +
                ", numberOfReports=" + numberOfReports +
                '}';
    }
}
