package cz.opendata.linked.business_entity.linker;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RuleBuilderTest {

    private final String workingDirPath = "/path";
    private final String orgSelectionA = "schema:Organization";
    private final String orgSelectionB = "gr:BusinessEntity";
    private BusinessEntityLinkerConfig config;
    private RuleBuilder builder;

    @Before
    public void prepare() {
        config = new BusinessEntityLinkerConfig();
        config.setConfidenceCutoff(0.9);
        config.setOrgSelectionA(orgSelectionA);
        config.setOrgSelectionB(orgSelectionB);
        builder = new RuleBuilder(config, workingDirPath);
    }

    @Test
    public void testLoadTemplate() {
        assertTrue(builder.getRule() != null);
    }

    @Test
    public void testAppendSchemas() throws Exception {
        assertTrue(builder.getRule().getElementsByTagName("Prefix").getLength() == Schemas.getSchemas().size());
    }

    @Test
    public void testBuildOutputs() throws Exception {
        NodeList outputs = builder.getRule().getElementsByTagName("Output");
        assertTrue(outputs.getLength() == 2);
    }

    @Test
    public void testSourceDataset() throws Exception {
        Node source = builder.getRule().getElementsByTagName("SourceDataset").item(0);
        assertTrue(source != null);
        NamedNodeMap attrs = source.getAttributes();
        assertTrue(attrs != null);
        assertThat("sourceA", is(attrs.getNamedItem("dataSource").getTextContent()));
        Node restrict = source.getFirstChild();
        assertTrue(restrict != null);
        assertThat("?a rdf:type " + orgSelectionA, is(source.getTextContent().trim()));
    }

    @Test
    public void testTargetDataset() throws Exception {
        Node source = builder.getRule().getElementsByTagName("TargetDataset").item(0);
        assertTrue(source != null);
        NamedNodeMap attrs = source.getAttributes();
        assertTrue(attrs != null);
        assertThat("sourceA", is(attrs.getNamedItem("dataSource").getTextContent()));
        Node restrict = source.getFirstChild();
        assertTrue(restrict != null);
        assertThat("?b rdf:type " + orgSelectionB, is(source.getTextContent().trim()));
    }

    private void printXml(Document doc) {
        StringWriter w = new StringWriter();
        StreamResult r = new StreamResult(w);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            transformer.transform(new DOMSource(doc), r);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        System.out.print(w.toString());

    }
}
