import { SessionService } from '../../common/session.service';
import { AccountAccess, AisConsent, AisConsentToGrant } from './dto/ais-consent';

export class ConsentUtil {
  public static getOrDefault(authorizationId: string, storageService: SessionService): AisConsentToGrant {
    if (!storageService.getConsentObject(authorizationId, () => new AisConsentToGrant())) {
      storageService.setConsentObject(authorizationId, ConsentUtil.initializeConsentObject());
    }

    return storageService.getConsentObject(authorizationId, () => new AisConsentToGrant());
  }

  public static rollbackConsent(authorizationId: string, sessionService: SessionService) {
    const consentObj = ConsentUtil.getOrDefault(authorizationId, sessionService);
    consentObj.consent.access.availableAccounts = null;
    consentObj.consent.access.allPsd2 = null;
    consentObj.consent.access.accounts = null;
    consentObj.consent.access.balances = null;
    consentObj.consent.access.transactions = null;
    sessionService.setConsentObject(authorizationId, consentObj);
  }

  public static isEmptyObject(obj: object) {
    return null === obj || !obj || Object.keys(obj).length === 0;
  }

  private static initializeConsentObject(): AisConsentToGrant {
    const aisConsent = new AisConsentToGrant();
    // FIXME: These fields MUST be initialized by FinTech through API and user can only adjust it.
    aisConsent.consent = new AisConsentImpl();
    aisConsent.consent.access = new AccountAccess();
    aisConsent.consent.frequencyPerDay = 10;
    aisConsent.consent.recurringIndicator = true;
    aisConsent.consent.validUntil = ConsentUtil.futureDate().toISOString().split('T')[0];
    return aisConsent;
  }

  // TODO: should be removed when form is filled by FinTech
  private static futureDate(): Date {
    const result = new Date();
    result.setDate(result.getDate() + 90);
    return result;
  }
}

class AisConsentImpl implements AisConsent {
  access: AccountAccess;
  frequencyPerDay: number;
  recurringIndicator;
  validUntil: string;
}
