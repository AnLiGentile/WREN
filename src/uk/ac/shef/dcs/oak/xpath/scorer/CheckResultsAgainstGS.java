package uk.ac.shef.dcs.oak.xpath.scorer;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.shef.dcs.oak.operations.SetOperations;
import uk.ac.shef.dcs.oak.operations.TextOperations;
import uk.ac.shef.dcs.oak.xpath.processors.ExtractValues;

public class CheckResultsAgainstGS {

	private static Logger l4j = Logger.getLogger(CheckResultsAgainstGS.class);

	// TODO this are just for testing purposes
	// add read from main args[]
	private static String experimentName = "testExperiment";
	// this folder contains as many subfolder as many strategies have been
	// tested
	private static String annotationFolder = "./extractionResults/testExperiment";
	private static String gtFolder = "./resources/datasets/swde-17477/groundtruth";
	private static String resMultiple = "./experimentResults/" + experimentName
			+ File.separator;

	private static String gazFolderCard = "./resources/gazetteers/gazWithCardinality/";

	public double compare(String gsFile, String resultFile) {
		LoadGS gs = new LoadGS(gsFile);
		HashMap<String, Set<String>> gsData = gs.getValues();

		double accuracy = 0;
		double accuracyPartial = 0;

		LoadGS res = new LoadGS(resultFile);
		HashMap<String, Set<String>> resData = res.getValues();

		int gsCountPages = 0; // number of pages, 2000 or less
		int gsCountNumberOfRes = 0; // number of results, can be multiple on
									// single page
		int resCountfullMatches = 0; // number of full matches with the gold
										// standard
		int resCountMatchesSIGIRlike = 0; // number of matches with the gold
											// standard, following SIGIR method
											// for multiple values

		int resCountPartialMatch = 0; // number of actual matching, counting
										// each result as indipendent from the
										// page
		int resFalseNegative = 0; // number of actual matching, counting each
									// result as indipendent from the page

		int resCountTotal = 0; // total number of results from the method

		for (String g : gsData.keySet()) {
			gsCountPages++;
			Set<String> gsResSet = new HashSet<String>(); // set of result for
															// each page
			for (String r : gsData.get(g)) {
				r = TextOperations.normalizeString(r);
				gsResSet.add(r);
			}
			gsCountNumberOfRes = gsCountNumberOfRes + gsResSet.size();
			// System.out.println(gsCountPages+" loaded GS for page "+g+"\t"+gsResSet+"\t size: "+gsResSet.size());

			if (resData.get(g) != null) {
				Set<String> resResSet = new HashSet<String>();
				for (String re : resData.get(g)) {
					resResSet.add(TextOperations.normalizeString(re));
				}
				resCountTotal = resCountTotal + resResSet.size();

				// System.out.print(gsCountPages+" loaded RE for page "+g+"\t"+resResSet+"\t size: "+resResSet.size()+
				// " " +resResSet.equals(gsResSet));

				// if the result set is identical to gold standard
				if (resResSet.equals(gsResSet)) {
					resCountfullMatches++;
					resCountMatchesSIGIRlike++;
					resCountPartialMatch = resCountPartialMatch
							+ gsResSet.size();
				}

				else {
					l4j.warn(g + " not equal " + resResSet + " " + gsResSet);
					if (resResSet.size() == 1) {
						String r = resResSet.iterator().next();
						if (gsResSet.contains(r))
							resCountMatchesSIGIRlike++;
					}
					Set<String> match = SetOperations.intersection(resResSet,
							gsResSet);
					resCountPartialMatch = resCountPartialMatch + match.size();
					resFalseNegative = resFalseNegative + resResSet.size()
							- match.size();
					// System.out.print(" CORRECT anwsers: "+match.size()+" WRONG anwsers: "+(resResSet.size()-match.size()));
					// if(gsFile.contains("book-")&resultFile.contains("-title")&resultFile.contains("annotation_results_strategySingleXpath"))
					// System.out.println(" GS: "+gsResSet+" RESULTS: "+resResSet);

				}
			}
			// System.out.println();

		}

		accuracyPartial = (double) resCountPartialMatch / gsCountNumberOfRes;
		// accuracy = (double) resCountfullMatches / gsCountPages;
		accuracy = (double) resCountMatchesSIGIRlike / gsCountPages;

		// TODO save on file
		// System.out.print(gsFile.substring(gsFile.lastIndexOf(File.separator)));
		// System.out.println("gs: "+
		// gsCountPages+" gs res: "+gsCountNumberOfRes+" res: "+resCountTotal+" full matches: "+resCountfullMatches+" partial matches: "+resCountPartialMatch);

		// System.out.println("accuracy "+
		// resCountfullMatches+"/"+gsCountPages+" = "+ accuracy);
		//
		// System.out.println("partial accuracy "+
		// resCountPartialMatch+"/"+gsCountNumberOfRes+" = "+ accuracyPartial);
		//
		// System.out.println("resCountTotal "+ resCountTotal);

		if (accuracy < 1) {
			// System.out.println(resultFile.substring(resultFile.lastIndexOf(File.separator))+"\t accuracy "+
			// resCountfullMatches+"/"+gsCountPages+" = "+ accuracy);
			l4j.warn(resultFile.substring(resultFile
					.lastIndexOf(File.separator) + 1) + "\t" + accuracy);
		}
		return accuracy;
	}

