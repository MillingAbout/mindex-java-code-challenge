package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);
        Employee employee;
        try {
            employee = employeeRepository.findByEmployeeId(id);
        } catch (Exception e) {
            employee = null;
            LOG.debug("Caught exception reading employee with id [{}]", id, e);
        }

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String id) {
        LOG.info("Reading employee reporting structure with id [{}]", id);
        Employee employee = employeeRepository.findByEmployeeId(id);

        //TODO  Investigate if Mongodb or a framework has a better means of traversing a tree of related records
        ReportingStructure reportingStructure = deriveNumberOfReports(employee);
        return reportingStructure;
    }

    // Recursively traverse the tree of employee.directReports populating the dependencies to
    // create a "fully filled out ReportingStructure"
    private ReportingStructure deriveNumberOfReports(final Employee employee) {
        // base case
        if (employee == null ||
                employee.getDirectReports() == null ||
                employee.getDirectReports().size() == 0) {
            return new ReportingStructure (employee, 0);
        }

        // continue recursing
        final List<Employee> directReports = new ArrayList<>();
        int count = employee.getDirectReports().size();
        for (Employee directReport : employee.getDirectReports()) {
            Employee derivedEmployee = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
            ReportingStructure derivedReportingStructure = deriveNumberOfReports (derivedEmployee);
            directReports.add(derivedReportingStructure.getEmployee());
            count += derivedReportingStructure.getNumberOfReports();
        }
        employee.setDirectReports(directReports);
        return new ReportingStructure(employee, count);
    }
}
