package de.adorsys.opba.core.protocol.controller;

import com.google.common.collect.ImmutableMap;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.AccountAccess;
import de.adorsys.xs2a.adapter.service.model.ConsentCreationResponse;
import de.adorsys.xs2a.adapter.service.model.Consents;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.TRANSACTIONS;
import static de.adorsys.opba.core.protocol.controller.constants.ApiVersion.API_1;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.ACCEPT;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.CONTENT_TYPE;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_IP_ADDRESS;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_GTW_ASPSP_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_REQUEST_ID;

@RestController
@RequestMapping(API_1)
@RequiredArgsConstructor
public class AccountInformation {

    private final AccountInformationService ais;

    @GetMapping(TRANSACTIONS)
    public Response<ConsentCreationResponse> transactions() {
        return ais.createConsent(
                RequestHeaders.fromMap(
                        ImmutableMap.<String, String>builder()
                                .put(ACCEPT, "application/json")
                                .put(PSU_ID, "anton.brueckner")
                                .put(X_REQUEST_ID, "2f77a125-aa7a-45c0-b414-cea25a116035")
                                .put(CONTENT_TYPE, "application/json")
                                .put(PSU_IP_ADDRESS, "1.1.1.1")
                                // Identifies bank for XS2A-adapter
                                .put(X_GTW_ASPSP_ID, "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46")
                                .build()
                ),
                consents()
        );
    }

    private Consents consents() {
        Consents consents = new Consents();
        AccountAccess access = new AccountAccess();
        access.setAvailableAccounts(AccountAccess.AvailableAccountsEnum.ALLACCOUNTS);
        consents.setAccess(access);
        consents.setCombinedServiceIndicator(false);
        consents.setRecurringIndicator(true);
        consents.setFrequencyPerDay(10);
        consents.setValidUntil(LocalDate.of(2021, 10, 10));

        return consents;
    }
}
