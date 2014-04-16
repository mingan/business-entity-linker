package cz.opendata.linked.business_entity.linker;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RuleBuilderTest {

    private final String workingDirPath = "/path";
    private final String orgSelectionA = "schema:Organization";
    private final String orgSelectionB = "gr:BusinessEntity";
    private final String identSelectionA = "/adms:identifier/skos:notation";
    private final String identSelectionB = "/gr:vatID";
    private BusinessEntityLinkerConfig config;
    private RuleBuilder builder;

    @Before
    public void prepare() {
        config = new BusinessEntityLinkerConfig();
        config.setConfidenceCutoff(0.9);
        config.setOrgSelectionA(orgSelectionA);
        config.setOrgSelectionB(orgSelectionB);
        config.setIdentSelectionA(identSelectionA);
        config.setIdentSelectionB(identSelectionB);
    }

    @Test
    public void testLoadTemplate() {
        builder = new RuleBuilder(config, workingDirPath);
        assertTrue(builder.getRule() != null);
    }

    @Test
    public void testAppendSchemas() throws Exception {
        builder = new RuleBuilder(config, workingDirPath);
        assertTrue(builder.getRule().getElementsByTagName("Prefix").getLength() == Schemas.getSchemas().size());
    }

    @Test
    public void testOneDataSource() throws Exception {
        builder = new RuleBuilder(config, workingDirPath);
        assertTrue(builder.getRule().getElementsByTagName("DataSource").getLength() == 1);
    }

    @Test
    public void testTwoDataSources() throws Exception {
        config.setNumberOfSources(2);
        builder = new RuleBuilder(config, workingDirPath);
        assertTrue(builder.getRule().getElementsByTagName("DataSource").getLength() == 2);
        String idA = ((Element) builder.getRule().getElementsByTagName("DataSource").item(0)).getAttribute("id");
        String idB = ((Element) builder.getRule().getElementsByTagName("DataSource").item(1)).getAttribute("id");
        assertThat(idA, not(is(idB)));
        printXml(builder.getRule());
    }

    @Test
    public void testBuildOutputs() throws Exception {
        builder = new RuleBuilder(config, workingDirPath);
        NodeList outputs = builder.getRule().getElementsByTagName("Output");
        assertTrue(outputs.getLength() == 2);
    }

    @Test
    public void testSourceDataset() throws Exception {
        builder = new RuleBuilder(config, workingDirPath);
        Node source = builder.getRule().getElementsByTagName("SourceDataset").item(0);
        assertTrue(source != null);
        NamedNodeMap attrs = source.getAttributes();
        assertTrue(attrs != null);
        assertThat("sourceA", is(attrs.getNamedItem("dataSource").getTextContent()));
        Node restrict = source.getFirstChild();
        assertTrue(restrict != null);
        assertThat(restrict.getNodeName(), is("RestrictTo"));
        assertThat("?a rdf:type " + orgSelectionA, is(restrict.getTextContent().trim()));
    }

    @Test
    public void testTargetDatasetWithOneSource() throws Exception {
        builder = new RuleBuilder(config, workingDirPath);
        Node source = builder.getRule().getElementsByTagName("TargetDataset").item(0);
        assertTrue(source != null);
        NamedNodeMap attrs = source.getAttributes();
        assertTrue(attrs != null);
        assertThat("sourceA", is(attrs.getNamedItem("dataSource").getTextContent()));
        Node restrict = source.getFirstChild();
        assertTrue(restrict != null);
        assertThat(restrict.getNodeName(), is("RestrictTo"));
        assertThat("?b rdf:type " + orgSelectionB, is(restrict.getTextContent().trim()));
    }

    @Test
    public void testTargetDatasetWithTwoSources() throws Exception {
        config.setNumberOfSources(2);
        builder = new RuleBuilder(config, workingDirPath);
        Node source = builder.getRule().getElementsByTagName("TargetDataset").item(0);
        assertTrue(source != null);
        NamedNodeMap attrs = source.getAttributes();
        assertTrue(attrs != null);
        assertThat("sourceB", is(attrs.getNamedItem("dataSource").getTextContent()));
        Node restrict = source.getFirstChild();
        assertTrue(restrict != null);
        assertThat(restrict.getNodeName(), is("RestrictTo"));
        assertThat("?b rdf:type " + orgSelectionB, is(restrict.getTextContent().trim()));
    }

    @Test
    public void testIdentComparison() throws Exception {
        builder = new RuleBuilder(config, workingDirPath);
        Node linkageRule = builder.getRule().getElementsByTagName("LinkageRule").item(0);
        NodeList components = linkageRule.getChildNodes();
        assertThat(components.getLength(), is(1));

        Node compare = components.item(0);
        assertThat(compare.getNodeName(), is("Compare"));
        assertThat(((Element) compare).getAttribute("metric"), is("equality"));

        assertThat(compare.getChildNodes().getLength(), is(2));
        assertThat(((Element) compare.getFirstChild()).getAttribute("path"), containsString(identSelectionA));
        assertThat(((Element) compare.getLastChild()).getAttribute("path"), containsString(identSelectionB));
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
