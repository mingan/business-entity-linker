package cz.opendata.linked.business_entity.linker;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.rio.RDFFormat;

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BusinessEntityLinkerTest {

    public static final String OUTPUT_DIR = "w:\\dip\\dedup\\test-output\\";

    private BusinessEntityLinker linker;
    private BusinessEntityLinkerConfig config;
    private TestEnvironment env;
    private RDFDataUnit sourceData;
    private RDFDataUnit targetData;
    private RDFDataUnit goodLinks;
    private RDFDataUnit probableLinks;

    @Before
    public void prepare() {
        linker = new BusinessEntityLinker();
        config = new BusinessEntityLinkerConfig();
        env = TestEnvironment.create();

        config.setOrgSelectionA("gr:BusinessEntity");
        config.setOrgSelectionB("gr:BusinessEntity");
        config.setIdentSelectionA("schema:vatID");
        config.setIdentSelectionB("schema:vatID");
        config.setConfidenceCutoff(1.0);
        config.setSilkPath("C:\\Program Files\\Silk Workbench\\commandline\\silk.jar");
        config.setJavaMemory(1024);
    }

    @Test
    public void testMaxBlocking() throws Exception {
        int blocking = 1000;
        config.setBlocking(blocking);
        config.setBlocking(1000000000);
        assertThat(config.getBlocking(), is(blocking));
    }

    @Test
    public void testExact() throws Exception {
        sourceData = env.createRdfInputFromResource("Source dataset", false, "exact.ttl", RDFFormat.TURTLE);
        runTest("exact", 2, 0);
    }

    @Test
    public void testExactWithTwoDatasets() throws Exception {
        sourceData = env.createRdfInputFromResource("Source dataset", false, "exact.ttl", RDFFormat.TURTLE);
        targetData = env.createRdfInputFromResource("Target dataset", false, "exact2.ttl", RDFFormat.TURTLE);
        runTest("two exact", 2, 0);
    }

    private void runTest(String name, int exact, int probable) {
        try {
            linker.configureDirectly(config);
            goodLinks = env.createRdfOutput("Good links", false);
            probableLinks = env.createRdfOutput("Probable links", false);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            env.run(linker);

            printResultToFile(name);

            assertTrue(goodLinks.getTripleCount() == exact);
            assertTrue(probableLinks.getTripleCount() == probable);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            env.release();
        }
    }

    private void printResultToFile(String file) {
        try {
            Random rg = new Random();
            int no = rg.nextInt();
            goodLinks.loadToFile(OUTPUT_DIR + file + "-good-" + no + ".ttl", RDFFormatType.TTL);
            probableLinks.loadToFile(OUTPUT_DIR + file + "-prob-" + no + ".ttl", RDFFormatType.TTL);
        } catch (CannotOverwriteFileException e) {
            e.printStackTrace();
        } catch (RDFException e) {
            e.printStackTrace();
        }
    }
}