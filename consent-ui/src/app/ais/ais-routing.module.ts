import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EntryPageComponent } from './entry-page/entry-page.component';
import { EnterPinPageComponent } from './enter-pin-page/enter-pin-page.component';
import { EnterTanPageComponent } from './enter-tan-page/enter-tan-page.component';
import { ResultPageComponent } from './result-page/result-page.component';
import { ScaSelectPageComponent } from './sca-select-page/sca-select-page.component';
import { ErrorPageComponent } from './error-page/error-page.component';
import { ConsentInitiateComponent } from './entry-page/initiation/consent-initiate/consent-initiate.component';
import { EntryPageAccountsComponent } from './entry-page/initiation/accounts/entry-page-accounts/entry-page-accounts.component';
import { DedicatedAccessComponent } from './entry-page/initiation/common/dedicated-access/dedicated-access.component';
import { AccountsConsentReviewComponent } from './entry-page/initiation/accounts/accounts-consent-review/accounts-consent-review.component';
import { EntryPageTransactionsComponent } from './entry-page/initiation/transactions/entry-page-transactions/entry-page-transactions.component';
import { TransactionsConsentReviewComponent } from './entry-page/initiation/transactions/transactions-consent-review/transactions-consent-review.component';
import { ToAspspRedirectionComponent } from './to-aspsp-page/to-aspsp-redirection.component';
import { ConsentSharingComponent } from './entry-page/initiation/consent-sharing/consent-sharing.component';
import { WaitForDecoupledComponent } from './wait-for-decoupled/wait-for-decoupled.component';
import { RestoreSessionPageComponent } from './restore-session-page/restore-session-page.component';

const routes: Routes = [
  {
    path: ':authId',
    component: EntryPageComponent,
    children: [
      { path: '', component: ConsentInitiateComponent },
      { path: ConsentSharingComponent.ROUTE, component: ConsentSharingComponent },
      {
        path: EntryPageAccountsComponent.ROUTE,
        children: [
          { path: '', component: EntryPageAccountsComponent },
          { path: DedicatedAccessComponent.ROUTE, component: DedicatedAccessComponent },
          { path: AccountsConsentReviewComponent.ROUTE, component: AccountsConsentReviewComponent }
        ]
      },
      {
        path: EntryPageTransactionsComponent.ROUTE,
        children: [
          { path: '', component: EntryPageTransactionsComponent },
          { path: DedicatedAccessComponent.ROUTE, component: DedicatedAccessComponent },
          { path: TransactionsConsentReviewComponent.ROUTE, component: TransactionsConsentReviewComponent }
        ]
      },
      { path: ToAspspRedirectionComponent.ROUTE, component: ToAspspRedirectionComponent },
      { path: WaitForDecoupledComponent.ROUTE, component: WaitForDecoupledComponent },
      { path: ResultPageComponent.ROUTE, component: ResultPageComponent },
      { path: RestoreSessionPageComponent.ROUTE, component: RestoreSessionPageComponent },
      {
        path: 'authenticate',
        component: EnterPinPageComponent
      },
      {
        path: 'select-sca-method',
        component: ScaSelectPageComponent
      },
      {
        path: EnterTanPageComponent.ROUTE,
        component: EnterTanPageComponent
      },
      {
        path: 'report-sca-result',
        component: ResultPageComponent
      },
      { path: 'error', component: ErrorPageComponent }
    ]
  },
  {
    path: 'error',
    component: ErrorPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AisRoutingModule {}
