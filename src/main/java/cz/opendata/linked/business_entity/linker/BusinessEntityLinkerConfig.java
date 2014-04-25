package cz.opendata.linked.business_entity.linker;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

public class BusinessEntityLinkerConfig extends DPUConfigObjectBase {

    private boolean sparqlA = false;
    private boolean sparqlB = false;
    
    private String sparqlAEndpoint = "";
    private String sparqlALogin = "";
    private String sparqlAPassword = "";
    private String sparqlAGraph = "";
    private String sparqlBEndpoint = "";
    private String sparqlBLogin = "";
    private String sparqlBPassword = "";
    private String sparqlBGraph = "";
    
    private String orgSelectionA = OptionsLists.organization.get(0);
    private String orgSelectionB = OptionsLists.organization.get(0);

    private String identSelectionA = OptionsLists.ident.get(0);
    private String identSelectionB = OptionsLists.ident.get(0);

    private String nameSelectionA = OptionsLists.name.get(0);
    private String nameSelectionB = OptionsLists.name.get(0);

    private Double nameThreshold = 0.75;

    private String metric = OptionsLists.metric.get(0);

    private Double confidenceCutoff = 0.9;
    private int numberOfSources = 1;
    private boolean exact = true;
    private int blocking = 1000;

    public static final int BLOCKING_TOP_LIMIT = 65535;
    public static final int BLOCKING_BOTTOM_LIMIT = 0;

    private String silkPath = "";
    private int javaMemory = 2048;

    public BusinessEntityLinkerConfig() {
    }

    public String getOrgSelectionA() {
        return orgSelectionA;
    }

    public void setOrgSelectionA(String orgSelectionA) {
        this.orgSelectionA = orgSelectionA;
    }

    public String getOrgSelectionB() {
        return orgSelectionB;
    }

    public void setOrgSelectionB(String orgSelectionB) {
        this.orgSelectionB = orgSelectionB;
    }

    public String getIdentSelectionA() {
        return identSelectionA;
    }

    public void setIdentSelectionA(String identSelectionA) {
        this.identSelectionA = identSelectionA;
    }

    public String getIdentSelectionB() {
        return identSelectionB;
    }

    public void setIdentSelectionB(String identSelectionB) {
        this.identSelectionB = identSelectionB;
    }

    public Double getConfidenceCutoff() {
        return confidenceCutoff;
    }

    public void setConfidenceCutoff(double confidenceCutoff) {
        this.confidenceCutoff = confidenceCutoff;
    }

    public int getNumberOfSources() {
        return numberOfSources;
    }

    public void setNumberOfSources(int numberOfSources) {
        this.numberOfSources = numberOfSources;
    }

    public void setSelfLink(boolean selfLink) {
        if (selfLink) {
            setNumberOfSources(1);
        } else {
            setNumberOfSources(2);
        }
    }

    public String getNameSelectionA() {
        return nameSelectionA;
    }

    public void setNameSelectionA(String nameSelectionA) {
        this.nameSelectionA = nameSelectionA;
    }

    public String getNameSelectionB() {
        return nameSelectionB;
    }

    public void setNameSelectionB(String nameSelectionB) {
        this.nameSelectionB = nameSelectionB;
    }

    public boolean isExact() {
        return exact;
    }

    public void setExact(boolean exact) {
        this.exact = exact;
    }

    public Double getNameThreshold() {
        return nameThreshold;
    }

    public void setNameThreshold(double nameThreshold) {
        this.nameThreshold = nameThreshold;
    }

    public int getBlocking() {
        return blocking;
    }

    public void setBlocking(int blocking) {
        if (BLOCKING_BOTTOM_LIMIT <= blocking && blocking <= BLOCKING_TOP_LIMIT) {
            this.blocking = blocking;
        }
    }

    public boolean isSparqlA() {
        return sparqlA;
    }

    public void setSparqlA(boolean sparqlA) {
        this.sparqlA = sparqlA;
    }

    public boolean isSparqlB() {
        return sparqlB;
    }

    public void setSparqlB(boolean sparqlB) {
        this.sparqlB = sparqlB;
    }

    public String getSparqlAEndpoint() {
        return sparqlAEndpoint;
    }

    public void setSparqlAEndpoint(String sparqlAEndpoint) {
        this.sparqlAEndpoint = sparqlAEndpoint;
    }

    public String getSparqlALogin() {
        return sparqlALogin;
    }

    public void setSparqlALogin(String sparqlALogin) {
        this.sparqlALogin = sparqlALogin;
    }

    public String getSparqlAPassword() {
        return sparqlAPassword;
    }

    public void setSparqlAPassword(String sparqlAPassword) {
        this.sparqlAPassword = sparqlAPassword;
    }

    public String getSparqlAGraph() {
        return sparqlAGraph;
    }

    public void setSparqlAGraph(String sparqlAGraph) {
        this.sparqlAGraph = sparqlAGraph;
    }

    public String getSparqlBEndpoint() {
        return sparqlBEndpoint;
    }

    public void setSparqlBEndpoint(String sparqlBEndpoint) {
        this.sparqlBEndpoint = sparqlBEndpoint;
    }

    public String getSparqlBLogin() {
        return sparqlBLogin;
    }

    public void setSparqlBLogin(String sparqlBLogin) {
        this.sparqlBLogin = sparqlBLogin;
    }

    public String getSparqlBPassword() {
        return sparqlBPassword;
    }

    public void setSparqlBPassword(String sparqlBPassword) {
        this.sparqlBPassword = sparqlBPassword;
    }

    public String getSparqlBGraph() {
        return sparqlBGraph;
    }

    public void setSparqlBGraph(String sparqlBGraph) {
        this.sparqlBGraph = sparqlBGraph;
    }

    public int getJavaMemory() {
        return javaMemory;
    }

    public void setJavaMemory(int javaMemory) {
        this.javaMemory = javaMemory;
    }

    public String getSilkPath() {
        return silkPath;
    }

    public void setSilkPath(String silkPath) {
        this.silkPath = silkPath;
    }

    public static boolean isBLOCKING_BOTTOM_LIMIT() {
        return true;
    }

    public static boolean isBLOCKING_TOP_LIMIT() {
        return true;
    }


    public static void setBLOCKING_BOTTOM_LIMIT() {
    }

    public static void setBLOCKING_TOP_LIMIT() {
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }
}
