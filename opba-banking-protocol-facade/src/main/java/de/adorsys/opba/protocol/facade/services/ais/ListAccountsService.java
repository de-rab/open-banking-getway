package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ais.ListAccounts;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.LIST_ACCOUNTS;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

/**
 * List accounts action handler
 */
@Service
public class ListAccountsService extends FacadeService<ListAccountsRequest, AccountListBody, ListAccounts> {

    public ListAccountsService(
        Map<String, ? extends ListAccounts> actionProviders,
        ProtocolSelector selector,
        @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
        ProtocolResultHandler handler,
        TransactionTemplate txTemplate) {
        super(LIST_ACCOUNTS, actionProviders, selector, provider, handler, txTemplate);
    }
}
