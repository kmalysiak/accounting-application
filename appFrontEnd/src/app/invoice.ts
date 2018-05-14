import { Company } from './company';

export class Invoice {
    invoiceId: number;
    name: string;
    issueDate: string;
    buyer: Company;
    seller: Company;

    constructor() {
        this.buyer = new Company();
        this.seller = new Company();
    }
}