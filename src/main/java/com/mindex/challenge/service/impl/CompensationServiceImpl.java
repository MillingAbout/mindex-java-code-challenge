package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompensationServiceImpl implements CompensationService {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Compensation update(Compensation compensation) {
        Compensation existingCompensation = compensationRepository.findByEmployee(compensation.getEmployee());
        LOG.info("{} compensation for employee id [{}]",
                (existingCompensation != null) ? "Updating" : "Creating",
                compensation.getEmployee());

        Compensation updatedCompensation = compensationRepository.save(compensation);

        return updatedCompensation;
    }

    @Override
    public Compensation read(String id) {
        LOG.info("Read employee compensation for id [{}]", id);
        Compensation compensation = compensationRepository.findByEmployee(id);
        return compensation;
    }
}