package com.keskin.appointments.api;

import com.keskin.appointments.application.dto.AppointmentDto;
import com.keskin.appointments.application.dto.CreateAppointmentRequestDto;
import com.keskin.appointments.application.dto.UpdateAppointmentDto;
import com.keskin.appointments.application.service.AppointmentAppService;
import com.keskin.common.security.annotation.RequiresAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;


@Tag(
        name = "Appointment service for appointment system",
        description = "Handles appointment management"
)
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentAppService appointmentAppService;

    @Operation(summary = "Get appointment by ID", description = "Accessible by the owner or an admin.")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointment(
            @PathVariable UUID id) {
        return ResponseEntity.ok(appointmentAppService.getAppointment(id));
    }

    @Operation(summary = "Create a new appointment")
    @PostMapping
    public ResponseEntity<AppointmentDto> createAppointment(
            @Valid @RequestBody CreateAppointmentRequestDto requestDto
            ){
        AppointmentDto createdAppointment = appointmentAppService.createAppointment(requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAppointment.id())
                .toUri();

        return ResponseEntity.created(location).body(createdAppointment);
    }

    @Operation(summary = "Reschedule an appointment")
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto> updateAppointment(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAppointmentDto requestDto
            ){
        AppointmentDto updatedAppointment = appointmentAppService.updateAppointment(id, requestDto);

        return ResponseEntity.ok(updatedAppointment);
    }

    @Operation(summary = "Cancel an appointment")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(
            @PathVariable UUID id
    ){
        appointmentAppService.deleteAppointment(id);

        return ResponseEntity.noContent().build();
    }

    // ------ ADMIN ONLY -------

    @Operation(summary = "Approve an appointment", description = "Admin only.")
    @RequiresAdmin
    @PutMapping("/{id}/approve")
    public ResponseEntity<AppointmentDto> approveAppointment(
            @PathVariable UUID id
    ){
        AppointmentDto approvedAppointment = appointmentAppService.approveAppointment(id);

        return ResponseEntity.ok(approvedAppointment);
    }

    @Operation(summary = "Complete an appointment", description = "Admin only.")
    @RequiresAdmin
    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentDto> completeAppointment(
            @PathVariable UUID id
    ){
        AppointmentDto completedAppointment = appointmentAppService.completeAppointment(id);

        return ResponseEntity.ok(completedAppointment);
    }
}