	public double[] precRecall(String gsFile, String resultFile) {
		LoadGS gs = new LoadGS(gsFile);
		HashMap<String, Set<String>> gsData = gs.getValues();

		double[] precisionRecall = { 0, 0 };

		double accuracy = 0;
		double accuracyPartial = 0;

		LoadGS res = new LoadGS(resultFile);
		HashMap<String, Set<String>> resData = res.getValues();

		int gsCountPages = 0; // number of pages, 2000 or less
		int gsCountNumberOfRes = 0; // number of results, can be multiple on
									// single page
		int resCountfullMatches = 0; // number of full matches with the gold
										// standard
		int resCountMatchesSIGIRlike = 0; // number of matches with the gold
											// standard, following SIGIR method
											// for multiple values

		int resCountPartialMatch = 0; // number of actual matching, counting
										// each result as indipendent from the
										// page
		int resFalseNegative = 0; // number of actual matching, counting each
									// result as indipendent from the page

		int resCountTotal = 0; // total number of results from the method

		for (String g : gsData.keySet()) {
			gsCountPages++;
			Set<String> gsResSet = new HashSet<String>(); // set of result for
															// each page
			for (String r : gsData.get(g)) {
				r = TextOperations.normalizeString(r);
				gsResSet.add(r);
			}
			gsCountNumberOfRes = gsCountNumberOfRes + gsResSet.size();
			// System.out.println(gsCountPages+" loaded GS for page "+g+"\t"+gsResSet+"\t size: "+gsResSet.size());

			if (resData.get(g) != null) {
				Set<String> resResSet = new HashSet<String>();
				for (String re : resData.get(g)) {
					resResSet.add(TextOperations.normalizeString(re));
				}
				resCountTotal = resCountTotal + resResSet.size();

				// System.out.print(gsCountPages+" loaded RE for page "+g+"\t"+resResSet+"\t size: "+resResSet.size()+
				// " " +resResSet.equals(gsResSet));

				// if the result set is identical to gold standard
				if (resResSet.equals(gsResSet)) {
					resCountfullMatches++;
					resCountMatchesSIGIRlike++;
					resCountPartialMatch = resCountPartialMatch
							+ gsResSet.size();
				}

				else {
					l4j.warn(g + " not equal " + resResSet + " " + gsResSet);
					if (resResSet.size() == 1) {
						String r = resResSet.iterator().next();
						if (gsResSet.contains(r))
							resCountMatchesSIGIRlike++;
					}
					Set<String> match = SetOperations.intersection(resResSet,
							gsResSet);
					if (match.size() > 1)
						resCountMatchesSIGIRlike++;

					resCountPartialMatch = resCountPartialMatch + match.size();
					resFalseNegative = resFalseNegative + resResSet.size()
							- match.size();
					// System.out.print(" CORRECT anwsers: "+match.size()+" WRONG anwsers: "+(resResSet.size()-match.size()));
					// if(gsFile.contains("book-")&resultFile.contains("-title")&resultFile.contains("annotation_results_strategySingleXpath"))
					// System.out.println(" GS: "+gsResSet+" RESULTS: "+resResSet);

				}

			} else {

			}
			// System.out.println();

		}

		accuracyPartial = (double) resCountPartialMatch / gsCountNumberOfRes;
		// accuracy = (double) resCountfullMatches / gsCountPages;
		accuracy = (double) resCountMatchesSIGIRlike / gsCountPages;

		precisionRecall[0] = resCountMatchesSIGIRlike / (double) resData.size(); // precision
		precisionRecall[1] = resCountMatchesSIGIRlike / (double) gsData.size(); // recall

		// TODO save on file
		// System.out.print(gsFile.substring(gsFile.lastIndexOf(File.separator)));
		// System.out.println("gs: "+
		// gsCountPages+" gs res: "+gsCountNumberOfRes+" res: "+resCountTotal+" full matches: "+resCountfullMatches+" partial matches: "+resCountPartialMatch);

		// System.out.println("accuracy "+
		// resCountfullMatches+"/"+gsCountPages+" = "+ accuracy);
		//
		// System.out.println("partial accuracy "+
		// resCountPartialMatch+"/"+gsCountNumberOfRes+" = "+ accuracyPartial);
		//
		// System.out.println("resCountTotal "+ resCountTotal);

		if (accuracy < 1) {
			// System.out.println(resultFile.substring(resultFile.lastIndexOf(File.separator))+"\t accuracy "+
			// resCountfullMatches+"/"+gsCountPages+" = "+ accuracy);
			l4j.warn(resultFile.substring(resultFile
					.lastIndexOf(File.separator) + 1) + "\t" + accuracy);
		}

		// System.out.println("resCountMatchesSIGIRlike "+resCountMatchesSIGIRlike);
		// System.out.println("GS: " + gsData.size()+"-->"+gsCountPages);
		// System.out.println(resData.size());

		return precisionRecall;
	}

