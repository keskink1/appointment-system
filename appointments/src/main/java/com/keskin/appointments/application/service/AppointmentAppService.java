package com.keskin.appointments.application.service;

import com.keskin.appointments.application.dto.AppointmentDto;
import com.keskin.appointments.application.dto.CreateAppointmentRequestDto;
import com.keskin.appointments.application.mapper.AppointmentMapper;
import com.keskin.appointments.domain.model.Appointment;
import com.keskin.appointments.domain.repository.AppointmentRepository;
import com.keskin.appointments.domain.repository.UserShadowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentAppService {

    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;
    private final UserShadowRepository userShadowRepository;


    /*
    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentRequestDto requestDto){
        userShadowRepository.findById(requestDto) // wait for jwt

        Appointment appointment = Appointment.createAppointment(
                requestDto.time(),

        )
        appointmentRepository.save();
    }

     */
}
