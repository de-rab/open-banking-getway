package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.service.ConsentConfirmationService;
import de.adorsys.opba.tppbankingapi.token.model.generated.PsuConsentSessionResponse;
import de.adorsys.opba.tppbankingapi.token.resource.generated.ConsentConfirmationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;

@RestController
@RequiredArgsConstructor
public class TppBankingConsentConfirmationController implements ConsentConfirmationApi {

    private final ConsentConfirmationService consentConfirmationService;

    @Override
    public ResponseEntity<PsuConsentSessionResponse> confirmConsent(String authId,
                                                                    UUID xRequestID,
                                                                    String xTimestampUTC,
                                                                    String xRequestSignature,
                                                                    String fintechID,
                                                                    String serviceSessionPassword,
                                                                    String fintechDataPassword) {
        UUID authorizationSessionId = UUID.fromString(authId);

        if (!consentConfirmationService.confirmConsent(authorizationSessionId, PasswordExtractingUtil.getDataProtectionPassword(serviceSessionPassword, fintechDataPassword))) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PsuConsentSessionResponse response = new PsuConsentSessionResponse();
        response.setAuthorizationSessionId(authorizationSessionId);

        return ResponseEntity.ok()
                .header(X_REQUEST_ID, xRequestID.toString())
                .body(response);
    }
}