	public PrecisionRecall precRecallWithSingleValueExpected(String gsFile,
			String resultFile) {
		LoadGS gs = new LoadGS(gsFile);
		HashMap<String, Set<String>> gsData = gs.getValues();

		// double [] precisionRecall={0,0};

		double relevantDocumentCount = gsData.size();
		double tp = 0;// true positives: relevantResults -->
		double fp = 0; // false positives: allRes - tp
		double fn = 0; // false negatives: relevantDocumentCount -
						// relevantResults

		LoadGS res = new LoadGS(resultFile);
		HashMap<String, Set<String>> resData = res.getValues();

		int resCountTotal = 0; // total number of results from the method

		for (String g : gsData.keySet()) {
			Set<String> gsResSet = new HashSet<String>(); // set of result for
															// each page
			for (String r : gsData.get(g)) {
				r = TextOperations.normalizeString(r);
				gsResSet.add(r);
			}
			// gsResSet now contains all GS results for page with id g

			if (resData.get(g) != null) {
				Set<String> resResSet = new HashSet<String>();
				for (String re : resData.get(g)) {
					resResSet.add(TextOperations.normalizeString(re));
				}
				// resResSet now contains all results for page with id g

				resCountTotal = resCountTotal + resResSet.size();
				Set<String> match = SetOperations.intersection(resResSet,
						gsResSet);

				if (match.isEmpty()) {
					fn = fn + 1;
				}

				Set<String> falsePos = SetOperations.difference(resResSet,
						match);

				l4j.trace("falsePos " + falsePos);

				l4j.trace("gsResSet " + gsResSet);
				l4j.trace("resResSet " + resResSet);
				l4j.trace("match " + match);

				tp = tp + match.size();
				fp = fp + falsePos.size();

			}

		}

		l4j.trace("relevantDocumentCount " + relevantDocumentCount);
		l4j.trace("tp " + tp);
		l4j.trace("fp " + fp);
		l4j.trace("fn " + fn);

		double precision = tp / (tp + fp);
		double recall = tp / (tp + fn);

		PrecisionRecall precisionRecall = new PrecisionRecall(precision, recall);

		return precisionRecall;
	}

