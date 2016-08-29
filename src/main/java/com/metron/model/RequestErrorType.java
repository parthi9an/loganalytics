package com.metron.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.metron.orientdb.OrientDBGraphManager;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author satheesh
 */

public class RequestErrorType {

    private Map<String, String> errTypes = new HashMap<String, String>();

    private static RequestErrorType _instance = null;

    public static RequestErrorType getInstance() {
        if (_instance == null) {
            _instance = new RequestErrorType();
        }
        return _instance;
    }

    public RequestErrorType() {
        errTypes.put(".*User has .* privileges .*", "USER privileges ERROR");
        errTypes.put(".*System\\.(.*)Exception", "Exception");
    }

    public List<Error> findErrorType(String message) {
        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
        //
        // strip out all "" and form the RegExp for the Item
        Error error;
        HashMap<String, Object> props;
        String value = message.replaceAll("\".*\"", "\"XXX\"");
        value = value.replaceAll("\\[.*\\]", "\\[XXX\\]");
        value = value.replaceAll("\'.*\'", "\"XXX\"");

        List<Error> errorTypes = new ArrayList<Error>();

        // check if error matches any on the errortypes which is predefined
        for (String key : errTypes.keySet()) {
            // Create a Pattern object
            Pattern r = Pattern.compile(key);

            // Now create matcher object.
            Matcher m = r.matcher(value);
            if (m.find()) {
                String errorValue = this.errTypes.get(key);
                if (m.group(1) == null) {
                    errorValue = m.group(1)+this.errTypes.get(key);
                }
                error = new Error(errorValue, graph);
                props = new HashMap<String, Object>();
                props.put("value", errorValue);
                error.setProperties(props);
                error.save();
                errorTypes.add(error);
                //errorTypes.add(this.getErrorType(errorValue, graph));

            } else {
                System.out.println("NO MATCH");
            }
        }

        // if error does not match the prdefined error types create a custom
        // with the error message

        if (errorTypes.size() == 0) {
            error = new Error(value, graph);
            props = new HashMap<String, Object>();
            props.put("value", value);
            error.setProperties(props);
            error.save();
            errorTypes.add(error);
        }

        return errorTypes;
    }

    public OrientVertex createErrorType(String errorTypeString) {
        // create the Error type and save it in DB
        return null;
    }

}
