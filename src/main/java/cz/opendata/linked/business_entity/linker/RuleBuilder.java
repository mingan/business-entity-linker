package cz.opendata.linked.business_entity.linker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class RuleBuilder {

    private static final Logger log = LoggerFactory.getLogger(RuleBuilder.class);
    private final Document rule;
    private final String workingDirPath;

    private BusinessEntityLinkerConfig config;

    public RuleBuilder(BusinessEntityLinkerConfig config, String workingDirPath) {
        this.config = config;
        this.workingDirPath = workingDirPath;
        this.rule = loadTemplate();
        addPrefixes();
        buildSourceDataset();
        buildTargetDataset();
        buildOutputs();
    }

    private Document loadTemplate() {
        InputStream templateUrl = getClass().getResourceAsStream("/rule/rule_template.xml");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(templateUrl);
        } catch (SAXException e) {
            log.error("Failed to parse linkage rule template");
        } catch (IOException e) {
            log.error("Failed to load linkage rule template");
        } catch (ParserConfigurationException e) {
            log.error("Failed to read");
        }
        return null;
    }

    private void addPrefixes() {
        Node prefixes = rule.getElementsByTagName("Prefixes").item(0);
        for (Map.Entry<String, String> schema : Schemas.getSchemas().entrySet()) {
            Element s = rule.createElement("Prefix");
            s.setAttribute("id", schema.getKey());
            s.setAttribute("namespace", schema.getValue());
            prefixes.appendChild(s);
        }
    }

    private void buildSourceDataset() {
        Element dataset = (Element) rule.getElementsByTagName("SourceDataset").item(0);
        dataset.setAttribute("dataSource", "sourceA");
        dataset.setTextContent("?a rdf:type " + config.getOrgSelectionA());
    }

    private void buildTargetDataset() {
        Element dataset = (Element) rule.getElementsByTagName("TargetDataset").item(0);
        dataset.setAttribute("dataSource", "sourceA");
        dataset.setTextContent("?b rdf:type " + config.getOrgSelectionB());
    }

    private void buildOutputs() {
        Node outputs = rule.getElementsByTagName("Outputs").item(0);


        Element format = rule.createElement("Param");
        format.setAttribute("name", "format");
        format.setAttribute("value", "ntriples");

        Element confirmedOutput = rule.createElement("Param");
        confirmedOutput.setAttribute("name", "file");
        confirmedOutput.setAttribute("value", workingDirPath + File.separator + "confirmed.n3");

        Element confirmed = rule.createElement("Output");
        confirmed.setAttribute("type", "file");
        confirmed.setAttribute("minConfidence", config.getConfidenceCutoff().toString());
        confirmed.appendChild(format);
        confirmed.appendChild(confirmedOutput);
        outputs.appendChild(confirmed);


        format = rule.createElement("Param");
        format.setAttribute("name", "format");
        format.setAttribute("value", "ntriples");

        Element verifyOutput = rule.createElement("Param");
        verifyOutput.setAttribute("name", "file");
        verifyOutput.setAttribute("value", workingDirPath + File.separator + "verify.n3");

        Element verify = rule.createElement("Output");
        verify.setAttribute("type", "file");
        verify.setAttribute("maxConfidence", config.getConfidenceCutoff().toString());
        verify.appendChild(format);
        verify.appendChild(verifyOutput);

        outputs.appendChild(verify);
    }

    public Document getRule() {
        return rule;
    }
}
