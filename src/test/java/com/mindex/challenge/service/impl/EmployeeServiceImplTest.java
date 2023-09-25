package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);

        // Read a non-existant employee
        Employee employeeNotFound = restTemplate.getForEntity(employeeIdUrl, Employee.class, "12345").getBody();
        assertThat(employeeNotFound)
                .isNotNull()
                .extracting("employeeId", "firstName", "lastName", "position", "department", "directReports")
                .containsExactly(null, null, null, null, null, null);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);

        // Reporting Structure checks
        // Get employee with expected reports
        testEmployee.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        ReportingStructure employeeReportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployee.getEmployeeId()).getBody();
        assertThat(employeeReportingStructure.getEmployee().getEmployeeId()).isEqualTo(testEmployee.getEmployeeId());
        assertThat(employeeReportingStructure.getNumberOfReports()).isEqualTo(4);

        // Get employee with no expected reports
        testEmployee.setEmployeeId("62c1084e-6e34-4630-93fd-9153afb65309");
        ReportingStructure noExpectedReportingEmployees = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployee.getEmployeeId()).getBody();
        assertThat(noExpectedReportingEmployees.getEmployee().getEmployeeId()).isEqualTo(testEmployee.getEmployeeId());
        assertThat(noExpectedReportingEmployees.getNumberOfReports()).isEqualTo(0);

        // Get non-existant employee
        testEmployee.setEmployeeId("");
        ReportingStructure nonExistantEmployee = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployee.getEmployeeId()).getBody();
        assertThat(nonExistantEmployee).isNotNull();
        assertThat(nonExistantEmployee.getEmployee()).isNull();
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
