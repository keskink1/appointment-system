package com.keskin.appointments.application.service;

import com.keskin.appointments.application.dto.AppointmentDto;
import com.keskin.appointments.application.dto.CreateAppointmentRequestDto;
import com.keskin.appointments.application.dto.UpdateAppointmentDto;
import com.keskin.appointments.application.mapper.AppointmentMapper;
import com.keskin.appointments.domain.model.Appointment;
import com.keskin.appointments.domain.repository.AppointmentRepository;
import com.keskin.appointments.domain.repository.UserShadowRepository;
import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.common.exception.ResourceAlreadyExistsException;
import com.keskin.common.exception.ResourceNotFoundException;
import com.keskin.common.exception.UnauthorizedException;
import com.keskin.common.util.UserContextHelper;
import jakarta.ws.rs.ForbiddenException;
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

    private void checkAppointmentOwnership(Appointment appointment) {
        String currentUserId = UserContextHelper.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedException("You are not logged in.");
        }

        UUID actorId = UUID.fromString(currentUserId);

        if (!appointment.getUser().getUserId().equals(actorId) && !UserContextHelper.isAdmin()) {
            throw new ForbiddenException("You don't have permission to access this appointment.");
        }
    }

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

        if (appointmentRepository.existsByTimeAndUserId(requestDto.time(), userId)) {
            throw new ResourceAlreadyExistsException("Appointment", "Time", requestDto.time());
        }

        Appointment appointment = Appointment.createAppointment(
                requestDto.time(),
                userShadow.getUserId(),
                userShadow.getName(),
                userShadow.getEmail(),
                userShadow.getEmail()
        );

        Appointment createdAppointment  = appointmentRepository.save(appointment);

        return appointmentMapper.toDto(createdAppointment);
    }

    @Transactional
    public AppointmentDto updateAppointment(UUID appointmentId, UpdateAppointmentDto requestDto) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "ID", appointmentId));

        checkAppointmentOwnership(appointment);

        if (!appointment.getAppointmentTime().time().equals(requestDto.time())) {
            UUID actorId = appointment.getUser().getUserId();

            if (appointmentRepository.existsByTimeAndUserId(requestDto.time(), actorId)) {
                throw new ResourceAlreadyExistsException("Appointment", "Time", requestDto.time());
            }

            String actorEmail = UserContextHelper.getCurrentUserEmail();
            appointment.rescheduleAppointment(requestDto.time(), actorEmail);

            appointmentRepository.save(appointment);
        }

        return appointmentMapper.toDto(appointment);
    }

    @Transactional
    public void deleteAppointment(UUID appointmentId){

    }
}
