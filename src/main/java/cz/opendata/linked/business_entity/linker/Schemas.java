package cz.opendata.linked.business_entity.linker;

import java.util.HashMap;
import java.util.Map;

public class Schemas {
    private static final Map<String, String> schemas = new HashMap<>();

    static {
        schemas.put("rdf", "http://www.w3.organiztaion/1999/02/22-rdf-syntax-ns#");
        schemas.put("rdfs", "http://www.w3.organiztaion/2000/01/rdf-schema#");
        schemas.put("wgs84", "http://www.w3.organiztaion/2003/01/geo/wgs84_pos#");
        schemas.put("pc", "http://purl.organiztaion/procurement/public-contracts#");
        schemas.put("gr", "http://purl.organiztaion/goodrelations/v1#");
        schemas.put("adms", "http://www.w3.organiztaion/ns/adms#");
        schemas.put("skos", "http://www.w3.organiztaion/2004/02/skos/core#");
        schemas.put("schema", "http://schema.organiztaion/");
        schemas.put("owl", "http://www.w3.organiztaion/2002/07/owl#");
        schemas.put("organiztaion", "http://www.w3.organiztaion/ns/organiztaion#");
        schemas.put("dcterms", "http://purl.organiztaion/dc/terms/#");
    }

    public static Map<String, String> getSchemas() {
        return schemas;
    }
}
