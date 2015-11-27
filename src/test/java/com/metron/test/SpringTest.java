package com.metron.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:conf/log-dispatcher-servlet.xml",
        "classpath:conf/spring/applicationContext.xml" })
public class SpringTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

    }

    // @Test
    public void parseEvent() {
        System.out.println("Starting...");
        try {
            File file = new File("log/cs_server_events2.log");
            if (!file.exists()) {
                return;
            }
            String line = null;

            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {

                MvcResult result = this.mockMvc
                        .perform(post("/load").accept(MediaType.parseMediaType("application/json")).content(line))
                        .andExpect(status().isOk()).andReturn();
                String content = result.getResponse().getContentAsString();
                System.out.println(content);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/test1")).andExpect(status().isOk()).andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    // @Test
    public void parseEvent2() throws Exception {
        System.out.println(context.getServletContext().getRealPath("/"));

        MvcResult result = this.mockMvc
                .perform(post("/load").accept(MediaType.parseMediaType("application/json")).content(
                        "2015-06-04 00:47:39.562 -0700$INFO$END$session=1211180780754 completed for user=admin in domain=composite$7$admin$composite$20702$5733616238249$5733616238133$1211180780754$admin$composite$localhost$127.0.0.1$INTERNAL"))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();
        System.out.println(content);

    }

}
