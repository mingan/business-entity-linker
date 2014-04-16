package cz.opendata.linked.business_entity.linker;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.*;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

@AsTransformer
public class BusinessEntityLinker extends ConfigurableBase<BusinessEntityLinkerConfig>
		implements 
		// If you do not want the dialog, delete the following line
		// 	and getConfigurationDialog function
		ConfigDialogProvider<BusinessEntityLinkerConfig>
	{
	
	@InputDataUnit
	public RDFDataUnit sourceData;

    @InputDataUnit
    public RDFDataUnit sourceDataTarget;
	
	@OutputDataUnit
	public RDFDataUnit goodLinks;

    @OutputDataUnit
    public RDFDataUnit probableLinks;
	
	public BusinessEntityLinker() {
		super(BusinessEntityLinkerConfig.class);
	}

	@Override
	public AbstractConfigDialog<BusinessEntityLinkerConfig> getConfigurationDialog() {
		return new BusinessEntityLinkerDialog();
	}

        // TODO 2: Implement the method execute being called when the DPU is launched
	@Override
	public void execute(DPUContext context)
			throws DPUException,
				DataUnitException {

        // extract data from data unit to file

        // build a linkage rule based on config

        // save the rule to file

        // run Silk

        // load results to output data units
	}
	
}
