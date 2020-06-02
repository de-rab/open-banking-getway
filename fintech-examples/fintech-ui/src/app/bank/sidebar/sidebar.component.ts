import { Component, OnInit } from '@angular/core';
import { BankProfileService } from '../../bank-search/services/bank-profile.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  showListAccounts = false;
  showListTransactions = false;
  showInitiatePayment = false;
  showSettings = false;
  bankId: string;
  bankName: string;

  constructor(private bankProfileService: BankProfileService, private route: ActivatedRoute, private spinner: NgxSpinnerService ) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('bankid');
    this.getBankProfile(this.bankId);
  }

  getBankProfile(id: string) {
    this.spinner.show();
    this.bankProfileService.getBankProfile(id).subscribe(response => {
      this.bankName = response.bankName;
      this.showListAccounts = response.services.includes('LIST_ACCOUNTS');
      this.showListTransactions = response.services.includes('LIST_TRANSACTIONS');
      this.showInitiatePayment = response.services.includes('INITIATE_PAYMENT');
    }).add(() => this.spinner.hide());
  }

  getRouterLinkListAccounts(): string {
    return this.showListAccounts ? 'account' : '';
  }
}
