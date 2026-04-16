package com.clinica.salud.modules.patientportal.application.usecase;

import com.clinica.salud.modules.patientportal.application.dto.PatientLoginResponse;
import com.clinica.salud.modules.patientportal.application.dto.PatientRegisterRequest;
import com.clinica.salud.modules.patientportal.domain.model.PatientAccount;
import com.clinica.salud.modules.patientportal.domain.port.PatientAccountRepository;
import com.clinica.salud.modules.patientportal.domain.port.PatientLookupPort;
import com.clinica.salud.shared.exception.BusinessRuleException;
import com.clinica.salud.shared.exception.ResourceNotFoundException;
import com.clinica.salud.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientRegisterUseCase {

    private static final List<String> PORTAL_PERMISSIONS = List.of(
            "patients:read", "agenda:read", "agenda:create", "agenda:cancel",
            "EXAMENES_READ", "PRESCRIPCIONES_READ"
    );

    private final PatientAccountRepository accountRepository;
    private final PatientLookupPort patientLookupPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public PatientLoginResponse execute(PatientRegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // Verificar que existe un paciente con ese email
        PatientLookupPort.PatientInfo patientInfo = patientLookupPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", email));

        // Verificar que no tenga cuenta activa ya
        if (accountRepository.existsByEmail(email)) {
            throw new BusinessRuleException("Ya existe una cuenta para el email: " + email);
        }

        PatientAccount account = PatientAccount.builder()
                .patientId(patientInfo.patientId())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        account.validate();
        PatientAccount saved = accountRepository.save(account);
        saved.recordLogin();
        accountRepository.save(saved);

        String token = jwtService.generateToken(
                saved.getPatientId().toString(),
                "PACIENTE_PORTAL",
                PORTAL_PERMISSIONS,
                ""
        );
        String refreshToken = jwtService.generateRefreshToken(saved.getPatientId().toString());

        return PatientLoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .patientId(saved.getPatientId())
                .fullName(patientInfo.fullName())
                .email(saved.getEmail())
                .permissions(PORTAL_PERMISSIONS)
                .build();
    }
}
