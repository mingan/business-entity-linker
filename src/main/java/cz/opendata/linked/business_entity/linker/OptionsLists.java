package cz.opendata.linked.business_entity.linker;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class OptionsLists {

    static final List<String> organization = new LinkedList<>();
    static {
        organization.add("schema:Organization");
        organization.add("gr:BusinessEntity");
        organization.add("organization:Organization");
        organization.add("custom");
    }

    static final List<String> ident = new LinkedList<>();
    static {
        ident.add("adms:identifier/skos:notation");
        ident.add("schema:vatID");
        ident.add("schema:taxID");
        ident.add("gr:vatID");
        ident.add("gr:taxID");
        ident.add("org:identifier");
        ident.add("rov:registration/skos:notation");
    }

    static final List<String> name = new LinkedList<>();
    static {
        name.add("schema:legalName");
        name.add("schema:name");
        name.add("gr:legalName");
        name.add("gr:name");
        name.add("rov:legalName");
        name.add("dcterms:title");
        name.add("rdfs:label");
    }

    static final List<String> metric = new LinkedList<>();
    static {
        metric.add("levenshtein");
        metric.add("jaro");
        metric.add("jaroWinkler");
    }

    public static String getCustomOrg() {
        return organization.get(OptionsLists.organization.size() - 1);
    }

    public static Set<String> getNonCustomOrgs() {
        Set<String> out = new HashSet<>(organization);
        out.remove(getCustomOrg());
        return out;
    }
}
