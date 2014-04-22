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
import java.io.*;

@AsTransformer
public class BusinessEntityLinker extends ConfigurableBase<BusinessEntityLinkerConfig>
		implements ConfigDialogProvider<BusinessEntityLinkerConfig> {

    private static final Logger log = LoggerFactory.getLogger(BusinessEntityLinker.class);
	
	@InputDataUnit(name = "source", optional = true)
	public RDFDataUnit sourceData;

    @InputDataUnit(name = "target", optional = true)
    public RDFDataUnit targetData;
	
	@OutputDataUnit(name = "good")
	public RDFDataUnit goodLinks;

    @OutputDataUnit(name = "probable")
    public RDFDataUnit probableLinks;

    public BusinessEntityLinker() {
		super(BusinessEntityLinkerConfig.class);
	}

	@Override
	public AbstractConfigDialog<BusinessEntityLinkerConfig> getConfigurationDialog() {
        config.setNumberOfSources(1);
        if (targetData != null) {
            config.setNumberOfSources(2);
        }
        return new BusinessEntityLinkerDialog();
    }

	@Override
	public void execute(DPUContext context)
			throws DPUException,
				DataUnitException {

        String workingDirPath = context.getWorkingDir().getPath();
        if (sourceData != null) {
            sourceData.loadToFile(new File(workingDirPath + File.separator + "source.nt"), RDFFormatType.NT);
        }

        if (targetData != null) {
            targetData.loadToFile(new File(workingDirPath + File.separator + "target.nt"), RDFFormatType.NT);
            config.setNumberOfSources(2);
        }
        config.setNumberOfSources(2);
        // build a linkage rule based on config
        RuleBuilder builder = new RuleBuilder(config, workingDirPath);

        // save the rule to file
        String path = getPathToConfig(context);
        if (path == null) {
            context.sendMessage(MessageType.TERMINATION_REQUEST, "Failed to write Silk configuration");
        } else {
            writeSilkConfigToFile(builder.getRule(), path);
        }

        String memoryOption = "";
        if (config.getJavaMemory() > 0) {
            memoryOption = "-Xmx" + config.getJavaMemory() + "m";
        }
        try {
            String command = "java -DconfigFile=\"" + path + "\" " + memoryOption + " -jar \"" + config.getSilkPath() + "\"";
            Process proc = Runtime.getRuntime().exec(command);
            printProcessOutput(proc);
            proc.waitFor();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            context.sendMessage(MessageType.ERROR, "Problem executing Silk: " + e.getMessage());
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage());
            context.sendMessage(MessageType.ERROR, "Execution of Silk was interupted. " + e.getMessage());
        }
        log.info("Silk finished");


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

    private static void printProcessOutput(Process process) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errors = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                errors.append(line);
            }
            log.warn(errors.toString());
            in.close();

            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder notes = new StringBuilder();

            while ((line = in.readLine()) != null) {
                notes.append(line);
            }
            log.debug(notes.toString());
            in.close();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }

}