	public PrecisionRecall precRecallWithMultipleValueCheck(String gsFile,
			String resultFile) {
		LoadGS gs = new LoadGS(gsFile);
		HashMap<String, Set<String>> gsData = gs.getValues();

		// double [] precisionRecall={0,0};

		double relevantDocumentCount = 0;
		double tp = 0;// true positives: relevantResults -->
		double fp = 0; // false positives: allRes - tp
		double fn = 0; // false negatives: relevantDocumentCount -
						// relevantResults

		LoadGS res = new LoadGS(resultFile);
		HashMap<String, Set<String>> resData = res.getValues();

		int resCountTotal = 0; // total number of results from the method

		for (String g : gsData.keySet()) {
			Set<String> gsResSet = new HashSet<String>(); // set of result for
															// each page
			for (String r : gsData.get(g)) {
				r = TextOperations.normalizeString(r);
				gsResSet.add(r);
			}
			// gsResSet now contains all GS results for page with id g

			relevantDocumentCount = relevantDocumentCount + gsResSet.size();

			if (resData.get(g) != null) {
				Set<String> resResSet = new HashSet<String>();
				for (String re : resData.get(g)) {
					resResSet.add(TextOperations.normalizeString(re));
				}
				// resResSet now contains all results for page with id g

				resCountTotal = resCountTotal + resResSet.size();
				Set<String> match = SetOperations.intersection(resResSet,
						gsResSet);

				Set<String> falseNeg = SetOperations
						.difference(gsResSet, match);

				Set<String> falsePos = SetOperations.difference(resResSet,
						match);

				l4j.trace("falseNeg " + falseNeg);
				l4j.trace("falsePos " + falsePos);

				l4j.trace("gsResSet " + gsResSet);
				l4j.trace("resResSet " + resResSet);
				l4j.trace("match " + match);

				tp = tp + match.size();
				fp = fp + falsePos.size();
				fn = fn + falseNeg.size();

			}

		}

		l4j.trace("relevantDocumentCount " + relevantDocumentCount);
		l4j.trace("tp " + tp);
		l4j.trace("fp " + fp);
		l4j.trace("fn " + fn);

		double precision = tp / (tp + fp);
		double recall = tp / (tp + fn);

		PrecisionRecall precisionRecall = new PrecisionRecall(precision, recall);

		return precisionRecall;
	}

