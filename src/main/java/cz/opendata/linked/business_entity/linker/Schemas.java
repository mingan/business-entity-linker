package cz.opendata.linked.business_entity.linker;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Schemas {
    private static final Map<String, String> schemas = new HashMap<>();

    static {
        schemas.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        schemas.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        schemas.put("wgs84", "http://www.w3.org/2003/01/geo/wgs84_pos#");
        schemas.put("gr", "http://purl.org/goodrelations/v1#");
        schemas.put("adms", "http://www.w3.org/ns/adms#");
        schemas.put("skos", "http://www.w3.org/2004/02/skos/core#");
        schemas.put("schema", "http://schema.org/");
        schemas.put("owl", "http://www.w3.org/2002/07/owl#");
        schemas.put("org", "http://www.w3.org/ns/organization#");
        schemas.put("dcterms", "http://purl.org/dc/terms/#");
        schemas.put("rov", "http://www.w3.org/ns/regorg#");
    }

    public static Map<String, String> getSchemas() {
        return schemas;
    }

    public static String asString() {
        List<String> list = new LinkedList<>();
        Set<String> keys = schemas.keySet();
        Iterator it = keys.iterator();
        while(it.hasNext()) {
            String key = (String) it.next();
            list.add(key + " (" + schemas.get(key) + ")");
        }
        return StringUtils.join(list, ",");
    }
}
