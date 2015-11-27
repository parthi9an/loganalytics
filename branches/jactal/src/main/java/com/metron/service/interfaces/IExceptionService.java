package com.metron.service.interfaces;

import org.json.JSONObject;

public interface IExceptionService {

    JSONObject getExceptions(String keyword, String hostId, String fromDate, String toDate);

    JSONObject getExceptionElements(String exceptionId);

    JSONObject getExceptionsGraph(String hostId, String fromDate, String toDate);

    JSONObject search(String keyword, String hostId, String fromDate, String toDate);

}
