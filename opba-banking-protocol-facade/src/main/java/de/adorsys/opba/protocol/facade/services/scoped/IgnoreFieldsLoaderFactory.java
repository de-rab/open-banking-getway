package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.db.repository.jpa.IgnoreValidationRuleRepository;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.services.scoped.validation.FieldsToIgnoreLoader;
import de.adorsys.opba.protocol.api.services.scoped.validation.IgnoreValidationRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Factory to generate 'ignore rules for validation error' loader.
 */
@Service
@RequiredArgsConstructor
public class IgnoreFieldsLoaderFactory {

    private final IgnoreValidationRuleRepository ignoreValidationRuleRepository;

    /**
     * Creates ignore rules for a given protocol
     * @param protocolId Protocol to load ignore rules for
     * @return Field code to Ignore Rule loader
     */
    public FieldsToIgnoreLoader createIgnoreFieldsLoader(Long protocolId) {
        if (null == protocolId) {
            return new NoopFieldsToIgnoreLoader();
        }

        return new FieldsToIgnoreLoaderImpl(protocolId, ignoreValidationRuleRepository);
    }

    private static class NoopFieldsToIgnoreLoader implements FieldsToIgnoreLoader {
        @Override
        public <T> Map<FieldCode, IgnoreValidationRule> getIgnoreValidationRules(Class<T> invokerClass, Approach approach) {
            return Collections.emptyMap();
        }
    }
}
