package ru.urvanov.twino.controller.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoanDto {
    @NotNull
    @Min(1L)
    private BigDecimal amount;
    
    @NotNull
    private Long term;
    
    @NotNull
    @Size(min = 1, max = 100)
    private String surname;
    
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    
    @NotNull
    private Long personId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }



    public Long getTerm() {
        return term;
    }

    public void setTerm(Long term) {
        this.term = term;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }



}
