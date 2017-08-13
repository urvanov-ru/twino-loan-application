package ru.urvanov.twino.controller.domain;

import java.util.List;

public class ListLoansResponse extends BaseResponse {
    private List<LoanDto> loans;

    public List<LoanDto> getLoans() {
        return loans;
    }

    public void setLoans(List<LoanDto> loans) {
        this.loans = loans;
    }
    
}
