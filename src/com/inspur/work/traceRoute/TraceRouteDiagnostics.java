
package com.inspur.work.traceRoute;

public class TraceRouteDiagnostics {

    private String DiagnosticsState;
    private String ResponseTime;
    private int NumberOfRouteHops;
    private String[] RouteHops = null;

    public String getDiagnosticsState() {
        return DiagnosticsState;
    }

    public void setDiagnosticsState(String diagnosticsState) {
        DiagnosticsState = diagnosticsState;
    }

    public String getResponseTime() {
        return ResponseTime;
    }

    public void setResponseTime(String responseTime) {
        ResponseTime = responseTime;
    }

    public int getNumberOfRouteHops() {
        return NumberOfRouteHops;
    }

    public void setNumberOfRouteHops(int numberOfRouteHops) {
        NumberOfRouteHops = numberOfRouteHops;
    }

    public String[] getRouteHops() {
        return RouteHops;
    }

    public void setRouteHops(String[] routeHops) {
        RouteHops = routeHops;
    }

}
