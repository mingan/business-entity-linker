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
        identSelectionOptions.add("/adms:identifier/skos:notation");
        identSelectionOptions.add("/schema:vatID");
        identSelectionOptions.add("/schema:taxID");
        identSelectionOptions.add("/gr:vatID");
        identSelectionOptions.add("/gr:taxID");
    }

    private String orgSelectionA;
    private String orgSelectionB;

    private String identSelectionA;
    private String identSelectionB;
    private Double confidenceCutoff;

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
}
