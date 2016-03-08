package com.metron.filter;

/**
 @author satheesh
 */

import java.io.IOException;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.metron.model.AccessToken;

@Component
public class CorsFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        
        //check authenticity of request
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        boolean responseFilter = false;
        String accessToken = request.getHeader("Access-Token");
        
        // For requests other than /authenticate access-token is required
        if(! request.getRequestURI().contains("/authenticate")){           
            if(accessToken == null){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }else{
                byte[] valueDecoded = Base64.getDecoder().decode(accessToken.getBytes());
                System.out.println("Decoded value is " + new String(valueDecoded));
                String currUserName = new String(valueDecoded).split(":")[0];
                //Check whether access-token is valid or not
                if (new AccessToken().isValidToken(currUserName,accessToken)){
                    responseFilter = true;
                }else{
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }   
        }else{
           responseFilter = true;            
        }
          
        // CORS "pre-flight" request
        //HttpServletResponse response = (HttpServletResponse) res;
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type,Access-Token");
        response.addHeader("Access-Control-Expose-Headers", "Access-Token");
        response.addHeader("Access-Control-Max-Age", "1800");// 30 min
        if(responseFilter){
            filterChain.doFilter(req, res);
        }

    }

    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }

    public void destroy() {
        
    }

}