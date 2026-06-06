export interface InvestorPortfolio {
  investorId: number;
  fullName: string;
  age: number;
  products: Product[];
}

export interface Product{
    id:number;
    type: 'RETIREMENT'|'SAVINGS';
    name:string;
    currentBalance:number;
}

export interface WithdrawalRequest{
    productId:number;
    amount:number;
}
