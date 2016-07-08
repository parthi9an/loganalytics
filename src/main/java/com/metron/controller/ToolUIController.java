package com.metron.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
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
import com.metron.event.service.VersionEventService;
import com.metron.event.service.ViewEventService;
import com.metron.event.service.WindowEventService;
import com.metron.event.service.WindowScrollEventService;
import com.metron.model.AccessToken;
import com.metron.service.AuthenticationService;

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
            JSONObject windowResize = new JSONObject();
            windowResize.put("count", new WindowEventService().count());
            JSONObject windowScroll = new JSONObject();
            windowScroll.put("count", new WindowScrollEventService().count());
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
            
            result.put("windowResize", windowResize);
            result.put("windowScroll", windowScroll);
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
    ResponseEntity<String> getAllEvent(HttpServletRequest request/*,
            @RequestBody String filter*/){

        BaseEventService service = new BaseEventService(request.getParameter("filter"));
        String pageSize = request.getParameter("length") != null ? request.getParameter("length") : "";
        String skip = request.getParameter("start") != null ? request.getParameter("start") : "";
        String search = request.getParameter("search[value]");
        
        JSONObject eventresult = service.getAllEvents(pageSize,skip,search);
        
        //Sending necessary info along with data for server side pagination
        JSONObject result = new JSONObject();
        result.put("data", eventresult.has("eventdata") ? eventresult.get("eventdata"): new JSONArray());
        result.put("draw", request.getParameter("draw"));
        result.put("recordsTotal", eventresult.has("count") ? eventresult.get("count"): 0);
        result.put("recordsFiltered", eventresult.has("count") ? eventresult.get("count"): 0);

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
            @RequestBody String filter) {

        ActionEventService service = new ActionEventService(filter);
        JSONObject result = service.getActionNames();
        
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
            @RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId) {

        DomainEventService service = new DomainEventService();
        JSONObject result = service.getDomainNames(source,version,sessionId,serverId);
        
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
            @RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "sessionId", required = false) String sessionId) {

        ServerEventService service = new ServerEventService();
        JSONObject result = service.getServerNames(source,version,userId,sessionId);
        
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
            @RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "serverId", required = false) String serverId) {

        SessionEventService service = new SessionEventService();
        JSONObject result = service.getSessionNames(serverId,userId,source,version);
        
        return _formJSONSuccessResponse(result.toString());
    }
    
    /*
     * Get version list 
     * return version : as Json
     */
    
    @RequestMapping(value = "/getVersions")
    public @ResponseBody
    ResponseEntity<String> getVersions(HttpServletRequest request,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "serverId", required = false) String serverId) {

        VersionEventService service = new VersionEventService();
        JSONObject result = service.getVersionNames(source,serverId,userId,sessionId);
        
        return _formJSONSuccessResponse(result.toString());
    }
    
    @RequestMapping(value = "/getFilters")
    public @ResponseBody
    ResponseEntity<String> getFilters(HttpServletRequest request,
            @RequestBody String filter) {

        FilterEventService service = new FilterEventService(filter);
        JSONObject result = service.getFilters();
        
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
            /*@RequestParam(value = "name", required = true) String actionKey,*/
            @RequestBody String filter) throws JSONException {

        ActionEventService service = new ActionEventService(filter);
        
        JSONObject result = service.getCountOfCommandForAction();

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
            @RequestBody String filter) {

        ActionEventService service = new ActionEventService(filter);
        
        JSONObject result = service.getCountOfActionKey();

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
            @RequestBody String filter) {

        EventPatternService service = new EventPatternService(filter);
        
        JSONArray result = service.getPatterns();

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
            /*@RequestParam(value = "errorTracechecksum", required = true) String errorTracechecksum,*/
            @RequestBody String filter) {
        
        ErrorEventService service = new ErrorEventService(filter);
        
        JSONArray result = service.getPatterns();

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
            @RequestBody String filter) {

        KeyboardEventService service = new KeyboardEventService(filter);
        
        JSONObject result = service.getcommandCount();

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
            @RequestBody String filter) {

        ViewEventService service = new ViewEventService(filter);
        
        JSONObject result = service.getViewcount();

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
            @RequestBody String filter) {

        ViewEventService service = new ViewEventService(filter);
        
        JSONObject result = service.getViewActivityDuration();

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
            @RequestBody String filter) {
        
        DomainEventService service = new DomainEventService(filter);
        
        JSONObject result = service.getCountOfLoginUserByLoginType();

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
            @RequestBody String filter) {
        
        ErrorEventService service = new ErrorEventService(filter);
        
        JSONArray result = service.getExceptionCount();

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
            @RequestBody String filter) {
        
        ConfiguartionEventService service = new ConfiguartionEventService(filter);
        
        JSONObject result = service.getOverridenConfigCount();

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
            @RequestBody String filter) {
        
        WindowEventService service = new WindowEventService(filter);
        
        JSONObject result = service.getCountOfMovedWindows();

        return _formJSONSuccessResponse(result.toString());

    }
    
    /*
     * count of scroll in a view 
     * @params 
     * return name of view scrolled along with count as Json
     * report: pie chart
     */
    @RequestMapping(value = "/getCountOfScrollWindows")
    public @ResponseBody
    ResponseEntity<String> getCountOfScrollWindows(HttpServletRequest request,
            @RequestBody String filter) {
        
        WindowScrollEventService service = new WindowScrollEventService(filter);
        
        JSONObject result = service.getCountOfScrollWindows();

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
            @RequestBody String filter) {

        SessionEventService service = new SessionEventService(filter);
        JSONObject result = service.getCountOfSessions();
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
            @RequestBody String filter) {

        DomainEventService service = new DomainEventService(filter);
        JSONObject result = service.getCountOfUsers();
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
            /*@RequestParam(value = "property", required = true) String property,*/
            @RequestBody String filter) throws JSONException {
        
        EnvironmentEventService service = new EnvironmentEventService(filter);
        JSONObject result = service.getCountOfEnv();
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
        int responsecode = 0;
        try {
            JSONObject credentialsobj = new JSONObject(credentials);
            String currUsr, currPswd;
            currUsr = credentialsobj.getString("uName");
            currPswd = credentialsobj.getString("password");
            AuthenticationService auth = new AuthenticationService();
            result = auth.authenticate(currUsr, currPswd);

            if (result.getString("status").compareTo("Success") == 0) {
                // String accessToken =
                // Base64.getEncoder().encodeToString((currUsr+":"+loginTime).getBytes("utf-8"));
                String accessToken = new String(Base64.encodeBase64((currUsr + ":" + loginTime)
                        .getBytes("utf-8")));
                
                try {
                    responsecode = new AccessToken().insertData(accessToken, loginTime, currUsr);
                } catch (ClientProtocolException e) {
                    result.put("status", "Failed");
                    result.put("message", e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    result.put("status", "Failed");
                    result.put("message", "OrientDB Connection Refused");
                    result.put("info", e.getMessage());
                    e.printStackTrace();
                }
                if (responsecode == HttpURLConnection.HTTP_OK) {
                    response.addHeader("Access-Token", accessToken);
                }
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