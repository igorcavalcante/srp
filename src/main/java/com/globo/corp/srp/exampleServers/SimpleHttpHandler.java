package com.globo.corp.srp.exampleServers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class SimpleHttpHandler extends AbstractHandler {

    private String body;

    public SimpleHttpHandler(String body ) {
        this.body = body;
    }

    public void handle( String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();
        if (body != null) {
            out.println(body);
        } else {
            out.println("<b1>Default body</b1>");
        }

        baseRequest.setHandled(true);
    }
}
