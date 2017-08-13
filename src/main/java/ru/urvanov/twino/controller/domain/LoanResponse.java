package ru.urvanov.twino.controller.domain;

public class LoanResponse extends BaseResponse {
	private Long loanId;

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}
	
}
