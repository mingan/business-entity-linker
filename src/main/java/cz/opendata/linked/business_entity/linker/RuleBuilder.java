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
        buildBlocking();
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
        Element sources = (Element) rule.getElementsByTagName("DataSources").item(0);
        Element sourceA;
        if (config.isSparqlA()) {
            sourceA = createSparqlDataSource("sourceA", config.getSparqlAEndpoint(), config.getSparqlALogin(), config.getSparqlAPassword(), config.getSparqlAGraph());
        } else {
            sourceA = createFileDataSource("sourceA", "source.nt");
        }

        sources.appendChild(sourceA);

        if (config.getNumberOfSources() == 2) {
            Element sourceB;
            if (config.isSparqlB()) {
                sourceB = createSparqlDataSource("sourceB", config.getSparqlBEndpoint(), config.getSparqlBLogin(), config.getSparqlBPassword(), config.getSparqlBGraph());
            } else {
                sourceB = createFileDataSource("sourceB", "target.nt");
            }
            sources.appendChild(sourceB);
        }
    }

    private Element createFileDataSource(String id, String filename) {
        Element source = rule.createElement("DataSource");
        source.setAttribute("id", id);
        source.setAttribute("type", "file");

        Element file = rule.createElement("Param");
        file.setAttribute("name", "file");
        file.setAttribute("value",  workingDirPath + File.separator + filename);

        Element format = rule.createElement("Param");
        format.setAttribute("name", "format");
        format.setAttribute("value", "N-TRIPLE");

        source.appendChild(file);
        source.appendChild(format);

        return source;
    }

    private Element createSparqlDataSource(String id, String endpoint, String login, String pass, String graph) {
        Element source = rule.createElement("DataSource");
        source.setAttribute("id", id);
        source.setAttribute("type", "sparqlEndpoint");
        
        Element endpointParam = rule.createElement("Param");
        endpointParam.setAttribute("name", "endpointURI");
        endpointParam.setAttribute("value", endpoint);
        source.appendChild(endpointParam);

        if (login != null && !login.trim().equals("")) {
            Element loginParam = rule.createElement("Param");
            loginParam.setAttribute("name", "login");
            loginParam.setAttribute("value", login);
            source.appendChild(loginParam);
        }

        if (pass != null && !pass.trim().equals("")) {
            Element passParam = rule.createElement("Param");
            passParam.setAttribute("name", "password");
            passParam.setAttribute("value", pass);
            source.appendChild(passParam);
        }

        if (graph != null && !graph.trim().equals("")) {
            Element graphParam = rule.createElement("Param");
            graphParam.setAttribute("name", "graph");
            graphParam.setAttribute("value", graph);
            source.appendChild(graphParam);
        }

        return source;
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

    private void buildBlocking() {
        if (config.getBlocking() > 0) {
            Element blocking = (Element) rule.getElementsByTagName("Blocking").item(0);

            blocking.setAttribute("enabled", "true");
            blocking.setAttribute("blocks", String.valueOf(config.getBlocking()));
        }
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
