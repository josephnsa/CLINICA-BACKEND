package com.clinica.salud.modules.patientportal.application.usecase;

import com.clinica.salud.modules.patientportal.application.dto.PatientLoginRequest;
import com.clinica.salud.modules.patientportal.application.dto.PatientLoginResponse;
import com.clinica.salud.modules.patientportal.domain.model.PatientAccount;
import com.clinica.salud.modules.patientportal.domain.port.PatientAccountRepository;
import com.clinica.salud.modules.patientportal.domain.port.PatientLookupPort;
import com.clinica.salud.shared.exception.UnauthorizedException;
import com.clinica.salud.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientLoginUseCase {

    private static final List<String> PORTAL_PERMISSIONS = List.of(
            "patients:read", "agenda:read", "agenda:create", "agenda:cancel",
            "EXAMENES_READ", "PRESCRIPCIONES_READ"
    );

    private final PatientAccountRepository accountRepository;
    private final PatientLookupPort patientLookupPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public PatientLoginResponse execute(PatientLoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        PatientAccount account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!account.isActive()) {
            throw new UnauthorizedException("Cuenta de paciente inactiva");
        }

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        account.recordLogin();
        accountRepository.save(account);

        PatientLookupPort.PatientInfo patientInfo = patientLookupPort
                .findByEmail(email)
                .orElse(new PatientLookupPort.PatientInfo(account.getPatientId(), "", email));

        String token = jwtService.generateToken(
                account.getPatientId().toString(),
                "PACIENTE_PORTAL",
                PORTAL_PERMISSIONS,
                ""
        );
        String refreshToken = jwtService.generateRefreshToken(account.getPatientId().toString());

        return PatientLoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .patientId(account.getPatientId())
                .fullName(patientInfo.fullName())
                .email(account.getEmail())
                .permissions(PORTAL_PERMISSIONS)
                .build();
    }
}
