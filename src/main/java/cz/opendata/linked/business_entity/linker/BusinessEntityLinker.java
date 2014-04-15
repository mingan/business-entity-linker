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
	public RDFDataUnit rdfInput;
	
	@OutputDataUnit
	public RDFDataUnit rdfOutput;
	
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
		
		// DPU's configuration is accessible under 'this.config' 
                // DPU's context is accessible under 'context'
                // DPU's data units are accessible under 'rdfInput' and 'rdfOutput'
	}
	
}
