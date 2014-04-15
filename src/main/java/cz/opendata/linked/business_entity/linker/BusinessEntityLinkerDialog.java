package cz.opendata.linked.business_entity.linker;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class BusinessEntityLinkerDialog extends BaseConfigDialog<BusinessEntityLinkerConfig> {

	public BusinessEntityLinkerDialog() {
		super(BusinessEntityLinkerConfig.class);
	}

	@Override
	public void setConfiguration(BusinessEntityLinkerConfig conf) throws ConfigException {
		// TODO : load configuration from function parameter into dialog
	}

	@Override
	public BusinessEntityLinkerConfig getConfiguration() throws ConfigException {
		// TODO : gather information from dialog and store them into configuration, then return it
		return null;
	}

}
