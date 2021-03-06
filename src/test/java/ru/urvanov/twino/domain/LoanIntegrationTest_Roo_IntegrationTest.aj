// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ru.urvanov.twino.domain;

import java.util.Iterator;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.urvanov.twino.domain.Loan;
import ru.urvanov.twino.domain.LoanDataOnDemand;
import ru.urvanov.twino.domain.LoanIntegrationTest;

privileged aspect LoanIntegrationTest_Roo_IntegrationTest {
    
    declare @type: LoanIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: LoanIntegrationTest: @ContextConfiguration(locations = "classpath*:/META-INF/spring/applicationContext*.xml");
    
    declare @type: LoanIntegrationTest: @Transactional;
    
    @Autowired
    LoanDataOnDemand LoanIntegrationTest.dod;
    
    @Test
    public void LoanIntegrationTest.testCountLoans() {
        Assert.assertNotNull("Data on demand for 'Loan' failed to initialize correctly", dod.getRandomLoan());
        long count = Loan.countLoans();
        Assert.assertTrue("Counter for 'Loan' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void LoanIntegrationTest.testFindLoan() {
        Loan obj = dod.getRandomLoan();
        Assert.assertNotNull("Data on demand for 'Loan' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Loan' failed to provide an identifier", id);
        obj = Loan.findLoan(id);
        Assert.assertNotNull("Find method for 'Loan' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Loan' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void LoanIntegrationTest.testFindAllLoans() {
        Assert.assertNotNull("Data on demand for 'Loan' failed to initialize correctly", dod.getRandomLoan());
        long count = Loan.countLoans();
        Assert.assertTrue("Too expensive to perform a find all test for 'Loan', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Loan> result = Loan.findAllLoans();
        Assert.assertNotNull("Find all method for 'Loan' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Loan' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void LoanIntegrationTest.testFindLoanEntries() {
        Assert.assertNotNull("Data on demand for 'Loan' failed to initialize correctly", dod.getRandomLoan());
        long count = Loan.countLoans();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Loan> result = Loan.findLoanEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Loan' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Loan' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void LoanIntegrationTest.testFlush() {
        Loan obj = dod.getRandomLoan();
        Assert.assertNotNull("Data on demand for 'Loan' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Loan' failed to provide an identifier", id);
        obj = Loan.findLoan(id);
        Assert.assertNotNull("Find method for 'Loan' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyLoan(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'Loan' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void LoanIntegrationTest.testMergeUpdate() {
        Loan obj = dod.getRandomLoan();
        Assert.assertNotNull("Data on demand for 'Loan' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Loan' failed to provide an identifier", id);
        obj = Loan.findLoan(id);
        boolean modified =  dod.modifyLoan(obj);
        Integer currentVersion = obj.getVersion();
        Loan merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'Loan' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void LoanIntegrationTest.testPersist() {
        Assert.assertNotNull("Data on demand for 'Loan' failed to initialize correctly", dod.getRandomLoan());
        Loan obj = dod.getNewTransientLoan(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Loan' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Loan' identifier to be null", obj.getId());
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
        Assert.assertNotNull("Expected 'Loan' identifier to no longer be null", obj.getId());
    }
    
    
}
