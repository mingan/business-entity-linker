package cz.opendata.linked.business_entity.linker;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

public class BusinessEntityLinkerConfig extends DPUConfigObjectBase {



    private String orgSelectionA = OptionsLists.organiztaion.get(0);
    private String orgSelectionB = OptionsLists.organiztaion.get(0);

    private String identSelectionA = OptionsLists.ident.get(0);
    private String identSelectionB = OptionsLists.ident.get(0);

    private String nameSelectionA = OptionsLists.name.get(0);
    private String nameSelectionB = OptionsLists.name.get(0);

    private Double nameThreshold = 0.75;

    private Double confidenceCutoff = 1.0;
    private int numberOfSources = 1;
    private boolean exact = true;
    private int blocking = 1000;

    static final int blockingTopLimit = 65535;
    static final int blockingBottomLimit = 0;

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

    public int getBlocking() {
        return blocking;
    }

    public void setBlocking(int blocking) {
        if (blockingBottomLimit <= blocking && blocking <= blockingTopLimit) {
            this.blocking = blocking;
        }
    }
}
