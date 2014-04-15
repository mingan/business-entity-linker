package cz.opendata.linked.business_entity.linker;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 * Put your DPU's configuration here.
 * 
 * You can optionally implement {@link #isValid()} to provide possibility
 * to validate the configuration.
 * 
 * <b>This class must have default (parameter less) constructor!</b>
 */
public class BusinessEntityLinkerConfig extends DPUConfigObjectBase {

    private int width;
    
    private int height;	
	
	// BusinessEntityLinkerConfig must provide public non-parametric constructor
    public BusinessEntityLinkerConfig() {
        width = 100;
        height = 100;
    }
    
    public BusinessEntityLinkerConfig(int w, int h) {
        width = w;
        height = h;
    }
        
    public int getWidth() {
        return width;    
    }
    
     public int getHeight() {
        return height;    
    }

}
