package com.keskin.appointments.application.service;

import com.keskin.appointments.application.dto.AppointmentDto;
import com.keskin.appointments.application.dto.CreateAppointmentRequestDto;
import com.keskin.appointments.application.mapper.AppointmentMapper;
import com.keskin.appointments.domain.model.Appointment;
import com.keskin.appointments.domain.repository.AppointmentRepository;
import com.keskin.appointments.domain.repository.UserShadowRepository;
import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.common.exception.ResourceNotFoundException;
import com.keskin.common.exception.UnauthorizedException;
import com.keskin.common.util.UserContextHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentAppService {

    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;
    private final UserShadowRepository userShadowRepository;


    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentRequestDto requestDto) {
        String currentUserId = UserContextHelper.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedException("You are not logged in");
        }
        UUID userId = UUID.fromString(currentUserId);

        UserShadow userShadow = userShadowRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User shadow", "UUID", userId.toString())
        );

        Appointment appointment = Appointment.createAppointment(
                requestDto.time(),
                userShadow.getEmail(),
                userShadow.getUserId(),
                userShadow.getName(),
                userShadow.getEmail()
        );

        Appointment createdAppointment  = appointmentRepository.save(appointment);

        return appointmentMapper.toDto(createdAppointment);
    }


}
