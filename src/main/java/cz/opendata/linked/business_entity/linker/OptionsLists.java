package cz.opendata.linked.business_entity.linker;

import java.util.LinkedList;
import java.util.List;

public class OptionsLists {

    static final List<String> organiztaion = new LinkedList<>();
    static {
        organiztaion.add("schema:Organization");
        organiztaion.add("gr:BusinessEntity");
        organiztaion.add("organiztaion:Organization");
    }

    static final List<String> ident = new LinkedList<>();
    static {
        ident.add("adms:identifier/skos:notation");
        ident.add("schema:vatID");
        ident.add("schema:taxID");
        ident.add("gr:vatID");
        ident.add("gr:taxID");
    }

    static final List<String> name = new LinkedList<>();
    static {
        name.add("schema:name");
        name.add("gr:legalName");
        name.add("gr:name");
        name.add("dcterms:title");
        name.add("rdfs:label");
    }
}
