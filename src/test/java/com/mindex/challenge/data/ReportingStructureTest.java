package com.mindex.challenge.data;

import java.util.Collections;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ReportingStructureTest {
    @Test
    public void createReportingStructureWithDefaultConstructor() {
        final Employee employee = new Employee();
        final ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(0);

        assertThat(reportingStructure).isNotNull();
        assertThat(reportingStructure.getEmployee()).isEqualTo(employee);
        assertThat(reportingStructure.getNumberOfReports()).isEqualTo(0);
        assertThat(reportingStructure.toString()).matches("^.*Employee@[\\w]+, numberOfReports=0}");
    }

    @Test
    public void createReportingStructureWithNoDirectReports() {
        final Employee employee = new Employee();
        final ReportingStructure reportingStructure = new ReportingStructure(employee, 0);

        assertThat(reportingStructure).isNotNull();
        assertThat(reportingStructure.getEmployee()).isEqualTo(employee);
        assertThat(reportingStructure.getNumberOfReports()).isEqualTo(0);
    }

    @Test
    public void createReportingStructureWith1DirectReoprt() {
        final Employee employee1 = new Employee();
        final Employee employee2 = new Employee();
        employee1.setDirectReports(Collections.singletonList(employee2));

        assertThat(new ReportingStructure(employee1, 1).getNumberOfReports()).isEqualTo(1);
    }
}