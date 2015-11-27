package com.metron.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.metron.AppConfig;
import com.metron.model.EventFactory;
import com.metron.model.event.Event;
import com.metron.orientdb.OrientDBGraphManager;
import com.metron.service.BaseService;
import com.metron.service.DomainService;
import com.metron.service.ExceptionService;
import com.metron.service.HostService;
import com.metron.service.RequestService;
import com.metron.service.SessionService;
import com.metron.service.UserService;
import com.metron.service.interfaces.IBaseService;
import com.metron.service.interfaces.IDomainService;
import com.metron.service.interfaces.IExceptionService;
import com.metron.service.interfaces.IHostService;
import com.metron.service.interfaces.IRequestService;
import com.metron.service.interfaces.ISessionService;
import com.metron.service.interfaces.IUserService;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;

@RestController
public class WSController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {
        model.addAttribute("message", "Spring 3 MVC Hello World");
        return "hello";

    }

    @RequestMapping(value = "/sendUIEventLog", method = RequestMethod.HEAD)
    public @ResponseBody
    ResponseEntity<String> hasConnected(HttpServletRequest request) {

        // For now just log the data.(To check if data arrives here)
        System.out.println("WSController: Connected");
        return null;
    }

    // TDB3: add two parameters 1) fileName 2) data
    @RequestMapping(value = "/sendUIEventLog", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> sendUIEventLog(HttpServletRequest request,
            @RequestParam(value = "fileName", required = true) String fileName,
            @RequestParam(value = "data", required = true) String data) {
        // //For now just log the data.(To check if data arrives here)
        System.out.println("WSController: " + data);
        System.out.println("WSController: Log received successfully. ");

        return _formJSONSuccessResponse(data);
    }

    @RequestMapping(value = "/getSessions")
    public @ResponseBody
    ResponseEntity<String> getSessions() {
        return _formJSONSuccessResponse("");
    }

    @RequestMapping(value = "/dummy")
    public @ResponseBody
    ResponseEntity<String> dummy(HttpServletRequest request,
            @RequestParam(value = "filename", required = false) String name,
            @RequestParam(value = "data", required = false) String data) {
        System.out.println(request.getServerName());
        System.out.println(request.getRequestURL());
        return _formJSONSuccessResponse(data);
    }

    @RequestMapping(value = "/logSearch")
    public @ResponseBody
    ResponseEntity<String> logSearch(HttpServletRequest request,
            @RequestParam(value = "keyword", required = false) String keyword) {

        JSONObject result = new JSONObject();
        try {

            JSONObject match = new JSONObject();
            match.put("in", "Host");
            match.put("count", 5);

            JSONArray matches = new JSONArray();
            matches.put(match);

            match = new JSONObject();
            match.put("in", "Request");
            match.put("count", 6);

            matches.put(match);
            result.put("matches", matches);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return _formJSONSuccessResponse(result.toString());
    }

    @RequestMapping(value = "/getCustomers")
    public @ResponseBody
    ResponseEntity<String> getCustomers(HttpServletRequest request) {

        JSONObject result = new JSONObject();
        try {
            result.put("totalrecords", 10);

            JSONObject customer = new JSONObject();
            customer.put("id", "#12:0");
            customer.put("name", "Customer One");

            JSONArray customers = new JSONArray();
            customers.put(customer);
            customers.put(customer);
            result.put("customers", customers);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return _formJSONSuccessResponse(result.toString());
    }

    @RequestMapping(value = "/getExceptions")
    public @ResponseBody
    ResponseEntity<String> getExceptions(HttpServletRequest request,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "hostId", required = false) String hostId,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        ExceptionService service = new ExceptionService();

        JSONObject result = service.getExceptions(keyword, hostId, fromDate, toDate);

        return _formJSONSuccessResponse(result.toString());
    }

    /*@RequestMapping(value = "/getWarnings")
    public @ResponseBody
    ResponseEntity<String> getWarnings(HttpServletRequest request,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "hostId", required = false) String hostId,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        WarningService service = new WarningService();

        JSONObject result = service.getWarnings(keyword, hostId, fromDate, toDate);

        return _formJSONSuccessResponse(result.toString());
    }*/

    @RequestMapping(value = "/globalSearch")
    public @ResponseBody
    ResponseEntity<String> globalSearch(HttpServletRequest request,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "hostId", required = false) String hostId,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        JSONObject result = new JSONObject();
        try {
            IExceptionService excService = new ExceptionService();
            IRequestService reqService = new RequestService();
            /*IWarningService warnService = new WarningService();*/

            JSONObject exceptionRes = excService.search(keyword, hostId, fromDate, toDate);

            /*JSONObject warningRes = warnService.search(keyword, hostId, fromDate, toDate);*/

            JSONObject requestRes = reqService.search(keyword, hostId, fromDate, toDate);

            JSONArray searchResults = requestRes.getJSONArray("list");

            for (int i = 0; i < exceptionRes.getJSONArray("list").length(); i++) {
                searchResults.put(exceptionRes.getJSONArray("list").get(i));
            }
            /*for (int i = 0; i < warningRes.getJSONArray("list").length(); i++) {
                searchResults.put(warningRes.getJSONArray("list").get(i));
            }*/
            long totalRec = exceptionRes.getLong("count") + requestRes.getLong("count")
                   /* + warningRes.getLong("count")*/;
            result.put("message", "Matched in " + exceptionRes.getLong("count") + "Exceptions and "
                    /*+ warningRes.getLong("count") + "Warnings and "*/ + requestRes.getLong("count")
                    + " Request");
            result.put("totalrecords", totalRec);
            result.put("results", new BaseService().sortByTimestamp(searchResults));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return _formJSONSuccessResponse(result.toString());
    }

    @RequestMapping(value = "/logBrowser")
    public @ResponseBody
    ResponseEntity<String> logBrowser(HttpServletRequest request,
            @RequestParam(value = "timestamp", required = false) String timestamp,
            @RequestParam(value = "hostId", required = false) String hostId,
            @RequestParam(value = "get", required = false) String getPage) {

        IBaseService service = new BaseService();
        JSONObject json = service.getEvents(timestamp, hostId, getPage);
        return _formJSONSuccessResponse(json.toString());

    }

    @RequestMapping(value = "/getExceptionElements")
    public @ResponseBody
    ResponseEntity<String> getExceptionElements(HttpServletRequest request,
            @RequestParam(value = "id", required = true) String exceptionId) {

        IExceptionService service = new ExceptionService();
        JSONObject result = service.getExceptionElements(exceptionId);
        return _formJSONSuccessResponse(result.toString());
    }

    @RequestMapping(value = "/sendlog")
    public @ResponseBody
    ResponseEntity<String> sendlog(HttpServletRequest request, @RequestBody String requestString) {
        JSONObject jo;
        try {

            jo = new JSONObject(requestString);
            String line = jo.getString("message");
            String host = jo.getString("host");
            String path = jo.getString("path");
            Event event = EventFactory.getInstance().parseLine(line, path, host);
            if (event != null) {
                OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
                event.setGraph(graph);
                event.process();
                event.getGraph().shutdown();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return _formJSONSuccessResponse("");
    }

    @RequestMapping(value = "/getOverallSummary", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getOverallSummary() {
        JSONObject result = new JSONObject();

        try {
            JSONObject request = new JSONObject();
            request.put("count", new RequestService().count());
            JSONObject session = new JSONObject();
            session.put("count", new SessionService().count());
            JSONObject user = new JSONObject();
            user.put("count", new UserService().count());
            JSONObject domain = new JSONObject();
            domain.put("count", new DomainService().count());
            result.put("request", request);
            result.put("session", session);
            result.put("user", user);
            result.put("domain", domain);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return _formJSONSuccessResponse(result.toString());
    }

    @RequestMapping(value = "/hosts", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getHosts(HttpServletRequest request) {
        IHostService service = new HostService();
        JSONArray jsonArr = service.getHosts();
        return _formJSONSuccessResponse(jsonArr.toString());
    }

    @RequestMapping(value = "/getServerStatsGraph", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getServerStatsGraph(HttpServletRequest request,
            @RequestParam(value = "hostId", required = false) String hostId,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        IHostService service = new HostService();
        JSONObject result = service.getServerStatsGraph(hostId, fromDate, toDate);
        return _formJSONSuccessResponse(result.toString());
    }
    @RequestMapping(value = "/getRequestAndSessionStatusGraph", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getRequestAndSessionStatusGraph(HttpServletRequest request,
            @RequestParam(value = "hostId", required = false) String hostId,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        IHostService service = new HostService();
        JSONObject result = service.getRequestAndSessionStatus(hostId, fromDate, toDate);
        return _formJSONSuccessResponse(result.toString());
    }
    @RequestMapping(value = "/getExceptionsGraph", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getExceptionsGraph(HttpServletRequest request,
            @RequestParam(value = "hostId", required = false) String hostId,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        IExceptionService service = new ExceptionService();
        JSONObject result = service.getExceptionsGraph(hostId, fromDate, toDate);
        return _formJSONSuccessResponse(result.toString());
    }

    // ----------- INDEX PAGE API WITH REQUEST FILTERS ---------------------//

    @RequestMapping(value = "/getRequestAndSessionGraph", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getRequestAndSessionGraph(HttpServletRequest request,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "maxBytesIn", required = false) Integer maxBytesIn,
            @RequestParam(value = "minBytesIn", required = false) Integer minBytesIn,
            @RequestParam(value = "maxBytesOut", required = false) Integer maxBytesOut,
            @RequestParam(value = "minBytesOut", required = false) Integer minBytesOut,
            @RequestParam(value = "minRowsAffected", required = false) Integer minRowsAffected,
            @RequestParam(value = "maxRowsAffected", required = false) Integer maxRowsAffected,
            @RequestParam(value = "last", required = false) Long last,
            @RequestParam(value = "host", required = false) String host) {

        IHostService service = new HostService();
        JSONObject result = service.getRequestAndSession(status, maxBytesIn, minBytesIn,
                maxBytesOut, minBytesOut, minRowsAffected, maxRowsAffected, last, host);
        return _formJSONSuccessResponse(result.toString());
    }

    @RequestMapping(value = "/getUsersWithRequestFilter", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getUsersWithRequestFilter(HttpServletRequest request,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "maxBytesIn", required = false) Integer maxBytesIn,
            @RequestParam(value = "minBytesIn", required = false) Integer minBytesIn,
            @RequestParam(value = "maxBytesOut", required = false) Integer maxBytesOut,
            @RequestParam(value = "minBytesOut", required = false) Integer minBytesOut,
            @RequestParam(value = "rowsAffected", required = false) Integer minRowsAffected,
            @RequestParam(value = "rowsAffected", required = false) Integer maxRowsAffected,
            @RequestParam(value = "last", required = false) Long last,
            @RequestParam(value = "host", required = false) String host) {

        IUserService service = new UserService();
        JSONArray json = service.getUsersWithRequestFilter(status, maxBytesIn, minBytesIn,
                maxBytesOut, minBytesOut, minRowsAffected, maxRowsAffected, last, host);
        return _formJSONSuccessResponse(json.toString());
    }

    @RequestMapping(value = "/getSessionsWithRequestFilter", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getSessionsWithRequestFilter(HttpServletRequest request,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "maxBytesIn", required = false) Integer maxBytesIn,
            @RequestParam(value = "minBytesIn", required = false) Integer minBytesIn,
            @RequestParam(value = "maxBytesOut", required = false) Integer maxBytesOut,
            @RequestParam(value = "minBytesOut", required = false) Integer minBytesOut,
            @RequestParam(value = "rowsAffected", required = false) Integer minRowsAffected,
            @RequestParam(value = "rowsAffected", required = false) Integer maxRowsAffected,
            @RequestParam(value = "last", required = false) Long last,
            @RequestParam(value = "host", required = false) String host) {

        ISessionService service = new SessionService();
        JSONArray json = service.getSessionsWithRequest(status, maxBytesIn, minBytesIn,
                maxBytesOut, minBytesOut, minRowsAffected, maxRowsAffected, last, host);
        return _formJSONSuccessResponse(json.toString());
    }

    @RequestMapping(value = "/getHostsWithRequestFilter", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getHostsWithRequestFilter(HttpServletRequest request,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "maxBytesIn", required = false) Integer maxBytesIn,
            @RequestParam(value = "minBytesIn", required = false) Integer minBytesIn,
            @RequestParam(value = "maxBytesOut", required = false) Integer maxBytesOut,
            @RequestParam(value = "minBytesOut", required = false) Integer minBytesOut,
            @RequestParam(value = "rowsAffected", required = false) Integer minRowsAffected,
            @RequestParam(value = "rowsAffected", required = false) Integer maxRowsAffected,
            @RequestParam(value = "last", required = false) Long last,
            @RequestParam(value = "host", required = false) String host,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        HostService service = new HostService();
        JSONArray json = service.getHostsWithRequestFilter(status, maxBytesIn, minBytesIn,
                maxBytesOut, minBytesOut, minRowsAffected, maxRowsAffected, last, host, keyword,
                fromDate, toDate);
        return _formJSONSuccessResponse(json.toString());
    }

    @RequestMapping(value = "/getOverallSummaryWithFilter", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getOverallSummaryWithFilter(HttpServletRequest request,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "maxBytesIn", required = false) Integer maxBytesIn,
            @RequestParam(value = "minBytesIn", required = false) Integer minBytesIn,
            @RequestParam(value = "maxBytesOut", required = false) Integer maxBytesOut,
            @RequestParam(value = "minBytesOut", required = false) Integer minBytesOut,
            @RequestParam(value = "rowsAffected", required = false) Integer minRowsAffected,
            @RequestParam(value = "rowsAffected", required = false) Integer maxRowsAffected,
            @RequestParam(value = "last", required = false) Long last,
            @RequestParam(value = "host", required = false) String host) {

        IRequestService service = new RequestService();
        JSONObject json = service.getRequestSummary(status, maxBytesIn, minBytesIn, maxBytesOut,
                minBytesOut, minRowsAffected, maxRowsAffected, last, host);
        return _formJSONSuccessResponse(json.toString());
    }

    @RequestMapping(value = "/getRequestStatuses", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getRequestStatuses(
            @RequestParam(value = "window", defaultValue = "1", required = false) Integer window) {
        JSONArray result = new JSONArray();
        return _formJSONSuccessResponse(result.toString());
    }

    @RequestMapping(value = "/getHostInfo", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getHostInfo(@RequestParam(value = "id", required = true) String id) {
        IHostService service = new HostService();
        JSONObject json = service.getHostInfo(id);
        return _formJSONSuccessResponse(json.toString());
    }

    @RequestMapping(value = "/users", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getUsers() {

        IUserService service = new UserService();
        JSONArray json = service.getUsers();
        return _formJSONSuccessResponse(json.toString());
    }

    @RequestMapping(value = "/getRequests", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getRequests(
            @RequestParam(value = "timestamp", required = false) String timestamp) {
        IRequestService service = new RequestService();
        JSONArray json = service.getRequests(timestamp);
        return _formJSONSuccessResponse(json.toString());

    }

    @RequestMapping(value = "/domains", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getDomains() {

        IDomainService service = new DomainService();
        JSONArray json = service.getDomains();
        return _formJSONSuccessResponse(json.toString());

    }

    @RequestMapping(value = "/sessions", method = {RequestMethod.GET})
    public @ResponseBody
    ResponseEntity<String> getSessions(
            @RequestParam(value = "window", defaultValue = "5", required = false) Integer window) {

        ISessionService service = new SessionService();
        JSONArray json = service.getSessions(window);
        return _formJSONSuccessResponse(json.toString());
    }

    @RequestMapping(value = "/zipfileupload", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> zipFileUpload(MultipartHttpServletRequest request, HttpServletResponse response, 
           @RequestParam(value = "tagname", required = true) String tagname,
           @RequestParam(value = "custname", required = true) String custname,
           @RequestParam(value = "custcasenum", required = true) String custcasenum,
           @RequestParam(value = "loggendate", required = true) String loggendate)
            throws IOException, JSONException, ParseException {
        
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        
        if(tagname.compareToIgnoreCase("undefined") == 0){
            jo.put("response", "Tag Name is Required");
            return _formJSONSuccessResponse(ja.put(jo).toString());
        }
        Iterator<String> itr = request.getFileNames();
        MultipartFile file = null;
        try{
        file = request.getFile(itr.next());
        }catch(NoSuchElementException e){
            jo.put("response", "Browse the Zip File");
            return _formJSONSuccessResponse(ja.put(jo).toString());
        }
         
        if(file.getContentType().compareToIgnoreCase("application/x-zip-compressed") != 0){
            jo.put("response", "Only Zip Files are Accepted");
            return _formJSONSuccessResponse(ja.put(jo).toString());
        }
        
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Date date = (Date)formatter.parse(loggendate);
        SimpleDateFormat formatneeded=new SimpleDateFormat("YYYYMMdd");
        String formatedDate = formatneeded.format(date);
        
        String destination = AppConfig.getInstance().getString("zip.target.location");

        String output_folder = destination + custname +File.separator+custcasenum +File.separator+formatedDate +File.separator+tagname;

        byte[] bytes = null;

        byte[] buffer = new byte[1024];

        if (!file.isEmpty()) {
            bytes = file.getBytes();
            // store file in storage
        }
        InputStream inputStream = new ByteArrayInputStream(bytes);

        ZipInputStream zis = new ZipInputStream(inputStream);
        ZipEntry ze = zis.getNextEntry();
        
        if(ze.isDirectory()){
            jo.put("response", "Zip File Contains Unwanted Data");
            return _formJSONSuccessResponse(ja.put(jo).toString());
        }

        try {
            File folder = new File(output_folder);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            
            while (ze != null) {
                
                String fileName = ze.getName();
                File newFile = new File(output_folder + File.separator + fileName);

                System.out.println("file unzip : " + newFile.getAbsoluteFile());

                // create all non exists folders
                // else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            jo.put("response", "Target Directory Doesn't Exist");
            return _formJSONSuccessResponse(ja.put(jo).toString());
        }
        jo.put("response", "Unzipped Successfully");
        return _formJSONSuccessResponse(ja.put(jo).toString());
    }

    private ResponseEntity<String> _formJSONSuccessResponse(String data) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(data, httpHeaders, HttpStatus.OK);

    }
}