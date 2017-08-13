package ru.urvanov.twino.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStreamReader;

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
public class LoanRestApiControllerEmptyTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void before() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testSmoke() throws Exception {

        this.mockMvc
                .perform(get("/loanrestapi/test").accept(MediaType
                        .parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType("application/json;charset=UTF-8"))
                .andExpect(content()
                        .string("{\"success\":true,\"message\":null}"));
    }

    @Test
    public void testApplyLoan() throws Exception {
        ;
        String requestJson = IOUtils
                .readStringAndClose(new InputStreamReader(new ClassPathResource(
                        "ru/urvanov/twino/controller/LoanDto.json")
                                .getInputStream()),
                        -1);
        final String responseJson = IOUtils
                .readStringAndClose(new InputStreamReader(new ClassPathResource(
                        "ru/urvanov/twino/controller/ApplyLoanResponse.json")
                                .getInputStream()),
                        -1);
        this.mockMvc
                .perform(post("/loanrestapi/applyLoan").content(requestJson)
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
    public void testApplyLoanInvalid() throws Exception {
        String requestJson = IOUtils
                .readStringAndClose(new InputStreamReader(new ClassPathResource(
                        "ru/urvanov/twino/controller/LoanDtoInvalid.json")
                                .getInputStream()),
                        -1);

        this.mockMvc
                .perform(post("/loanrestapi/applyLoan").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.parseMediaType(
                                "application/json;charset=UTF-8")))
                .andExpect(status().isBadRequest());
    }

}
