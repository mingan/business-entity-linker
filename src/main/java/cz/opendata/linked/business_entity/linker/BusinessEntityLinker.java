package cz.opendata.linked.business_entity.linker;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsTransformer;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import de.fuberlin.wiwiss.silk.Silk;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

@AsTransformer
public class BusinessEntityLinker extends ConfigurableBase<BusinessEntityLinkerConfig>
		implements ConfigDialogProvider<BusinessEntityLinkerConfig> {

    private static final Logger log = LoggerFactory.getLogger(BusinessEntityLinker.class);
	
	@InputDataUnit(name = "Source dataset")
	public RDFDataUnit sourceData;

    @InputDataUnit(name = "Target dataset", optional = true)
    public RDFDataUnit targetData;
	
	@OutputDataUnit(name = "Good links")
	public RDFDataUnit goodLinks;

    @OutputDataUnit(name = "Probable links")
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

        String workingDirPath = context.getWorkingDir().getPath();
        sourceData.loadToFile(new File(workingDirPath + File.separator + "source.nt"), RDFFormatType.NT);
        config.setNumberOfSources(1);

        if (targetData != null) {
            targetData.loadToFile(new File(workingDirPath + File.separator + "target.nt"), RDFFormatType.NT);
            config.setNumberOfSources(2);
        }
        // extract data from data unit to file

        // build a linkage rule based on config
        RuleBuilder builder = new RuleBuilder(config, workingDirPath);

        // save the rule to file
        String path = getPathToConfig(context);
        if (path == null) {
            context.sendMessage(MessageType.TERMINATION_REQUEST, "Failed to write Silk configuration");
        } else {
            writeSilkConfigToFile(builder.getRule(), path);
        }

        // run Silk
        Silk.executeFile(new File(path), null, 1, true);

        // load results to output data units
        File confirmed = new File(workingDirPath + File.separator + "confirmed.n3");
        goodLinks.addFromFile(confirmed, RDFFormat.NTRIPLES);

        File verify = new File(workingDirPath + File.separator + "verify.n3");
        probableLinks.addFromFile(verify, RDFFormat.NTRIPLES);
	}

        private String getPathToConfig(DPUContext context) {
            String path = null;
            try {
                path = context.getWorkingDir().getCanonicalPath() + "/silk_config.xml";
            } catch (IOException e) {
                log.error("Failed to access Silk configuration file for write");
            }
            return path;
        }

        private void writeSilkConfigToFile(Document rule, String path) {
        try {
            File configFile = new File(path);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(rule);
            StreamResult result = new StreamResult(configFile);
            transformer.transform(source, result);

            StringWriter w = new StringWriter();
            StreamResult r = new StreamResult(w);
            transformer.transform(source, r);
            log.debug("Generated config for inspection" + w.toString());
        } catch (TransformerConfigurationException e) {
            log.error("Failed to transform generated configuration to file");
        } catch (TransformerException e) {
            log.error("Failed to transform generated configuration to file");
        }
    }

}
