// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ru.urvanov.twino.domain;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.urvanov.twino.domain.Loan;
import ru.urvanov.twino.domain.LoanDataOnDemand;
import ru.urvanov.twino.domain.Person;
import ru.urvanov.twino.domain.PersonDataOnDemand;

privileged aspect LoanDataOnDemand_Roo_DataOnDemand {
    
    declare @type: LoanDataOnDemand: @Component;
    
    private Random LoanDataOnDemand.rnd = new SecureRandom();
    
    private List<Loan> LoanDataOnDemand.data;
    
    @Autowired
    PersonDataOnDemand LoanDataOnDemand.personDataOnDemand;
    
    public Loan LoanDataOnDemand.getNewTransientLoan(int index) {
        Loan obj = new Loan();
        setAmount(obj, index);
        setPerson(obj, index);
        setTerm(obj, index);
        return obj;
    }
    
    public void LoanDataOnDemand.setAmount(Loan obj, int index) {
        BigDecimal amount = BigDecimal.valueOf(index);
        obj.setAmount(amount);
    }
    
    public void LoanDataOnDemand.setPerson(Loan obj, int index) {
        Person person = personDataOnDemand.getRandomPerson();
        obj.setPerson(person);
    }
    
    public void LoanDataOnDemand.setTerm(Loan obj, int index) {
        Date term = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setTerm(term);
    }
    
    public Loan LoanDataOnDemand.getSpecificLoan(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Loan obj = data.get(index);
        Long id = obj.getId();
        return Loan.findLoan(id);
    }
    
    public Loan LoanDataOnDemand.getRandomLoan() {
        init();
        Loan obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return Loan.findLoan(id);
    }
    
    public boolean LoanDataOnDemand.modifyLoan(Loan obj) {
        return false;
    }
    
    public void LoanDataOnDemand.init() {
        int from = 0;
        int to = 10;
        data = Loan.findLoanEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Loan' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Loan>();
        for (int i = 0; i < 10; i++) {
            Loan obj = getNewTransientLoan(i);
            try {
                obj.persist();
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            obj.flush();
            data.add(obj);
        }
    }
    
}
