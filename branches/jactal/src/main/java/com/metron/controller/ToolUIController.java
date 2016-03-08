package com.metron.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.metron.event.service.ActionEventService;
import com.metron.event.service.BaseEventService;
import com.metron.event.service.ConfiguartionEventService;
import com.metron.event.service.DomainEventService;
import com.metron.event.service.EnvironmentEventService;
import com.metron.event.service.ErrorEventService;
import com.metron.event.service.EventPatternService;
import com.metron.event.service.FieldEventService;
import com.metron.event.service.FilterEventService;
import com.metron.event.service.KeyboardEventService;
import com.metron.event.service.ServerEventService;
import com.metron.event.service.SessionEventService;
import com.metron.event.service.SourceEventService;
import com.metron.event.service.ViewEventService;
import com.metron.event.service.WindowEventService;
import com.metron.model.AccessToken;
import com.metron.orientdb.OrientDBGraphManager;
import com.metron.service.AuthenticationService;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;

@RestController
@RequestMapping("/TUI")
public class ToolUIController {
    
    protected Logger log = LoggerFactory.getLogger(ToolUIController.class);
    /* 15 API
     * Time series for everything (what occurred when)
     * Pie chart showing counts of commands/actions issued for each “action name"
     * Correlations of what patterns of commands commonly occur together
     * Pie chart showing counts of how commands are invoked (keyboard, toolbar, menu, button, mouse-click, etc.)
     * Pie chart showing counts of what views/dialogs are used
     * Pie charts showing total and average duration of activity within views/dialog (per view/dialog)
     * Pie chart showing how many users login via LDAP, composite, or dynamic domains.
     * Count of how many user sessions are included in the data set
     * Count of how many customers (sites) are included in the data set (we can determine “site” from the CIS installation - note: this needs to be done anonymously, yet uniquely)
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
                
        JSONObject result = new JSONObject();
        try {
            JSONObject window = new JSONObject();
            window.put("count", new WindowEventService().count());
            JSONObject session = new JSONObject();
            session.put("count", new SessionEventService().count()); 
            JSONObject action = new JSONObject();
            action.put("count", new ActionEventService().count());
            JSONObject keyboard = new JSONObject();
            keyboard.put("count", new KeyboardEventService().count());
            JSONObject view = new JSONObject();
            view.put("count", new ViewEventService().count());
            JSONObject site = new JSONObject();
            site.put("count", new ServerEventService().count());
            JSONObject env = new JSONObject();
            env.put("count", new EnvironmentEventService().count());
            JSONObject config = new JSONObject();
            config.put("count", new ConfiguartionEventService().count());
            JSONObject error = new JSONObject();
            error.put("count", new ErrorEventService().count());
            JSONObject field = new JSONObject();
            field.put("count", new FieldEventService().count());
            
            result.put("window", window);
            result.put("session", session);
            result.put("action", action);
            result.put("keyboard", keyboard);
            result.put("view", view);
            result.put("site", site);
            result.put("env", env);
            result.put("config", config);
            result.put("error", error);
            result.put("field", field);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return _formJSONSuccessResponse(result.toString());
        
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
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source){

        BaseEventService service = new BaseEventService();
        JSONArray result = service.getAllEvents(sessionId,serverId,userId,source,fromDate, toDate,limit);

        return _formJSONSuccessResponse(result.toString());
    }
   
    /*
     * Get action name list (i.e action_key)
     * @params 
     * return action_name : as Json
     */
    
    @RequestMapping(value = "/getActionNames")
    public @ResponseBody
    ResponseEntity<String> getActionNames(HttpServletRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        ActionEventService service = new ActionEventService();
        JSONObject result = service.getActionNames(sessionId,serverId,userId,source,fromDate, toDate);
        
        return _formJSONSuccessResponse(result.toString());
    }
    
    /*
     * Get sources list 
     * return source : as Json
     */
    
