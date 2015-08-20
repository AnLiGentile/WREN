package uk.ac.shef.dcs.oak.xpath.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

public class XpathOverlapCalculator {
	
	private Map<String, Integer> xpathIndexInMatrix;
	private Double[][] overlapMatrix;
	public Double[][] getOverlapMatrix() {
		return overlapMatrix;
	}

	public void setOverlapMatrix(Double[][] overlapMatrix) {
		this.overlapMatrix = overlapMatrix;
	}

	private Map<String, Map<String, Set<String>>> values;
	

	public XpathOverlapCalculator(
			Map<String, Map<String, Set<String>>> values) {
		super();
		this.values = values;
		this.xpathIndexInMatrix = new HashMap<String, Integer>();
		this.overlapMatrix = new Double[values.keySet().size()][values.keySet().size()];
		buildMatrix(values);
		
	}

	private void buildMatrix(Map<String, Map<String, Set<String>>> values) {
		Map<String, Map<String, String>> pr = new HashMap<String, Map<String,String>>();
		
		int i=0;
		for (Entry<String,Map<String, Set<String>>> e : values.entrySet()){
			xpathIndexInMatrix.put(e.getKey(), i);
			for (Entry<String, Set<String>> pages : e.getValue().entrySet()){
				if (pr.get(pages.getKey())==null)
					pr.put(pages.getKey(), new HashMap<String, String>());
				if (pages.getValue().size()>1) System.err.println(pages.getValue());
				pr.get(pages.getKey()).put(e.getKey(), pages.getValue().iterator().next());
			}
			i++;
		}
		for (Entry<String, Integer> row : xpathIndexInMatrix.entrySet()){
			for (Entry<String, Integer> col : xpathIndexInMatrix.entrySet()){
				int overlap =0;
				for (Entry<String, Map<String, String>> p : pr.entrySet()){
					try {
						String extractedValue1 = p.getValue().get(row.getKey());
						String extractedValue2 = p.getValue().get(col.getKey());
						if (extractedValue1!=null&extractedValue2!=null){

						if(extractedValue1.equalsIgnoreCase(extractedValue2))
								overlap++;}
					} catch (Exception e1) {
						System.err.println(p.getValue()
								+"\n" +row.getKey()+"\n"+ col.getKey());

						e1.printStackTrace();
					}
				}
				overlapMatrix[row.getValue()][col.getValue()] = (double)overlap;
			}
		}
	
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	
}
