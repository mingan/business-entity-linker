package cz.opendata.linked.business_entity.linker;

import com.vaadin.ui.*;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

import java.util.Iterator;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class BusinessEntityLinkerDialog extends BaseConfigDialog<BusinessEntityLinkerConfig> {

    private GridLayout mainLayout;
    private CheckBox checkboxSelfLink;
    private OptionGroup comparisonMode;
    private ComboBox identA;
    private ComboBox identB;
    private Label labelIdent;
    private ComboBox nameA;
    private ComboBox nameB;
    private Label labelName;
    private Slider nameThreshold;
    private Label nameThresholdLabel;
    private Label blockingLabel;
    private Slider blocks;
    private Label cutoffLabel;
    private Slider cutoff;

    public BusinessEntityLinkerDialog() {
		super(BusinessEntityLinkerConfig.class);

        buildMainLayout();
        setCompositionRoot(mainLayout);
	}

	@Override
	public void setConfiguration(BusinessEntityLinkerConfig config) throws ConfigException {
		checkboxSelfLink.setValue(config.getNumberOfSources() == 1);
        identA.setValue(config.getIdentSelectionA());
		identB.setValue(config.getIdentSelectionB());
        nameA.setValue(config.getNameSelectionA());
        nameB.setValue(config.getNameSelectionB());
        nameThreshold.setValue(config.getNameThreshold() * 100);
        blocks.setValue(new Double(config.getBlocking()));
        cutoff.setValue(config.getConfidenceCutoff());
	}

	@Override
	public BusinessEntityLinkerConfig getConfiguration() throws ConfigException {
		BusinessEntityLinkerConfig config = new BusinessEntityLinkerConfig();

        config.setExact(checkboxSelfLink.getValue());
        config.setIdentSelectionA(identA.getValue().toString());
        config.setIdentSelectionB(identB.getValue().toString());
        config.setNameSelectionA(nameA.getValue().toString());
        config.setNameSelectionB(nameB.getValue().toString());
        config.setNameThreshold(nameThreshold.getValue() / 100);
        config.setBlocking(blocks.getValue().intValue());
        config.setConfidenceCutoff(cutoff.getValue());

        return config;
	}

    private void buildMainLayout() {
        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        // common part: create layout
        mainLayout = new GridLayout(3, 8);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);

        mainLayout.setColumnExpandRatio(0, 0.36f);
        mainLayout.setColumnExpandRatio(1, 0.28f);
        mainLayout.setColumnExpandRatio(2, 0.36f);

        buildNumberOfSources();
        buildComparisonMode();
        buildIdentSelection();
        buildNameSelection();
        buildServiceFields();
    }

    private void buildNumberOfSources() {
        checkboxSelfLink = new CheckBox("Links are generated within one dataset");
        checkboxSelfLink.setDescription("When checked link configuration reads only from the first dataset and links it against itself. As a side product pairs of links A-B and B-A are generated.");
        checkboxSelfLink.setHeight("20px");
        mainLayout.addComponent(checkboxSelfLink, 0, 0, 2, 0);
    }

    private void buildComparisonMode() {
        comparisonMode = new OptionGroup("Comparison mode");
        comparisonMode.addItem("Equality match based on identifier");
        comparisonMode.addItem("Approximate match based on name");
        mainLayout.addComponent(comparisonMode, 0, 1, 2, 1);
    }

    private void buildIdentSelection() {
        identA = new ComboBox();
        identB = new ComboBox();
        identA.setWidth(100, Unit.PERCENTAGE);
        identB.setWidth(100, Unit.PERCENTAGE);

        identA.setNullSelectionAllowed(false);
        identB.setNullSelectionAllowed(false);

        Iterator<String> it = OptionsLists.ident.iterator();
        while(it.hasNext()) {
            String option = it.next();
            identA.addItem(option);
            identB.addItem(option);
        }

        labelIdent = new Label("Identifier");
        mainLayout.addComponent(identA, 0, 2);
        mainLayout.addComponent(labelIdent, 1, 2);
        mainLayout.addComponent(identB, 2, 2);
    }

    private void buildNameSelection() {
//        Label gap = new Label();
//        gap.setHeight("1em");
//        mainLayout.addComponent(gap, 0, 3, 2, 3);

        nameA = new ComboBox();
        nameB = new ComboBox();

        nameA.setNullSelectionAllowed(false);
        nameB.setNullSelectionAllowed(false);

        Iterator<String> it = OptionsLists.name.iterator();
        while(it.hasNext()) {
            String option = it.next();
            nameA.addItem(option);
            nameB.addItem(option);
        }

        labelName = new Label("Name");
        mainLayout.addComponent(nameA, 0, 3);
        mainLayout.addComponent(labelName, 1, 3);
        mainLayout.addComponent(nameB, 2, 3);

        nameThresholdLabel = new Label("Threshold");
        mainLayout.addComponent(nameThresholdLabel, 0, 4);

        nameThreshold = new Slider(0.0, 100.0, 1);
        nameThreshold.setDescription("Names are compared using Levenshtein distance. Threshold specifies how much the names can differ in percent. Names are transformed to lowercase and all special characters are removed prior to comparison.");
        nameThreshold.setWidth(100, Unit.PERCENTAGE);
        mainLayout.addComponent(nameThreshold, 1, 4, 2, 4);
    }

    private void buildServiceFields() {
        blockingLabel = new Label("Blocks");
        mainLayout.addComponent(blockingLabel, 0, 5);

        blocks = new Slider(BusinessEntityLinkerConfig.blockingBottomLimit, BusinessEntityLinkerConfig.blockingTopLimit);
        blocks.setDescription("Controls blocking function of silk. 0 turns blocking off. Higher values may reduce recall but are necessary for reasonable execution time for larger datasets.");
        blocks.setWidth(100, Unit.PERCENTAGE);
        mainLayout.addComponent(blocks, 1, 5, 2, 5);

        cutoffLabel = new Label("Cutoff");
        mainLayout.addComponent(cutoffLabel, 0, 6);

        cutoff = new Slider(0.0, 1.0, 1);
        cutoff.setDescription("Generated links with normalized score from interval (0, 1> above cutoff limit are considered correct. The rest are placed in secondary output data unit requiring manual verification.");
        cutoff.setWidth(100, Unit.PERCENTAGE);
        mainLayout.addComponent(cutoff, 1, 6, 2, 6);
    }

}