    @RequestMapping(value = "/getSource")
    public @ResponseBody
    ResponseEntity<String> getSource(HttpServletRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "serverId", required = false) String serverId) {

        SourceEventService service = new SourceEventService();
        JSONObject result = service.getSourceNames(sessionId,userId,serverId);
        
        return _formJSONSuccessResponse(result.toString());
    }
    /*
     * Get domains list 
     * return domains : as Json
     */
    
    @RequestMapping(value = "/getDomains")
    public @ResponseBody
    ResponseEntity<String> getDomains(HttpServletRequest request,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId) {

        DomainEventService service = new DomainEventService();
        JSONObject result = service.getDomainNames(source,sessionId,serverId);
        
        return _formJSONSuccessResponse(result.toString());
    }
    
    /*
     * Get servers list 
     * return servers : as Json
     */
    
    @RequestMapping(value = "/getServers")
    public @ResponseBody
    ResponseEntity<String> getServers(HttpServletRequest request,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "sessionId", required = false) String sessionId) {

        ServerEventService service = new ServerEventService();
        JSONObject result = service.getServerNames(source,userId,sessionId);
        
        return _formJSONSuccessResponse(result.toString());
    }
    
    /*
     * Get sessions list 
     * return sessions : as Json
     */
    
    @RequestMapping(value = "/getSessions")
    public @ResponseBody
    ResponseEntity<String> getSessions(HttpServletRequest request,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "serverId", required = false) String serverId) {

        SessionEventService service = new SessionEventService();
        JSONObject result = service.getSessionNames(serverId,userId,source);
        
        return _formJSONSuccessResponse(result.toString());
    }

    /*
     * Get counts of commands/actions issued for each “action name"
     * @params name: name 
     * optional_param: sessionId, sessionFrom, sessionTo
     * return count, command
     * report: Pie chart
     */
    @RequestMapping(value = "/getCommandCountOfActions")
    public @ResponseBody
    ResponseEntity<String> getCommandCountOfActions(HttpServletRequest request,
            @RequestParam(value = "name", required = true) String actionKey,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        ActionEventService service = new ActionEventService();
        
        JSONObject result = service.getCountOfCommandForAction(actionKey,sessionId,serverId,userId,source, fromDate, toDate);

        return _formJSONSuccessResponse(result.toString());
    }
    
    /*
     * Get counts of actions invoked (action key/name)
     * @params
     * optional_param: sessionId, sessionFrom, sessionTo
     * return count, action key/name
     * report: Pie chart
     */
    @RequestMapping(value = "/getActionKeyCount")
    public @ResponseBody
    ResponseEntity<String> getActionKeyCount(HttpServletRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        ActionEventService service = new ActionEventService();
        
        JSONObject result = service.getCountOfActionKey(sessionId,serverId,userId,source, fromDate, toDate);

        return _formJSONSuccessResponse(result.toString());
    }
    
    /*
     * Correlations of what patterns of commands commonly occur together
     * @params 
     * return Json
     * report: 
     */
    @RequestMapping(value = "/getCommonlyUsedPatterns")
    public @ResponseBody
    ResponseEntity<String> getCommonlyUsedPatterns(HttpServletRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        EventPatternService service = new EventPatternService();
        
        JSONArray result = service.getPatterns(sessionId,serverId,userId,source,fromDate,toDate);

        return _formJSONSuccessResponse(result.toString());
    }
    
    /*
     * common patterns and actions leading up to the exceptions
     * @params 
     * return Json
     * report: 
     */
    @RequestMapping(value = "/getCommonExceptionPatterns")
    public @ResponseBody
    ResponseEntity<String> getCommonExceptionPatterns(HttpServletRequest request,
            @RequestParam(value = "errorTracechecksum", required = true) String errorTracechecksum,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {
        
        ErrorEventService service = new ErrorEventService();
        
        JSONArray result = service.getPatterns(errorTracechecksum,sessionId,serverId,userId,source,fromDate,toDate);

        return _formJSONSuccessResponse(result.toString());
    }
    
    @RequestMapping(value = "/getEventDetails")
    public @ResponseBody
    ResponseEntity<String> getEventDetails(HttpServletRequest request,
            @RequestParam(value = "rid", required = true) String rid){
        
        BaseEventService service = new BaseEventService();
        
        JSONObject result = service.getEventDetails(rid);

        return _formJSONSuccessResponse(result.toString());
    }
    
    /*
     * Get counts of how commands are invoked (keyboard, toolbar, menu, button, mouse-click, etc.)
     * @params 
     * optional_param: sessionId, fromDate, toDate
     * return Json
     * report: Pie chart
     */
    @RequestMapping(value = "/getCommandCountByInvoked")
    public @ResponseBody
    ResponseEntity<String> getCommandCountByInvoked(HttpServletRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        KeyboardEventService service = new KeyboardEventService();
        
        JSONObject result = service.getcommandCount(sessionId,serverId,userId,source, fromDate, toDate);

        return _formJSONSuccessResponse(result.toString());
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
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        ViewEventService service = new ViewEventService();
        
        JSONObject result = service.getViewcount(sessionId,serverId,userId,source, fromDate, toDate);

        return _formJSONSuccessResponse(result.toString());
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
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        ViewEventService service = new ViewEventService();
        
        JSONObject result = service.getViewActivityDuration(sessionId,serverId,userId,source, fromDate, toDate);

        return _formJSONSuccessResponse(result.toString());
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
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {
        
        DomainEventService service = new DomainEventService();
        
        JSONObject result = service.getCountOfLoginUserByLoginType(sessionId,serverId,userId,source, fromDate, toDate);

        return _formJSONSuccessResponse(result.toString());

    }
    
    /*
     * count of Exceptions occurred
     * @params 
     * return Exception along with count as Json
     * report: table
     */
    @RequestMapping(value = "/getExceptionCount")
    public @ResponseBody
    ResponseEntity<String> getExceptionCount(HttpServletRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {
        
        ErrorEventService service = new ErrorEventService();
        
        JSONArray result = service.getExceptionCount(sessionId,serverId,userId,source,fromDate, toDate);

        return _formJSONSuccessResponse(result.toString());

    }
    
    /*
     * count of overridden Configurations
     * @params 
     * return Configuration along with count as Json
     * report: pie chart
     */
    @RequestMapping(value = "/getOverridenConfigCount")
    public @ResponseBody
    ResponseEntity<String> getOverridenConfigCount(HttpServletRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {
        
        ConfiguartionEventService service = new ConfiguartionEventService();
        
        JSONObject result = service.getOverridenConfigCount(sessionId,serverId,userId,source, fromDate, toDate);

        return _formJSONSuccessResponse(result.toString());

    }
    
    /*
     * count of windows moved
     * @params 
     * return name of window moved along with count as Json
     * report: pie chart
     */
    @RequestMapping(value = "/getCountOfMovedWindows")
    public @ResponseBody
    ResponseEntity<String> getCountOfMovedWindows(HttpServletRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {
        
        WindowEventService service = new WindowEventService();
        
        JSONObject result = service.getCountOfMovedWindows(sessionId,serverId,userId,source, fromDate, toDate);

        return _formJSONSuccessResponse(result.toString());

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
     * Get  Count of how many customers (sites) (server_id)(hash of Ipaddress & port) are included in the data set 
     * (we can determine “site” from the CIS installation - note: this needs to be done anonymously, yet uniquely)
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
     * Pie chart of session counts per site.  Maybe we can drill down from this to list the user sessions 
     * 
     * @params 
     * return count as Json
     * report: Pie Chart
     */
    @RequestMapping(value = "/getCountOfSessions")
    public @ResponseBody
    ResponseEntity<String> getCountOfSessions(HttpServletRequest request,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        SessionEventService service = new SessionEventService();
        JSONObject result = service.getCountOfSessions(serverId,userId,source,fromDate,toDate);
        return _formJSONSuccessResponse(result.toString());
    }
    
    /*
     * Pie chart of user(user_id/domain_id)(hash of user@domain) counts per site.  Maybe we can drill down from this to list the user sessions 
     * 
     * @params 
     * return count as Json
     * report: Pie Chart
     */
    @RequestMapping(value = "/getCountOfUsers")
    public @ResponseBody
    ResponseEntity<String> getCountOfUsers(HttpServletRequest request,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        DomainEventService service = new DomainEventService();
        JSONObject result = service.getCountOfUsers(serverId,sessionId,source,fromDate,toDate);
        return _formJSONSuccessResponse(result.toString());
    }
    
    /*
     * Pie charts showing environmental breakdowns per session and site 
     * (how many users are using MacOS or certain resolutions of screens), memory configurations. 
     * 
     * @params 
     * return Json
     * report: Pie Chart
     */
    @RequestMapping(value = "/getEnvBreakdown")
    public @ResponseBody
    ResponseEntity<String> getEnvBreakdown(HttpServletRequest request,
            @RequestParam(value = "property", required = true) String property,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {
        
        EnvironmentEventService service = new EnvironmentEventService();
        JSONObject result = service.getCountOfEnv(property,sessionId,serverId,userId,source,fromDate,toDate);
        return _formJSONSuccessResponse(result.toString());
    }
    
    @RequestMapping(value = "/getEnvProperties")
    public @ResponseBody
    ResponseEntity<String> getEnvProperties(HttpServletRequest request){
        
        EnvironmentEventService service = new EnvironmentEventService();
        JSONObject result = service.getEnvProperties();
        return _formJSONSuccessResponse(result.toString());
    }
    
    /**
     * Save filter chosen by the user
     * @param filter
     * @return
     */
    @RequestMapping(value = "/saveFilterCriteria",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> saveFilterCriteria(@RequestBody String filter){
        
        FilterEventService service = new FilterEventService();
        JSONObject result = service.saveFilterCriteria(filter);
        return _formJSONSuccessResponse(result.toString());
    }
    
    @RequestMapping(value = "/getSavedFilterCriteria")
    public @ResponseBody
    ResponseEntity<String> getSavedFilterCriteria(HttpServletRequest request,
            @RequestParam(value = "uName", required = true) String uName,
            @RequestParam(value = "limit", required = false) String limit){
        
        FilterEventService service = new FilterEventService();
        JSONObject result = service.getSavedFilterCriteria(uName,limit);
        return _formJSONSuccessResponse(result.toString());
    }
    
    @RequestMapping(value = "/deleteAllFilters")
    public @ResponseBody
    ResponseEntity<String> deleteAllFilters(HttpServletRequest request,
            @RequestParam(value = "uName", required = true) String uName){
        
        FilterEventService service = new FilterEventService();
        JSONObject result = service.deleteAllFilters(uName);
        return _formJSONSuccessResponse(result.toString());
    }
    
    @RequestMapping(value = "/deleteRecord")
    public @ResponseBody
    ResponseEntity<String> deleteRecord(HttpServletRequest request,
            @RequestParam(value = "rid", required = true) String rid){
        
        BaseEventService service = new BaseEventService();
        JSONObject result = service.deleteRecord(rid);
        return _formJSONSuccessResponse(result.toString());
    }
    
    /**
     * Dummy authentication
     * @param credentials
     * @return
     */
    @RequestMapping(value = "/authenticate",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> authenticate(HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody String credentials){
        
        JSONObject result = new JSONObject();
        String loginTime = Long.toString(new Date().getTime());
        try {
            JSONObject credentialsobj = new JSONObject(credentials);
            String currUsr, currPswd;
            currUsr = credentialsobj.getString("uName");
            currPswd = credentialsobj.getString("password");
            AuthenticationService auth = new AuthenticationService();
            result = auth.authenticate(currUsr, currPswd);

            if(result.getString("status").compareTo("success") == 0){
                //String accessToken = Base64.getEncoder().encodeToString((currUsr+":"+loginTime).getBytes("utf-8"));
                String accessToken = new String(Base64.encodeBase64((currUsr+":"+loginTime).getBytes("utf-8")));
                response.addHeader("Access-Token", accessToken);
                OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
                new AccessToken(currUsr,loginTime,accessToken,graph);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return _formJSONSuccessResponse(result.toString());
    }
    
    private ResponseEntity<String> _formJSONSuccessResponse(String data) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(data, httpHeaders, HttpStatus.OK);

    }
}