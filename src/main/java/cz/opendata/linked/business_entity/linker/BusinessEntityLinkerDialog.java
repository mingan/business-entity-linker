package cz.opendata.linked.business_entity.linker;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class BusinessEntityLinkerDialog extends BaseConfigDialog<BusinessEntityLinkerConfig> {
    public static final String APPROXIMATE = "Approximate match based on name";

    private GridLayout mainLayout;

    private TabSheet tabSheet;

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

    private Set<Component> activeOnIdent = new HashSet<>();
    private Set<Component> activeOnName = new HashSet<>();

    final String EQUALITY = "Equality match based on identifier";
    private GridLayout inputLayout;
    private GridLayout optionsLayout;
    private VerticalLayout javaConfigLayout;

    public BusinessEntityLinkerDialog() {
		super(BusinessEntityLinkerConfig.class);

        buildMainLayout();
        setCompositionRoot(mainLayout);
	}

	@Override
	public void setConfiguration(BusinessEntityLinkerConfig config) throws ConfigException {
		checkboxSelfLink.setValue(config.getNumberOfSources() == 1);
        setComparisonMode(config.isExact());
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

        config.setSelfLink(checkboxSelfLink.getValue());
        config.setExact(isExact());
        config.setIdentSelectionA(identA.getValue().toString());
        config.setIdentSelectionB(identB.getValue().toString());
        config.setNameSelectionA(nameA.getValue().toString());
        config.setNameSelectionB(nameB.getValue().toString());
        config.setNameThreshold(nameThreshold.getValue() / 100);
        config.setBlocking(blocks.getValue().intValue());
        config.setConfidenceCutoff(cutoff.getValue());

        return config;
	}

    private boolean isExact() {
        return comparisonMode.getValue().toString().equals(EQUALITY);
    }

    private void setComparisonMode(boolean exact) {
        if (exact) {
            comparisonMode.setValue(EQUALITY);
        } else {
            comparisonMode.setValue(APPROXIMATE);
        }
    }


    private void buildMainLayout() {
        setWidth("100%");
        setHeight("100%");

        mainLayout = new GridLayout(1, 1);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");

        tabSheet = new TabSheet();
        tabSheet.setImmediate(true);
        tabSheet.setWidth("100%");
        tabSheet.setHeight("100%");

        buildInputTab();
        buildOptionsTab();
        buildJavaConfigTab();

        mainLayout.addComponent(tabSheet, 0, 0);
        mainLayout.setComponentAlignment(tabSheet, Alignment.TOP_LEFT);
    }

    private void buildInputTab() {
        inputLayout = new GridLayout(6, 2);
        inputLayout.setMargin(false);
        inputLayout.setSpacing(true);
        inputLayout.setWidth("100%");
        inputLayout.setHeight("100%");

        inputLayout.setColumnExpandRatio(0, 0.5f);
        inputLayout.setColumnExpandRatio(1, 0.5f);

        buildNumberOfSources();
        Component[] labels = createColumnLabels();
        inputLayout.addComponent(labels[0], 0, 1);
        inputLayout.addComponent(labels[1], 1, 1);

        tabSheet.addTab(inputLayout, "Input");
    }

    private void buildNumberOfSources() {
        checkboxSelfLink = new CheckBox("Links are generated within one dataset");
        checkboxSelfLink.setDescription("When checked link configuration reads only from the first dataset and links it against itself. As a side product pairs of links A-B and B-A are generated.");
        checkboxSelfLink.setHeight("20px");
        inputLayout.addComponent(checkboxSelfLink, 0, 0, 1, 0);
    }

    private Component[] createColumnLabels() {
        Label source = new Label("Source");
        source.addStyleName(Reindeer.LABEL_H1);
        Label target = new Label("Target");
        target.addStyleName(Reindeer.LABEL_H1);

        return new Label[]{source, target};
    }

    private void buildOptionsTab() {
        optionsLayout = new GridLayout(3, 7);
        optionsLayout.setMargin(false);
        optionsLayout.setSpacing(true);
        optionsLayout.setWidth("100%");
        optionsLayout.setHeight("100%");

        optionsLayout.setColumnExpandRatio(0, 0.36f);
        optionsLayout.setColumnExpandRatio(1, 0.28f);
        optionsLayout.setColumnExpandRatio(2, 0.36f);

        Component[] labels = createColumnLabels();
        optionsLayout.addComponent(labels[0], 0, 0);
        optionsLayout.addComponent(labels[1], 2, 0);

        buildComparisonMode();
        buildIdentSelection();
        buildNameSelection();
        buildServiceFields();

        tabSheet.addTab(optionsLayout, "Linkage Rule");
    }

    private void buildJavaConfigTab() {
        javaConfigLayout = new VerticalLayout();

        tabSheet.addTab(javaConfigLayout, "Java");
    }

    private void buildComparisonMode() {
        comparisonMode = new OptionGroup("Comparison mode");
        comparisonMode.addItem(EQUALITY);
        comparisonMode.addItem(APPROXIMATE);

        comparisonMode.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (event.getProperty().toString().equals(EQUALITY)) {
                    disableIdent();
                    enableName();
                } else {
                    enableIdent();
                    disableName();
                }
            }
        });
        optionsLayout.addComponent(comparisonMode, 0, 1, 2, 1);
    }

    private void disableIdent() {
        enableIdent(false);
    }

    private void enableIdent() {
        enableIdent(true);
    }

    private void enableIdent(boolean enabled) {
        Iterator<Component> it = activeOnIdent.iterator();
        while (it.hasNext()) {
            it.next().setEnabled(enabled);
        }
    }

    private void disableName() {
        enableName(false);
    }

    private void enableName() {
        enableName(true);
    }

    private void enableName(boolean enabled) {
        Iterator<Component> it = activeOnName.iterator();
        while (it.hasNext()) {
            it.next().setEnabled(enabled);
        }
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
        optionsLayout.addComponent(identA, 0, 2);
        optionsLayout.addComponent(labelIdent, 1, 2);
        optionsLayout.addComponent(identB, 2, 2);

        activeOnIdent.add(identA);
        activeOnIdent.add(identB);
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
        optionsLayout.addComponent(nameA, 0, 3);
        optionsLayout.addComponent(labelName, 1, 3);
        optionsLayout.addComponent(nameB, 2, 3);

        nameThresholdLabel = new Label("Threshold");
        optionsLayout.addComponent(nameThresholdLabel, 0, 4);

        nameThreshold = new Slider(0.0, 100.0, 1);
        nameThreshold.setDescription("Names are compared using Levenshtein distance. Threshold specifies how much the names can differ in percent. Names are transformed to lowercase and all special characters are removed prior to comparison.");
        nameThreshold.setWidth(100, Unit.PERCENTAGE);
        optionsLayout.addComponent(nameThreshold, 1, 4, 2, 4);

        activeOnName.add(nameA);
        activeOnName.add(nameB);
        activeOnName.add(nameThreshold);
    }

    private void buildServiceFields() {
        cutoffLabel = new Label("Cutoff");
        optionsLayout.addComponent(cutoffLabel, 0, 5);

        cutoff = new Slider(0.0, 1.0, 1);
        cutoff.setDescription("Generated links with normalized score from interval (0, 1> above cutoff limit are considered correct. The rest are placed in secondary output data unit requiring manual verification.");
        cutoff.setWidth(100, Unit.PERCENTAGE);
        optionsLayout.addComponent(cutoff, 1, 5, 2, 5);

        activeOnName.add(cutoff);

        blockingLabel = new Label("Blocks");
        optionsLayout.addComponent(blockingLabel, 0, 6);

        blocks = new Slider(BusinessEntityLinkerConfig.blockingBottomLimit, BusinessEntityLinkerConfig.blockingTopLimit);
        blocks.setDescription("Controls blocking function of silk. 0 turns blocking off. Higher values may reduce recall but are necessary for reasonable execution time for larger datasets.");
        blocks.setWidth(100, Unit.PERCENTAGE);
        optionsLayout.addComponent(blocks, 1, 6, 2, 6);
    }

}
