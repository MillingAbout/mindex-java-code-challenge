package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
//TODO  This test class was implemented in line with observed current practice.
//      Things to consider:
//      1.These integration tests are actually run against the controlller and should be executed by a separate integration testrunner.
//      2. The Controller and Services classes should have unit test classes.
//      3. Multiple tests were added to a single test method in line with observed current practice.
//         Best practice is to place each test in it's own test method so that each test can be executed independently.
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {
    private String employeeUrl;
    private String compensationUrl;
    private String compensationIdUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void createReadCompensation() {
        // An employee must exist to add/update a compensation record
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("Jane");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Manager");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        Date effectiveDate = new Date();
        Compensation janeDoeCompensation = new Compensation();
        janeDoeCompensation.setEmployee(createdEmployee.getEmployeeId());
        janeDoeCompensation.setSalary(10000000);
        janeDoeCompensation.setEffectiveDate(effectiveDate);

        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, janeDoeCompensation, Compensation.class).getBody();
        assertThat(createdCompensation).isNotNull();
        assertThat(createdCompensation)
                .extracting("employee", "salary", "effectiveDate")
                .containsExactly(createdEmployee.getEmployeeId(), 10000000, effectiveDate);

        // Read checks
        // Read newly added compensation record
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, janeDoeCompensation.getEmployee()).getBody();
        assertThat(readCompensation).isNotNull();
        assertThat(readCompensation)
                .extracting("employee", "salary", "effectiveDate")
                .containsExactly(createdEmployee.getEmployeeId(), 10000000, effectiveDate);

        // Read non-existant compensation record
        janeDoeCompensation.setEmployee(null);
        Compensation readNonExistantCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, janeDoeCompensation.getEmployee()).getBody();
        assertThat(readNonExistantCompensation).isNotNull();
        assertThat(readNonExistantCompensation)
                .extracting("employee", "salary", "effectiveDate")
                .containsExactly(null, 0, null);

        // Read already exsiting compensation record
        String johnLennonId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
        Date johnLennonEffectiveDate = Date.from(Instant.parse("1966-01-02T00:00:00.000Z"));
        Compensation readJohnLennonCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, johnLennonId).getBody();
        assertThat(readJohnLennonCompensation).isNotNull();
        assertThat(readJohnLennonCompensation)
                .extracting("employee", "salary", "effectiveDate")
                .containsExactly(johnLennonId, 1000, johnLennonEffectiveDate);
    }
}