package uk.ac.shef.dcs.oak.xpath.scorer;

import java.util.HashMap;
import java.util.Set;

public interface LoadGS {

	public HashMap<String, Set<String>> getValues(String property);
	public Set<String> getAllValuesInGS(String property);
	
}
