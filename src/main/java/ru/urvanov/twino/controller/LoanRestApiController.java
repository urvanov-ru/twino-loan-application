package ru.urvanov.twino.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.urvanov.twino.controller.domain.BaseResponse;
import ru.urvanov.twino.controller.domain.ListLoansResponse;
import ru.urvanov.twino.controller.domain.LoanDto;
import ru.urvanov.twino.controller.domain.LoanResponse;
import ru.urvanov.twino.domain.Loan;
import ru.urvanov.twino.domain.Person;
import ru.urvanov.twino.service.RequestsByCountryService;

@RequestMapping("/loanrestapi/")
@Controller
public class LoanRestApiController {

    private static final Logger LOGGER  = LoggerFactory.getLogger(LoanRestApiController.class);
    
    @Autowired
    private RequestsByCountryService requestsByCountryService;
    
    @RequestMapping(method = RequestMethod.GET, value = "test")
    public @ResponseBody BaseResponse test() {
        BaseResponse result = new BaseResponse();
        result.setSuccess(true);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "applyLoan")
    public @ResponseBody LoanResponse applyLoan(
            @RequestBody @Valid LoanDto loanDto, HttpServletRequest httpServletRequest) {
        LoanResponse loanResponse = new LoanResponse();
        String clientIp = httpServletRequest.getRemoteAddr();
        LOGGER.debug("clientIp={}.", clientIp);
        if (!requestsByCountryService.checkHitsPerSecond(clientIp)) {
            loanResponse.setMessage("Too many requests from your country.");
            return loanResponse;
        }
        Loan loan = new Loan();
        loan.setAmount(loanDto.getAmount());
        loan.setTerm(new Date(loanDto.getTerm()));
        Person person = Person.findPerson(loanDto.getPersonId());
        if (person == null) {
            person = new Person();
        }
        person.setSurname(loanDto.getSurname());
        person.setName(loanDto.getName());
        if (person.getId() == null) {
            person.persist();
        } else {
            person.merge();
        }
        loan.setPerson(person);
        loan.persist();
        
        loanResponse.setSuccess(true);
        loanResponse.setLoanId(loan.getId());
        return loanResponse;
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "listLoans")
    public @ResponseBody ListLoansResponse listLoans() {
        ListLoansResponse result = new ListLoansResponse();
        result.setLoans(convertToLoanDtoList(Loan.findAllLoans()));
        result.setSuccess(true);
        return result;
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "listLoansByPersonId")
    public @ResponseBody ListLoansResponse listLoansByPersonId(@RequestParam("personId") Long personId) {
        ListLoansResponse result = new ListLoansResponse();
        Person person = Person.findPerson(personId);
        if (person == null) {
            result.setMessage("Invalid person id.");
            return result;
        } else if (!person.getEnabled()) {
            result.setMessage("Person is blacklisted.");
            return result;
        }
        
        result.setLoans(convertToLoanDtoList(Loan.findByPersonId(personId)));
        result.setSuccess(true);
        return result;
    }
    
    private List<LoanDto> convertToLoanDtoList(List<Loan> loans) {
        List<LoanDto> result = new ArrayList<>();
        for (Loan loan : loans) {
            LoanDto loanDto = new LoanDto();
            loanDto.setAmount(loan.getAmount());
            loanDto.setTerm(loan.getTerm().getTime());
            loanDto.setName(loan.getPerson().getName());
            loanDto.setSurname(loan.getPerson().getSurname());
            loanDto.setPersonId(loan.getPerson().getId());
            result.add(loanDto);
        }
        return result;
    }

}
