package cz.opendata.linked.business_entity.linker;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

public class RuleBuilderTest {

    private final String workingDirPath = "/path";
    private BusinessEntityLinkerConfig config;
    private RuleBuilder builder;

    @Before
    public void prepare() {
        config = new BusinessEntityLinkerConfig();
        config.setConfidenceCutoff(0.9);
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
