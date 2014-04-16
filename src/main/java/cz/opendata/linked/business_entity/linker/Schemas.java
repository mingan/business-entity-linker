package cz.opendata.linked.business_entity.linker;

import java.util.HashMap;
import java.util.Map;

public class Schemas {
    private static final Map<String, String> schemas = new HashMap<>();

    static {
        schemas.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        schemas.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        schemas.put("wgs84", "http://www.w3.org/2003/01/geo/wgs84_pos#");
        schemas.put("pc", "http://purl.org/procurement/public-contracts#");
        schemas.put("gr", "http://purl.org/goodrelations/v1#");
        schemas.put("adms", "http://www.w3.org/ns/adms#");
        schemas.put("skos", "http://www.w3.org/2004/02/skos/core#");
        schemas.put("schema", "http://schema.org/");
    }

    public static Map<String, String> getSchemas() {
        return schemas;
    }
}
