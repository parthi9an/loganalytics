package com.metron.controller;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.metron.model.EventFactory;
import com.metron.model.TestVo;
import com.metron.model.event.Event;

@RestController
public class LogController {

    // @Autowired
    // private AppConfig config;
    //
    // @Autowired
    // private EventMappings eventMappings;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/load", method = RequestMethod.POST)
    public String load(@RequestBody String eventData) {

        return "success";
    }

    @RequestMapping("/test")
    public String test() {

        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s);

        // String[] beanNames = ctx.getBeanDefinitionNames();
        // Arrays.sort(beanNames);
        // for (String beanName : beanNames) {
        // System.out.println(beanName);
        // }
        return "success";
    }

    @RequestMapping("/test1")
    public @ResponseBody TestVo test1(Model model) {
        TestVo tv = new TestVo();
        tv.setId(1);
        tv.setName("Sheik");

        return tv;

    }
}
