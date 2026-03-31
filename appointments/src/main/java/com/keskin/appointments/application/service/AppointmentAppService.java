package com.keskin.appointments.application.service;

import com.keskin.appointments.application.dto.AppointmentDto;
import com.keskin.appointments.application.dto.CreateAppointmentRequestDto;
import com.keskin.appointments.application.dto.UpdateAppointmentDto;
import com.keskin.appointments.application.mapper.AppointmentMapper;
import com.keskin.appointments.domain.model.Appointment;
import com.keskin.appointments.domain.repository.AppointmentRepository;
import com.keskin.appointments.domain.repository.UserShadowRepository;
import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.common.exception.AuthenticationException;
import com.keskin.common.exception.ResourceAlreadyExistsException;
import com.keskin.common.exception.ResourceNotFoundException;
import com.keskin.common.exception.ForbiddenException;
import com.keskin.common.util.UserContextHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentAppService {

    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;
    private final UserShadowRepository userShadowRepository;

    private void checkAppointmentOwnership(Appointment appointment) {
        String currentUserId = UserContextHelper.getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthenticationException("You are not logged in.");
        }

        UUID actorId = UUID.fromString(currentUserId);

        if (!appointment.getUser().getUserId().equals(actorId) && !UserContextHelper.isAdmin()) {
            throw new ForbiddenException("You don't have permission to access this appointment.");
        }
    }

    // Secondary guard, in case this method is called outside the controller context
    private void requireAdmin() {
        if (!UserContextHelper.isAdmin()) {
            throw new ForbiddenException("Only admins can perform this action.");
        }
    }

    private Appointment getAppointmentById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "ID", appointmentId));
    }

    /**
     * Retrieves an appointment by ID.
     * Only the owner or an admin can access it.
     */
    @Transactional(readOnly = true)
    public AppointmentDto getAppointment(UUID appointmentId) {
        log.info("Fetching appointment {} by user {}", appointmentId, UserContextHelper.getCurrentUserId());

        Appointment appointment = getAppointmentById(appointmentId);

        checkAppointmentOwnership(appointment);

        return appointmentMapper.toDto(appointment);
    }

    /**
     * Creates a new appointment for the currently authenticated user.
     * Validates that no appointment exists at the same time for the user.
     */
    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentRequestDto requestDto) {
        String currentUserMail = UserContextHelper.getCurrentUserEmail();
        String currentUserId = UserContextHelper.getCurrentUserId();

        if (currentUserId == null) {
            throw new AuthenticationException("You are not logged in");
        }
        UUID userId = UUID.fromString(currentUserId);
        log.info("Creating appointment for user {} at time {}", userId, requestDto.time());

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
                currentUserMail
        );

        Appointment createdAppointment = appointmentRepository.save(appointment);

        log.info("Appointment {} created for user {}", createdAppointment.getUuid(), userId);
        return appointmentMapper.toDto(createdAppointment);
    }

    /**
     * Reschedules an existing appointment if the time has changed.
     * No-op if the requested time is the same as the current time.
     */
    @Transactional
    public AppointmentDto updateAppointment(UUID appointmentId, UpdateAppointmentDto requestDto) {
        log.info("Updating appointment {} by user {}", appointmentId, UserContextHelper.getCurrentUserId());
        Appointment appointment = getAppointmentById(appointmentId);
        checkAppointmentOwnership(appointment);

        if (!appointment.getAppointmentTime().time().equals(requestDto.time())) {
            UUID actorId = appointment.getUser().getUserId();

            // checking if user has an appointment at that time.
            if (appointmentRepository.existsByTimeAndUserId(requestDto.time(), actorId)) {
                throw new ResourceAlreadyExistsException("Appointment", "Time", requestDto.time());
            }

            String actorEmail = UserContextHelper.getCurrentUserEmail();
            appointment.rescheduleAppointment(requestDto.time(), actorEmail);
            appointmentRepository.save(appointment);
            log.info("Appointment {} rescheduled to {}", appointmentId, requestDto.time());
        } else {
            log.info("Appointment {} time unchanged, skipping reschedule", appointmentId);
        }

        return appointmentMapper.toDto(appointment);
    }

    /**
     * Soft-cancels an appointment. The record is retained but marked as cancelled.
     */
    @Transactional
    public void deleteAppointment(UUID appointmentId) {
        log.info("Cancelling appointment {} by user {}", appointmentId, UserContextHelper.getCurrentUserId());

        Appointment appointment = getAppointmentById(appointmentId);

        checkAppointmentOwnership(appointment);

        String actorEmail = UserContextHelper.getCurrentUserEmail();
        appointment.cancelAppointment(actorEmail);
        appointmentRepository.save(appointment);
        log.info("Appointment {} cancelled", appointmentId);
    }

    /**
     * Approves a pending appointment. Admin only.
     */
    @Transactional
    public AppointmentDto approveAppointment(UUID appointmentId) {
        log.info("Approving appointment {} by admin {}", appointmentId, UserContextHelper.getCurrentUserEmail());

        Appointment appointment = getAppointmentById(appointmentId);

        requireAdmin();

        String actorEmail = UserContextHelper.getCurrentUserEmail();
        appointment.approveAppointment(actorEmail);
        appointmentRepository.save(appointment);
        log.info("Appointment {} approved", appointmentId);

        return appointmentMapper.toDto(appointment);
    }

    /**
     * Marks an appointment as completed. Admin only.
     */
    @Transactional
    public AppointmentDto completeAppointment(UUID appointmentId) {
        log.info("Completing appointment {} by admin {}", appointmentId, UserContextHelper.getCurrentUserEmail());

        Appointment appointment = getAppointmentById(appointmentId);

        requireAdmin();

        String actorEmail = UserContextHelper.getCurrentUserEmail();
        appointment.completeAppointment(actorEmail);

        appointmentRepository.save(appointment);
        log.info("Appointment {} completed", appointmentId);

        return appointmentMapper.toDto(appointment);
    }
}
