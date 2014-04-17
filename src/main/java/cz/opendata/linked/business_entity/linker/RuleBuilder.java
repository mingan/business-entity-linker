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
        buildDataSources();
        buildSourceDataset();
        buildTargetDataset();
        buildComparison();
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

    private void buildDataSources() {
        Element sourceA = (Element) rule.getElementsByTagName("DataSource").item(0);

        Element file = rule.createElement("Param");
        file.setAttribute("name", "file");
        file.setAttribute("value",  workingDirPath + File.separator + "source.nt");

        Element format = rule.createElement("Param");
        format.setAttribute("name", "format");
        format.setAttribute("value", "N-TRIPLE");

        sourceA.appendChild(file);
        sourceA.appendChild(format);

        if (config.getNumberOfSources() == 2) {
            Element sourceB = rule.createElement("DataSource");
            sourceB.setAttribute("id", "sourceB");
            sourceB.setAttribute("type", "file");

            file = rule.createElement("Param");
            file.setAttribute("name", "file");
            file.setAttribute("value",  workingDirPath + File.separator + "target.nt");

            format = rule.createElement("Param");
            format.setAttribute("name", "format");
            format.setAttribute("value", "N-TRIPLE");

            sourceB.appendChild(file);
            sourceB.appendChild(format);
            sourceA.getParentNode().appendChild(sourceB);
        }
    }

    private void buildSourceDataset() {
        Element dataset = (Element) rule.getElementsByTagName("SourceDataset").item(0);
        dataset.setAttribute("dataSource", "sourceA");
        Element restrictTo = rule.createElement("RestrictTo");
        restrictTo.setTextContent("?a rdf:type " + config.getOrgSelectionA());
        dataset.appendChild(restrictTo);
    }

    private void buildTargetDataset() {
        Element dataset = (Element) rule.getElementsByTagName("TargetDataset").item(0);
        String source = "sourceA";
        if (config.getNumberOfSources() == 2) {
            source = "sourceB";
        }
        dataset.setAttribute("dataSource", source);
        Element restrictTo = rule.createElement("RestrictTo");
        restrictTo.setTextContent("?b rdf:type " + config.getOrgSelectionB());
        dataset.appendChild(restrictTo);
    }

    private void buildComparison() {
        Element root = (Element) rule.getElementsByTagName("LinkageRule").item(0);
        if (config.getNumberOfSources() == 1) {
            root = buildIneqalityAggregation(root);
        }

        Element compare = rule.createElement("Compare");
        compare.setAttribute("required", "true");
        if (config.isExact()) {
            compare.setAttribute("metric", "equality");
            compare.setAttribute("threshold", "0.0");

            Element pathA = rule.createElement("Input");
            Element pathB = rule.createElement("Input");
            pathA.setAttribute("path", "?a/" + config.getIdentSelectionA());
            pathB.setAttribute("path", "?b/" + config.getIdentSelectionB());

            compare.appendChild(pathA);
            compare.appendChild(pathB);
        } else {
            compare.setAttribute("metric", "levenshtein");
            compare.setAttribute("threshold", config.getNameThreshold().toString());

            Element pathA = rule.createElement("Input");
            Element pathB = rule.createElement("Input");
            pathA.setAttribute("path", "?a/" + config.getNameSelectionA());
            pathB.setAttribute("path", "?b/" + config.getNameSelectionB());

            Element[] transformations = createNameTransformations();
            compare.appendChild(transformations[0]);
            transformations[0].appendChild(transformations[1]);
            transformations[1].appendChild(pathA);

            transformations = createNameTransformations();
            compare.appendChild(transformations[0]);
            transformations[0].appendChild(transformations[1]);
            transformations[1].appendChild(pathB);
        }
        root.appendChild(compare);
    }

    private Element[] createNameTransformations() {
        Element lowercase = rule.createElement("TransformInput");
        lowercase.setAttribute("function", "lowerCase");

        Element removeSpecialChars = rule.createElement("TransformInput");
        removeSpecialChars.setAttribute("function", "removeSpecialChars");

        return new Element[]{lowercase, removeSpecialChars};
    }

    private Element buildIneqalityAggregation(Element root) {
        Element aggregate = rule.createElement("Aggregate");
        aggregate.setAttribute("type", "min");

        Element compare = rule.createElement("Compare");
        compare.setAttribute("metric", "inequality");
        compare.setAttribute("threshold", "0.0");
        compare.setAttribute("required", "true");

        Element pathA = rule.createElement("Input");
        pathA.setAttribute("path", "?a");
        Element pathB = rule.createElement("Input");
        pathB.setAttribute("path", "?b");

        compare.appendChild(pathA);
        compare.appendChild(pathB);

        aggregate.appendChild(compare);
        root.appendChild(aggregate);

        return aggregate;

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
