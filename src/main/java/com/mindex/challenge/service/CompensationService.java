package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;


public interface CompensationService {
    Compensation update(Compensation compensation);
    Compensation read(String id);
}
