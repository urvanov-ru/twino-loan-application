package ru.urvanov.twino.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.util.IOUtils;
import org.hamcrest.CustomMatcher;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {
        "file:src/main/webapp/WEB-INF/spring/webmvc-config.xml",
        "file:src/main/resources/META-INF/spring/applicationContext.xml" })
@ActiveProfiles("test")
public class LoanRestApiControllerWithDataTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Before
    public void before() throws Exception {

        // initialize your database connection here
        IDatabaseConnection databaseConnection = null;
        // ...

        // initialize your dataset here
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        IDataSet dataSet = builder.build(new ClassPathResource(
                "ru/urvanov/twino/controller/test-data.xml").getInputStream());

        try (Connection connection = dataSource.getConnection()) {
            databaseConnection = new DatabaseConnection(connection);
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
        }
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testListLoans() throws Exception {
        final String responseJson = IOUtils
                .readStringAndClose(new InputStreamReader(new ClassPathResource(
                        "ru/urvanov/twino/controller/ListLoansResponse.json")
                                .getInputStream()),
                        -1);
        this.mockMvc
                .perform(post("/loanrestapi/listLoans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.parseMediaType(
                                "application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(
                        content().string(new CustomMatcher<String>("matcher") {

                            @Override
                            public boolean matches(Object item) {
                                try {
                                    return JSONCompare
                                            .compareJSON(responseJson,
                                                    (String) item,
                                                    JSONCompareMode.LENIENT)
                                            .passed();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    return false;
                                }
                            }

                        }));
    }

    @Test
    public void testListLoansByPersonId() throws Exception {
        final String responseJson = IOUtils
                .readStringAndClose(new InputStreamReader(new ClassPathResource(
                        "ru/urvanov/twino/controller/ListLoansByPersonIdResponse.json")
                                .getInputStream()),
                        -1);
        this.mockMvc
                .perform(post("/loanrestapi/listLoansByPersonId?personId=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.parseMediaType(
                                "application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(
                        content().string(new CustomMatcher<String>("matcher") {

                            @Override
                            public boolean matches(Object item) {
                                try {
                                    return JSONCompare
                                            .compareJSON(responseJson,
                                                    (String) item,
                                                    JSONCompareMode.LENIENT)
                                            .passed();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    return false;
                                }
                            }

                        }));
    }

    @Test
    public void testListLoansByPersonIdBlacklisted() throws Exception {
        final String responseJson = IOUtils
                .readStringAndClose(new InputStreamReader(new ClassPathResource(
                        "ru/urvanov/twino/controller/ListLoansByPersonIdBlacklistedResponse.json")
                                .getInputStream()),
                        -1);
        this.mockMvc
                .perform(post("/loanrestapi/listLoansByPersonId?personId=3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.parseMediaType(
                                "application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(
                        content().string(new CustomMatcher<String>("matcher") {

                            @Override
                            public boolean matches(Object item) {
                                try {
                                    return JSONCompare
                                            .compareJSON(responseJson,
                                                    (String) item,
                                                    JSONCompareMode.LENIENT)
                                            .passed();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    return false;
                                }
                            }

                        }));
    }

}
