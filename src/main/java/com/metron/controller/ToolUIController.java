package com.metron.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/TUI")
public class ToolUIController {
    
    protected Logger log = LoggerFactory.getLogger(ToolUIController.class);
    /* 15 API
     * Time series for everything (what occurred when)
     * Pie chart showing counts of commands/actions issued for each �action name"
     * Correlations of what patterns of commands commonly occur together
     * Pie chart showing counts of how commands are invoked (keyboard, toolbar, menu, button, mouse-click, etc.)
     * Pie chart showing counts of what views/dialogs are used
     * Pie charts showing total and average duration of activity within views/dialog (per view/dialog)
     * Pie chart showing how many users login via LDAP, composite, or dynamic domains.
     * Count of how many user sessions are included in the data set
     * Count of how many customers (sites) are included in the data set (we can determine �site� from the CIS installation - note: this needs to be done anonymously, yet uniquely)
     * Pie chart of user session counts per site.  Maybe we can drill down from this to list the user sessions
     * Pie charts showing environmental breakdowns per session and site (how many users are using MacOS or certain resolutions of screens), memory configurations.
     * Report of what preferences are often override (i.e. non-default)
     * Counts of how many times windows are moved or resized - what views are visible when they do this?
     * How often do people use scrolling and in what views?
     * Report on common exception patterns along with:
        Counts on how frequently they occur
        What commands/actions were invoked leading up to those exceptions?  
        Are their any common patterns and actions leading up to the exceptions?
     */

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {
        model.addAttribute("message", "Spring 3 MVC Hello World");
        System.out.println("Testing debug log");
        return "hello";

    }
    
    /*
     * get count of all events
     * @params 
     * return event_name, count : as Json
     */
    @RequestMapping(value = "/getOverAllSummary")
    public @ResponseBody
    ResponseEntity<String> getOverAllSummary(HttpServletRequest request) {

        return _formJSONSuccessResponse("");
    }
    
    /*
     * Time series for everything (what occurred when)
     * @params eventName, fromDate, toDate (optional)
     * return timestamp, event_name, event_detail : as Json
     * report : table
     */
    @RequestMapping(value = "/getAllEvent")
    public @ResponseBody
    ResponseEntity<String> getAllEvent(HttpServletRequest request,
            @RequestParam(value = "eventName", required = false) String eventName,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {


        return _formJSONSuccessResponse("");
    }
   
    /*
     * Get action name list
     * @params 
     * return action_name : as Json
     */
    
    @RequestMapping(value = "/getAllAction")
    public @ResponseBody
    ResponseEntity<String> getAllAction(HttpServletRequest request,
            @RequestParam(value = "eventName", required = false) String eventName,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {


        return _formJSONSuccessResponse("");
    }

    /*
     * Get counts of commands/actions issued for each �action name"
     * @params name: name 
     * optional_param: sessionId, sessionFrom, sessionTo
     * return count, command
     * report: Pie chart
     */
    @RequestMapping(value = "/getCommandCountOfActions")
    public @ResponseBody
    ResponseEntity<String> getCommandCountOfActions(HttpServletRequest request,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "sessionFrom", required = false) String sessionFrom,
            @RequestParam(value = "sessionTo", required = false) String sessionTo) {


        return _formJSONSuccessResponse("");
    }
    
    /*
     * Correlations of what patterns of commands commonly occur together
     * @params 
     * return Json
     * report: 
     */
    @RequestMapping(value = "/getCommonlyUsedCommands")
    public @ResponseBody
    ResponseEntity<String> getCommonlyUsedCommands(HttpServletRequest request,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {


        return _formJSONSuccessResponse("");
    }
    
    /*
     * Get counts of how commands are invoked (keyboard, toolbar, menu, button, mouse-click, etc.)
     * @params 
     * optional_param: sessionId, sessionFrom, sessionTo
     * return Json
     * report: Pie chart
     */
    @RequestMapping(value = "/getCommandCountByInvoked")
    public @ResponseBody
    ResponseEntity<String> getCommandCountByInvoked(HttpServletRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "sessionFrom", required = false) String sessionFrom,
            @RequestParam(value = "sessionTo", required = false) String sessionTo) {


        return _formJSONSuccessResponse("");
    }
    
    /*
     * Get counts of what views/dialogs are used
     * @params 
     * return Json
     * report: Pie chart
     */
    @RequestMapping(value = "/getViewCount")
    public @ResponseBody
    ResponseEntity<String> getViewCount(HttpServletRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "sessionFrom", required = false) String sessionFrom,
            @RequestParam(value = "sessionTo", required = false) String sessionTo) {


        return _formJSONSuccessResponse("");
    }
    
    /*
     * Get total and average duration of activity within views/dialog (per view/dialog)
     * @params 
     * return total, avg, view_name as Json
     * report: Pie charts
     */
    @RequestMapping(value = "/getViewActivity")
    public @ResponseBody
    ResponseEntity<String> getViewActivity(HttpServletRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "sessionFrom", required = false) String sessionFrom,
            @RequestParam(value = "sessionTo", required = false) String sessionTo) {


        return _formJSONSuccessResponse("");
    }
    
    /*
     * Get how many users login via LDAP, composite, or dynamic domains.
     * @params 
     * return domain_name, user/session count as Json
     * report: Pie chart
     */
    @RequestMapping(value = "/getCountOfLoginUser")
    public @ResponseBody
    ResponseEntity<String> getCountOfLoginUser(HttpServletRequest request,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        return _formJSONSuccessResponse("");
    }
    
    /*
     * Get count of how many user sessions are included in the data set
     * @params 
     * return count as Json
     * report: 
     */
    @RequestMapping(value = "/getCountOfUserSession")
    public @ResponseBody
    ResponseEntity<String> getCountOfUserSession(HttpServletRequest request,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        return _formJSONSuccessResponse("");
    }
    
    /*
     * Get  Count of how many customers (sites) are included in the data set 
     * (we can determine �site� from the CIS installation - note: this needs to be done anonymously, yet uniquely)
     * 
     * @params 
     * return count as Json
     * report: 
     */
    @RequestMapping(value = "/getCountOfCustomer")
    public @ResponseBody
    ResponseEntity<String> getCountOfCustomer(HttpServletRequest request,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        return _formJSONSuccessResponse("");
    }

    /*
     * Pie chart of user session counts per site.  Maybe we can drill down from this to list the user sessions 
     * 
     * @params 
     * return count as Json
     * report: Pie Chart
     */
    @RequestMapping(value = "/getCountOfUserSessionBySite")
    public @ResponseBody
    ResponseEntity<String> getCountOfUserSessionBySite(HttpServletRequest request,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        return _formJSONSuccessResponse("");
    }
    
    /*
     * Pie charts showing environmental breakdowns per session and site 
     * (how many users are using MacOS or certain resolutions of screens), memory configurations. 
     * 
     * @params 
     * return Json
     * report: Pie Chart
     */
    @RequestMapping(value = "/getEnvBreakdownBySessionAndSite")
    public @ResponseBody
    ResponseEntity<String> getEnvBreakdownBySessionAndSite(HttpServletRequest request,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        return _formJSONSuccessResponse("");
    }
    
    
    private ResponseEntity<String> _formJSONSuccessResponse(String data) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(data, httpHeaders, HttpStatus.OK);

    }
}