	public static void main(String[] args) {

		l4j.info("started");
		CheckResultsAgainstGS cr = new CheckResultsAgainstGS();

		File gs = new File(gtFolder);

		File res = new File(annotationFolder);

		System.out.print("domain \t website \t attribute");

		for (File strategy : res.listFiles()) {
			if (strategy.isDirectory())
				System.out.print("\t" + strategy.getName() + "\t");

		}
		System.out.println();
		// Map<String,PrintWriter> outS = new HashMap<String,PrintWriter>();
		Map<String, PrintWriter> outM = new HashMap<String, PrintWriter>();

		try {
			// outS = new PrintWriter(new FileWriter(resSingle));
			// outM = new PrintWriter(new FileWriter(resMultiple));

			// new File(resSingle).mkdirs();
			new File(resMultiple).mkdirs();

			for (File strategy : res.listFiles()) {
				if (strategy.isDirectory()) {

					String strategyName = strategy.getName();
					// outS.put(strategyName, new PrintWriter(new
					// FileWriter(resSingle+strategyName+".xls")));
					outM.put(strategyName, new PrintWriter(new FileWriter(
							resMultiple + strategyName + ".xls")));
					// outS.get(strategyName).print("domain \t website \t attribute \n"
					// );
					outM.get(strategyName).print(
							"domain \t website \t attribute \n");
				}
			}

			for (File att : gs.listFiles()) {
				if (att.isDirectory()) {
					// System.out.println(att);

					for (File gsXatt : att.listFiles()) {
						if (gsXatt.getName().endsWith(".txt")) {
							String[] name = gsXatt
									.getName()
									.substring(
											0,
											gsXatt.getName()
													.lastIndexOf(".txt"))
									.split("-");
							// System.out.print(gsXatt.getName());
							System.out.print(name[0] + "\t" + name[1] + "\t"
									+ name[2]);
							// outS.print(name[0]+"\t" +name[1]+"\t" +name[2]);
							// outM.print(name[0]+"\t" +name[1]+"\t" +name[2]);

							// for each strategy
							String gazCard = gazFolderCard + name[0];

							if (new File(gazCard).exists()) {
								Map<String, Integer> cardinalityInfo = ExtractValues
										.loadCardinalityInfo(gazCard);

								for (File strategy : res.listFiles()) {

									if (strategy.isDirectory()) {
										// TODO save results in one file per
										// strategy

										// load domain folder
										File resultFromStrategy = new File(
												strategy + File.separator
														+ att.getName());
										// System.out.print(" "+strategy.getName());
										// search for correct file
										if (resultFromStrategy.isDirectory()) {
											// Double acc = null;
											// double[] PR =null;
											PrecisionRecall pr = null;
											// PrecisionRecall prSingle =null;

											for (File g : resultFromStrategy
													.listFiles()) {
												if (g.getName()
														.endsWith(".txt")
														&& g.getName()
																.equals(gsXatt
																		.getName())) {

													l4j.trace("g = " + g);

													if (cardinalityInfo
															.get(name[2]) == 1) {
														pr = cr.precRecallWithSingleValueExpected(
																gsXatt.getAbsolutePath(),
																g.getAbsolutePath());

													} else {
														pr = cr.precRecallWithMultipleValueCheck(
																gsXatt.getAbsolutePath(),
																g.getAbsolutePath());

													}
													// pr =
													// cr.precRecallWithMultipleValueCheck(gsXatt.getAbsolutePath(),g.getAbsolutePath());
													// prSingle =
													// cr.precRecallWithSingleValueExpected(gsXatt.getAbsolutePath(),g.getAbsolutePath());

													continue;
												}
											}

											try {
												if (pr != null) {
													System.out.print("\t"
															+ pr.precision
															+ "\t" + pr.recall);

													outM.get(strategy.getName())
															.print(name[0]
																	+ "\t"
																	+ name[1]
																	+ "\t"
																	+ name[2]);
													outM.get(strategy.getName())
															.print("\t"
																	+ pr.precision
																	+ "\t"
																	+ pr.recall
																	+ "\n");
												} else {
													System.out.print("\t"
															+ "<null>\t<null>");

													outM.get(strategy.getName())
															.print(name[0]
																	+ "\t"
																	+ name[1]
																	+ "\t"
																	+ name[2]);
													outM.get(strategy.getName())
															.print("\t"
																	+ "<null>\t<null>\n");
												}

												// if (prSingle != null) {
												// System.out.print("\t" +
												// prSingle.precision+"\t"
												// +prSingle.recall);
												// outS.get(strategy.getName()).print(name[0]+"\t"
												// +name[1]+"\t" +name[2]);
												// outS.get(strategy.getName()).print("\t"
												// + prSingle.precision+"\t"
												// +prSingle.recall+"\n");
												// }
												// else {
												// System.out.print("\t" +
												// "<null>\t<null>");
												// outS.get(strategy.getName()).print(name[0]+"\t"
												// +name[1]+"\t" +name[2]);
												// outS.get(strategy.getName()).print("\t"
												// + "<null>\t<null>\n");
												// }

											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									}
									// outS.get(strategyName).close();
									// outM.get(strategyName).close();
								}
							}

							// out.println(gsXatt.getName());

						}
						// outS.get(strategyName).println();
						// outM.get(strategyName).println();

						System.out.println();

					}
					// outS.close();
					// outM.close();

				}

			}
			for (File strategy : res.listFiles()) {
				if (strategy.isDirectory()) {

					String strategyName = strategy.getName();

					// outS.get(strategyName).close();
					outM.get(strategyName).close();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public HashMap<String, Set<String>> getResFromStrategy(String resultFile) {

		LoadGS res = new LoadGS(resultFile);
		HashMap<String, Set<String>> resData = res.getValues();

		return resData;
	}
}
