package com.keskin.appointments.application.service;

import com.keskin.appointments.application.mapper.AppointmentMapper;
import com.keskin.appointments.domain.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

@Service
public class AppointmentAppService {

    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;

    public AppointmentAppService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
    }


}
