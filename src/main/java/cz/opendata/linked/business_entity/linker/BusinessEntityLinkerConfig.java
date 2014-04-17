package cz.opendata.linked.business_entity.linker;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

import java.util.LinkedList;
import java.util.List;

public class BusinessEntityLinkerConfig extends DPUConfigObjectBase {

    private static final List<String> orgSelectionOptions = new LinkedList<>();
    static {
        orgSelectionOptions.add("schema:Organization");
        orgSelectionOptions.add("gr:BusinessEntity");
        orgSelectionOptions.add("org:Organization");
    }

    private static final List<String> identSelectionOptions = new LinkedList<>();
    static {
        identSelectionOptions.add("adms:identifier/skos:notation");
        identSelectionOptions.add("schema:vatID");
        identSelectionOptions.add("schema:taxID");
        identSelectionOptions.add("gr:vatID");
        identSelectionOptions.add("gr:taxID");
    }

    private static final List<String> nameSelectionOptions = new LinkedList<>();
    static {
        nameSelectionOptions.add("schema:name");
        nameSelectionOptions.add("gr:legalName");
        nameSelectionOptions.add("gr:name");
        nameSelectionOptions.add("dcterms:title");
        nameSelectionOptions.add("rdfs:label");
    }

    private String orgSelectionA = orgSelectionOptions.get(0);
    private String orgSelectionB = orgSelectionOptions.get(0);

    private String identSelectionA = identSelectionOptions.get(0);
    private String identSelectionB = identSelectionOptions.get(0);

    private String nameSelectionA = nameSelectionOptions.get(0);
    private String nameSelectionB = nameSelectionOptions.get(0);

    private Double nameThreshold = 0.75;

    private Double confidenceCutoff = 1.0;
    private int numberOfSources = 1;
    private boolean exact = true;

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
}
