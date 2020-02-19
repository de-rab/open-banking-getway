package de.adorsys.opba.protocol.facade.services.ais;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.KeyWithParamsDto;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ConsentAcquiredResult;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import de.adorsys.opba.protocol.facade.config.ApplicationTest;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.services.FacadeEncryptionServiceFactory;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(classes = ApplicationTest.class)
public class ServiceContextProviderTest {

    @Autowired
    ProtocolResultHandler handler;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    @Autowired
    ServiceContextProvider serviceContextProvider;

    @Autowired
    ServiceSessionRepository serviceSessionRepository;

    @Autowired
    BankProtocolRepository protocolRepository;

    @Autowired
    SecretKeyOperations secretKeyOperations;

    @Autowired
    FacadeEncryptionServiceFactory facadeEncryptionServiceFactory;

    @Test
    @SneakyThrows
    void saveSessionTest() {
        UUID id = UUID.randomUUID();
        String testBankID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
        String password = "password";
        ListAccountsRequest request = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .bankId(testBankID)
                                .requestId(id)
                                .sessionPassword(password)
                                .fintechRedirectUrlOk("http://google.com")
                                .fintechRedirectUrlNok("http://microsoft.com")
                                .build()
                ).build();

        ServiceContext<FacadeServiceableGetter> providedContext = serviceContextProvider.provide(request);
        EncryptionService encryptionService = providedContext.getEncryptionService();
        URI redirectionTo = new URI("/");
        Result<URI> result = new ConsentAcquiredResult<>(redirectionTo);
        FacadeRedirectResult<URI> uriFacadeResult = (FacadeRedirectResult) handler.handleResult(result, id, providedContext);

        assertThat(providedContext.getRequest().getFacadeServiceable().getSessionPassword()).isEqualTo(password);

        assertThat(serviceSessionRepository.count()).isEqualTo(1L);
        Iterable<ServiceSession> all = serviceSessionRepository.findAll();
        assertThat(all.iterator().hasNext()).isTrue();
        ServiceSession session = all.iterator().next();

        // check that key is recoverable with password
        KeyWithParamsDto keyWithParams = secretKeyOperations.generateKey(
                password,
                session.getAlgo(),
                session.getSalt(),
                session.getIterCount()
        );
        assertThat(secretKeyOperations.decrypt(session.getSecretKey())).isEqualTo(keyWithParams.getKey());

        // check that in context stored first request parameters facadServicable
        String context = session.getContext();
        byte[] decryptedContext = encryptionService.decrypt(context.getBytes());
        FacadeServiceableRequest decryptedFacadeServiceble = MAPPER.readValue(decryptedContext, FacadeServiceableRequest.class);
        assertAll("Test obj1 with obj2 equality",
                () -> assertEquals(decryptedFacadeServiceble.getBankId(), request.getFacadeServiceable().getBankId()),
                () -> assertEquals(decryptedFacadeServiceble.getRedirectCode(), request.getFacadeServiceable().getRedirectCode()),
                () -> assertEquals(decryptedFacadeServiceble.getAuthorizationSessionId(), request.getFacadeServiceable().getAuthorizationSessionId()),
                () -> assertEquals(decryptedFacadeServiceble.getAuthorization(), request.getFacadeServiceable().getAuthorization()),
                () -> assertEquals(decryptedFacadeServiceble.getServiceSessionId(), request.getFacadeServiceable().getServiceSessionId()),
                () -> assertEquals(decryptedFacadeServiceble.getSessionPassword(), request.getFacadeServiceable().getSessionPassword()),
                () -> assertEquals(decryptedFacadeServiceble.getFintechRedirectUrlOk(), request.getFacadeServiceable().getFintechRedirectUrlOk()),
                () -> assertEquals(decryptedFacadeServiceble.getFintechRedirectUrlNok(), request.getFacadeServiceable().getFintechRedirectUrlNok()));

        // storing some data to context using provided encryption service
        String protocolDefinedDataToStoreInContext = "some context data";
        String encryptedContext = new String(encryptionService.encrypt(
                MAPPER.writeValueAsBytes(protocolDefinedDataToStoreInContext))
        );
        session.setContext(encryptedContext);
        session.setProtocol(protocolRepository.findById(3L).orElseThrow(() -> new IllegalArgumentException("protocol 3 not found")));
        serviceSessionRepository.save(session);

        // check that stored data is encrypted
        ListAccountsRequest request2 = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .serviceSessionId(session.getId())
                                .fintechRedirectUrlOk("http://google.com")
                                .fintechRedirectUrlNok("http://microsoft.com")
                                .authorizationSessionId(uriFacadeResult.getAuthorizationSessionId())
                                .redirectCode(uriFacadeResult.getRedirectCode())
                                .build()
                ).build();
        ServiceContext<FacadeServiceableGetter> providedContext2 = serviceContextProvider.provide(request2);
        EncryptionService encryptionService2 = providedContext2.getEncryptionService();

        ServiceSession sessionForCheck = serviceSessionRepository.findById(session.getId()).orElseThrow(
                () -> new IllegalArgumentException("Session not found:" + session.getId())
        );
        assertThat(sessionForCheck.getContext()).isNotEqualTo(protocolDefinedDataToStoreInContext);

        byte[] decryptedData = encryptionService2.decrypt(sessionForCheck.getContext().getBytes());
        assertThat(MAPPER.readValue(decryptedData, String.class)).isEqualTo(protocolDefinedDataToStoreInContext);
    }
}
