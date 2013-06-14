/**
 * 
 */
package database.dawis.webstart;

import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import pojos.DBColumn;

import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;
import biologicalObjects.edges.KEGGEdge;
import biologicalObjects.edges.ReactionPairEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.CompoundNode;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Disease;
import biologicalObjects.nodes.Drug;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Factor;
import biologicalObjects.nodes.Fragment;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.GeneOntology;
import biologicalObjects.nodes.Glycan;
import biologicalObjects.nodes.Matrix;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.Reaction;
import biologicalObjects.nodes.Site;
import configurations.Wrapper;
import database.dawis.DAWISQueries;
import database.dawis.EdgeController;
import database.dawis.ReactionAnalyser;
import edu.uci.ics.jung.graph.Vertex;

/**
 * 
 * This Class is to be used to combine the data from DAWIS remote control to a
 * biological network
 * 
 * @author Olga Mantler
 * 
 */
@SuppressWarnings("unchecked")
public class DAWISWebstartCombiner
{

	private MyGraph myGraph;

	private Pathway pw=null;

	ProgressBar bar;

	private RemoteData data;

	private String pathwayLink="";
	private String pathwayImage="";
	private String pathwayNumber="";

	private boolean showPathways, showDiseases, showGenes, showGOs, showProteins, showEnzymes, showCompounds, showGlycans, showDrugs;

	private boolean newReaction=false;
	private boolean newReactionPair=false;
	private boolean loadedPath=false;

	private Vector<String> newReactions=new Vector<String>();
	private Vector<String> newReactionPairs=new Vector<String>();
//	private Vector<String[]> newLoadedElements=new Vector<String[]>();
	private ArrayList<DBColumn> newLoadedElements=new ArrayList<DBColumn>();

	boolean[] settings=new boolean[11];

	private DAWISNode dawisNode=null;

	// Hashtable with all nodes of the pathway
	private Hashtable<String, BiologicalNodeAbstract> allElements=new Hashtable<String, BiologicalNodeAbstract>();
	// Vector with all edges of the pathway
	private Vector<String[]> edges=new Vector();
	// Hashtable with all reactionPairEdge informations
	private Hashtable<String, ReactionPairEdge> rPairEdges=new Hashtable<String, ReactionPairEdge>();
	// Hashtable with all reactionEdge informations
	private Hashtable<String, KEGGEdge> keggEdges=new Hashtable<String, KEGGEdge>();

	private int row=0;
	private int column=-1;

	private InternalGraphRepresentation adjazenzList;

//	public DAWISWebstartCombiner(Pathway pathway, Vector<String[]> elements, RemoteData remoteData)
	public DAWISWebstartCombiner(Pathway pathway, ArrayList<DBColumn> elements, RemoteData remoteData)
	{

		this.pw=pathway;
		if (!pw.getBiologicalElements().isEmpty())
		{
			loadedPath=true;
		}
		this.data=remoteData;
		newLoadedElements=elements;
		data.addElements(elements);

		getPathwayHeadings();

		pw.setLink(pathwayLink);
		pw.setImagePath(pathwayImage);
		pw.setNumber(pathwayNumber);

		myGraph=pw.getGraph();

	}

	/**
	 * add elements to the network String array: <element_id, object,
	 * databaseName>
	 * 
	 * @param elements
	 */
	public void addElements()
	{
		MainWindow w=MainWindowSingelton.getInstance();
		w.setEnable(true);

		// test objects to load
		getObjects();

		// set settings
		setSettings();

		// create for each objects elementsVector with elements to check
		createElementsVector();

		// handle reactions first
		if (newReaction)
		{
			Iterator<String> nrIt=newReactions.iterator();
			while (nrIt.hasNext())
			{
				String elem=(String)nrIt.next();
				createReaction(elem);
			}
			newReaction=false;
		}

		// handle reaction pairs
		if (newReactionPair)
		{
			Iterator<String> nrIt=newReactionPairs.iterator();
			while (nrIt.hasNext())
			{
				String elem=(String)nrIt.next();
				createReactionPair(elem);
			}
			newReactionPair=false;
		}

		// handle other elements
		for (DBColumn column : newLoadedElements)
		{
			String[] elem=column.getColumn();

			if (!elem[1].equals("Reaction")&!elem[1].equals("Reaction Pair"))
			{
				Vector<String> elemVect=getElementsVector(elem[1]);

				if (elemVect.size()>0)
				{
					// test element dependences
					testDependences(elem, elemVect);
				}
			}
		}
	}

	private void createReactionPair(String elem)
	{

		String db="KEGG";
		BiologicalNodeAbstract newBNA=null;
		CompoundNode one=null;
		CompoundNode two=null;
		boolean oneExists=false;

		String query=DAWISQueries.getCompoundFromReactionPair;
		String[] attributes={elem};

		Hashtable<String, String> ht=data.getIdIdRelationsAsHashtable();

		ArrayList<DBColumn> pair=new Wrapper().requestDbContent(3, query, attributes);
		
		for (DBColumn column : pair)
		{
			String[] det=column.getColumn();
			String id=det[0];

			if (!ht.containsKey(id))
			{
				ht.put(id, id);

				String[] el={id, "Compound", "KEGG"};
				newLoadedElements.add(new DBColumn(el));

				if (!oneExists)
				{
					one=new CompoundNode(det[0], "", null);
					one.setAbstract(false);
					one.setReference(false);
					allElements.put(det[0], one);

					newBNA=one;
					newBNA.setDB(db);

					createDAWISNode(newBNA);
					addAllIDsToElementsHashtable(newBNA);

					oneExists=true;
				}
				else
				{
					two=new CompoundNode(det[0], "", null);
					two.setAbstract(false);
					two.setReference(false);
					allElements.put(det[0], two);

					newBNA=two;
					newBNA.setDB(db);

					createDAWISNode(newBNA);
					addAllIDsToElementsHashtable(newBNA);
				}

				getObjects();
				setSettings();
				createElementsVector();

			}
			else
			{
				String vertexLabel=ht.get(id);
				if (one==null)
				{
					if (!loadedPath)
					{
						one=(CompoundNode)allElements.get(vertexLabel);
					}
					else
					{
						one=(CompoundNode)pw.getNodeByName(vertexLabel);
					}
				}
				else if (two==null)
				{
					if (!loadedPath)
					{
						two=(CompoundNode)allElements.get(vertexLabel);
					}
					else
					{
						two=(CompoundNode)pw.getNodeByName(vertexLabel);
					}
				}
			}

			if (one!=null&&two!=null)
			{
				ReactionPairEdge edge=new ReactionPairEdge();
				edge.setReactionPairID(elem);
				fillReactionPairEdgeWithInformation(edge);
				String[] entry={two.getLabel(), one.getLabel(), "True"};
				edges.add(entry);
				rPairEdges.put(two.getLabel()+one.getLabel(), edge);
			}
		}
	}

	private void fillReactionPairEdgeWithInformation(ReactionPairEdge edge)
	{

		String[] elem={edge.getReactionPairID()};
		ArrayList<DBColumn> reactionPairDetails=new Wrapper().requestDbContent(3, DAWISQueries.getReactionPairDetails, elem);

		
		if (reactionPairDetails.size()>0)
		{
			for (DBColumn column : reactionPairDetails)
			{
				String[] det=column.getColumn();
				
				edge.setName(det[2]);
				edge.setType(det[4]);
			}

		}
	}

	/**
	 * get elements, which are related to the object Vector <String>
	 * 
	 * @param object
	 * @return
	 */
	private Vector<String> getElementsVector(String object)
	{

		if (object.equals("Pathway"))
		{
			return data.getPathwayRelatedDomains();
		}
		else if (object.equals("Enzyme"))
		{
			return data.getEnzymeRelatedDomains();
		}
		else if (object.equals("Protein"))
		{
			return data.getProteinRelatedDomains();
		}
		else if (object.equals("Gene"))
		{
			return data.getGeneRelatedDomains();
		}
		else if (object.equals("Gene Ontology"))
		{
			return data.getGeneOntologyRelatedDomains();
		}
		else if (object.equals("Disease"))
		{
			return data.getDiseaseRelatedDomains();
		}
		else if (object.equals("Glycan"))
		{
			return data.getGlycanRelatedDomains();
		}
		else if (object.equals("Compound"))
		{
			return data.getCompoundRelatedDomains();
		}
		else if (object.equals("Drug"))
		{
			return data.getDrugRelatedDomains();
		}
		else if (object.equals("Factor"))
		{
			return data.getFactorRelatedDomains();
		}
		else if (object.equals("Fragment"))
		{
			return data.getFragmentRelatedDomains();
		}
		else if (object.equals("Site"))
		{
			return data.getSiteRelatedDomains();
		}
		else if (object.equals("Matrix"))
		{
			return data.getMatrixRelatedDomains();
		}
		else
		{
			return null;
		}

	}

	/**
	 * create reaction
	 * 
	 * @param reaction_id
	 */
	private void createReaction(String singleReaction)
	{

		String db="";

		Hashtable<String, String> ht=data.getIdIdRelationsAsHashtable();

		boolean keggReaction=false;
		boolean transpathReaction=false;

		if (singleReaction.startsWith("XN"))
		{
			transpathReaction=true;
			db="Transpath";
		}
		else if (singleReaction.startsWith("R"))
		{
			keggReaction=true;
			db="KEGG";
		}

		// create KEGGEdge, where the reaction data should be stored
		KEGGEdge edge=new KEGGEdge();
		edge.setKEEGReactionID(singleReaction);

		String[] det={singleReaction};
		ArrayList<DBColumn> enzymes=null;
		ArrayList<DBColumn> substrate=null;
		ArrayList<DBColumn> products=null;

//		ArrayList<DBColumn> rSub=null;
//		ArrayList<DBColumn> rProd=null;

		if (keggReaction)
		{

			// store reaction data in the KEGGEdge
			fillKEGGEdgeWithElementInformation(edge);

			// get reaction enzymes
			enzymes=new Wrapper().requestDbContent(3, DAWISQueries.getEnzymeFromReaction, det);

			ReactionAnalyser reactionAnalyser=new ReactionAnalyser(edge.getEquation());

//			rProd=reactionAnalyser.getProducts();
//			rSub=reactionAnalyser.getSubstrates();
			
			products=reactionAnalyser.getProducts();
			substrate=reactionAnalyser.getSubstrates();

//			if (rProd.size()>0)
//			{
//				products=new ArrayList<DBColumn>();
//				pasteElementsToVector(rProd, products);
//			}
//			
//			if (rSub.size()>0)
//			{
//				substrate=new ArrayList<DBColumn>();
//				pasteElementsToVector(rSub, substrate);
//			}

		}
		else if (transpathReaction)
		{
			// store reaction data in the KEGGEdge
			fillKEGGEdgeWithTPElementInformation(edge);

			// get reaction substrates
			substrate=new Wrapper().requestDbContent(3, DAWISQueries.getTRANSPATHReactionSubstrates, det);

			// get reaction products
			products=new Wrapper().requestDbContent(3, DAWISQueries.getTRANSPATHReactionProducts, det);
		}

		int countEnzymes=0;
		
		if (enzymes!=null)
		{
			countEnzymes=enzymes.size();
		}
		
		int countSubstrates=0;
		
		if (substrate!=null)
		{
			countSubstrates=substrate.size();
		}
		
		int countProducts=0;
		
		if (products!=null)
		{
			countProducts=products.size();
		}

		BiologicalNodeAbstract enz=null;
		BiologicalNodeAbstract sub=null;
		BiologicalNodeAbstract prod=null;

		if (countEnzymes>0)
		{

			// add object type to the elementsVector of related elements
			checkObjects("Enzyme");

			// create enzyme reaction
			
			for (DBColumn column : enzymes)
			{
				String[] e=column.getColumn();
				edge.addEnzyme(e[0]);

				if (!ht.containsKey(e[0]))
				{
					ht.put(e[0], e[0]);
					String[] enzDet={e[0], "Enzyme", "KEGG"};
					newLoadedElements.add(new DBColumn(enzDet));

					enz=new Enzyme(e[0], "", null);
					enz.setAbstract(false);
					enz.setReference(false);
					enz.setDB(db);
					allElements.put(e[0], enz);

					createDAWISNode(enz);
					Enzyme e2=(Enzyme)enz;
					e2.lookUpAtAllDatabases();

					addAllIDsToElementsHashtable(e2);

				}
				else
				{
					String vertexLabel=ht.get(e[0]);

					if (!loadedPath)
					{
						enz=(Enzyme)allElements.get(vertexLabel);
					}
					else
					{
						enz=(Enzyme)pw.getNodeByLabel(vertexLabel);
					}
				}
				if (countSubstrates>0)
				{
					
					for (DBColumn column_sub : substrate)
					{
						String[] s=column_sub.getColumn();
						edge.addSubstrate(s[0]);
						if (keggReaction)
						{
							if (s[0].startsWith("C"))
							{
								if (!ht.containsKey(s[0]))
								{
									ht.put(s[0], s[0]);
									String[] subDet={s[0], "Compound", "KEGG"};
									newLoadedElements.add(new DBColumn(subDet));

									// add object type to the elementsVector of
									// related elements
									checkObjects("Compound");

									sub=new CompoundNode(s[0], "", null);
									sub.setAbstract(false);
									sub.setReference(false);
									sub.setDB(db);
									allElements.put(s[0], sub);

									createDAWISNode(sub);
									CompoundNode cn=(CompoundNode)sub;

									addAllIDsToElementsHashtable(cn);

								}
								else
								{
									String vertexLabel=ht.get(s[0]);
									if (!loadedPath)
									{
										sub=(CompoundNode)allElements.get(vertexLabel);
									}
									else
									{
										sub=(CompoundNode)pw.getNodeByLabel(vertexLabel);
									}
								}
							}
							else if (s[0].startsWith("G"))
							{

								if (!ht.containsKey(s[0]))
								{
									ht.put(s[0], s[0]);

									String[] subDet={s[0], "Glycan", "KEGG"};
									newLoadedElements.add(new DBColumn(subDet));

									// add object type to the elementsVector of
									// related elements
									checkObjects("Glycan");

									sub=new Glycan(s[0], "", null);
									sub.setAbstract(false);
									sub.setReference(false);
									sub.setDB(db);
									allElements.put(s[0], sub);

									createDAWISNode(sub);

									addAllIDsToElementsHashtable(sub);

								}
								else
								{
									String vertexLabel=ht.get(s[0]);
									if (!loadedPath)
									{
										sub=(Glycan)allElements.get(vertexLabel);
									}
									else
									{
										sub=(Glycan)pw.getNodeByLabel(vertexLabel);
									}

								}

							}
						}
						else if (transpathReaction)
						{
							if (s[0].startsWith("M"))
							{
								if (!ht.containsKey(s[0]))
								{
									ht.put(s[0], s[0]);
									String[] subDet={s[0], "Compound", "Transpath"};
									newLoadedElements.add(new DBColumn(subDet));

									// add object type to the elementsVector of
									// related elements
									checkObjects("Compound");

									sub=new CompoundNode(s[0], "", null);
									sub.setAbstract(false);
									sub.setReference(false);
									sub.setDB(db);
									allElements.put(s[0], sub);

									createDAWISNode(sub);
									CompoundNode cn=(CompoundNode)sub;

									addAllIDsToElementsHashtable(cn);

								}
								else
								{
									String vertexLabel=ht.get(s[0]);
									if (!loadedPath)
									{
										sub=(CompoundNode)allElements.get(vertexLabel);
									}
									else
									{
										sub=(CompoundNode)pw.getNodeByLabel(vertexLabel);
									}

								}
							}
							else if (s[0].startsWith("G"))
							{

								if (!ht.containsKey(s[0]))
								{
									ht.put(s[0], s[0]);

									String[] subDet={s[0], "Gene", "Transpath"};
									newLoadedElements.add(new DBColumn(subDet));

									// add object type to the elementsVector of
									// related elements
									checkObjects("Gene");

									sub=new Gene(s[0], "", null);
									sub.setAbstract(false);
									sub.setReference(false);
									sub.setDB(db);
									allElements.put(s[0], sub);
									createDAWISNode(sub);
									Gene gn=(Gene)sub;
									gn.lookUpAtAllDatabases();

									addAllIDsToElementsHashtable(gn);

								}
								else
								{
									String vertexLabel=ht.get(s[0]);
									if (!loadedPath)
									{
										sub=(Gene)allElements.get(vertexLabel);
									}
									else
									{
										sub=(Gene)pw.getNodeByLabel(vertexLabel);
									}

								}
							}
						}
						String[] entry={sub.getLabel(), enz.getLabel(), "True"};
						edges.add(entry);

						keggEdges.put(sub.getLabel()+enz.getLabel(), edge);

					}

				}

				if (countProducts>0)
				{
					
					for (DBColumn column_prod : products)
					{
						String[] p=column_prod.getColumn();
						edge.addProduct(p[0]);

						if (keggReaction)
						{
							if (p[0].startsWith("C"))
							{
								if (!ht.containsKey(p[0]))
								{
									ht.put(p[0], p[0]);

									String[] subDet={p[0], "Compound", "KEGG"};
									newLoadedElements.add(new DBColumn(subDet));

									// add object type to the elementsVector of
									// related elements
									checkObjects("Compound");

									prod=new CompoundNode(p[0], "", null);
									prod.setAbstract(false);
									prod.setReference(false);
									prod.setDB(db);
									allElements.put(p[0], prod);

									createDAWISNode(prod);
									CompoundNode cn=(CompoundNode)prod;

									addAllIDsToElementsHashtable(cn);

								}
								else
								{
									String vertexLabel=ht.get(p[0]);
									if (!loadedPath)
									{
										prod=(CompoundNode)allElements.get(vertexLabel);
									}
									else
									{
										prod=(CompoundNode)pw.getNodeByLabel(vertexLabel);
									}

								}
							}
							else if (p[0].startsWith("G"))
							{

								if (!ht.containsKey(p[0]))
								{
									ht.put(p[0], p[0]);
									String[] subDet={p[0], "Glycan", "KEGG"};
									newLoadedElements.add(new DBColumn(subDet));

									// add object type to the elementsVector of
									// related elements
									checkObjects("Glycan");

									prod=new Glycan(p[0], "", null);
									prod.setAbstract(false);
									prod.setReference(false);
									prod.setDB(db);
									allElements.put(p[0], prod);

									createDAWISNode(prod);
									Glycan gn=(Glycan)prod;

									addAllIDsToElementsHashtable(gn);

								}
								else
								{
									String vertexLabel=ht.get(p[0]);
									if (!loadedPath)
									{
										prod=(Glycan)allElements.get(vertexLabel);
									}
									else
									{
										prod=(Glycan)pw.getNodeByLabel(vertexLabel);
									}

								}
							}
						}
						else if (transpathReaction)
						{
							if (p[0].startsWith("M"))
							{
								if (!ht.containsKey(p[0]))
								{
									ht.put(p[0], p[0]);
									String[] subDet={p[0], "Compound", "Transpath"};
									newLoadedElements.add(new DBColumn(subDet));

									// add object type to the elementsVector of
									// related elements
									checkObjects("Compound");

									prod=new CompoundNode(p[0], "", null);
									prod.setAbstract(false);
									prod.setReference(false);
									prod.setDB(db);
									allElements.put(p[0], prod);
									createDAWISNode(prod);

									addAllIDsToElementsHashtable(prod);

								}
								else
								{
									String vertexLabel=ht.get(p[0]);
									if (!loadedPath)
									{
										prod=(CompoundNode)allElements.get(vertexLabel);
									}
									else
									{
										prod=(CompoundNode)pw.getNodeByLabel(vertexLabel);
									}

								}

							}
							else if (p[0].startsWith("G"))
							{

								if (!ht.containsKey(p[0]))
								{
									ht.put(p[0], p[0]);
									String[] subDet={p[0], "Gene", "Transpath"};
									newLoadedElements.add(new DBColumn(subDet));

									// add object type to the elementsVector of
									// related elements
									checkObjects("Gene");

									prod=new Gene(p[0], "", null);
									prod.setAbstract(false);
									prod.setReference(false);
									prod.setDB(db);
									allElements.put(p[0], prod);
									createDAWISNode(prod);
									Gene gn=(Gene)prod;
									gn.lookUpAtAllDatabases();

									addAllIDsToElementsHashtable(gn);

								}
								else
								{
									String vertexLabel=ht.get(p[0]);
									if (!loadedPath)
									{
										prod=(Gene)allElements.get(vertexLabel);
									}
									else
									{
										prod=(Gene)pw.getNodeByLabel(vertexLabel);
									}

								}
							}
						}
						String[] entry={enz.getLabel(), prod.getLabel(), "True"};
						edges.add(entry);
						keggEdges.put(enz.getLabel()+prod.getLabel(), edge);

					}
				}
				if (countSubstrates==0&countProducts==0)
				{
					if (!ht.containsKey(singleReaction))
					{
						ht.put(singleReaction, singleReaction);

						Reaction r=new Reaction(singleReaction, "", null);
						r.setAbstract(false);
						r.setReference(false);
						r.setDB(db);
						allElements.put(singleReaction, r);
						createDAWISNode(r);
						Reaction rn=(Reaction)r;

						addAllIDsToElementsHashtable(rn);

						String[] entry={r.getLabel(), enz.getLabel(), "False"};
						edges.add(entry);
					}

				}
			}

		}
		else
		{

			// create non-enzyme reaction
			if (keggReaction)
			{
				if (countSubstrates>0)
				{
					
					for (DBColumn column_sub : substrate)
					{
						String[] s=column_sub.getColumn();
						edge.addSubstrate(s[0]);

						if (s[0].startsWith("C"))
						{

							if (!ht.containsKey(s[0]))
							{
								ht.put(s[0], s[0]);

								// add object type to the elementsVector of
								// related
								// elements
								checkObjects("Compound");

								sub=new CompoundNode(s[0], "", null);
								sub.setAbstract(false);
								sub.setReference(false);
								sub.setDB(db);
								allElements.put(s[0], sub);

								createDAWISNode(sub);

								addAllIDsToElementsHashtable(sub);

							}
							else
							{
								String vertexLabel=ht.get(s[0]);
								if (!loadedPath)
								{
									sub=(CompoundNode)allElements.get(vertexLabel);
								}
								else
								{
									sub=(CompoundNode)pw.getNodeByLabel(vertexLabel);
								}

							}

						}
						else if (s[0].startsWith("G"))
						{

							if (!ht.containsKey(s[0]))
							{
								ht.put(s[0], s[0]);

								// add object type to the elementsVector of
								// related
								// elements
								checkObjects("Glycan");

								sub=new Glycan(s[0], "", null);
								sub.setAbstract(false);
								sub.setReference(false);
								sub.setDB(db);
								allElements.put(s[0], sub);

								// data.addElementToTable(sub.getLabel(), sub
								// .getVertex());
								createDAWISNode(sub);
								Glycan gn=(Glycan)sub;

								addAllIDsToElementsHashtable(gn);

							}
							else
							{
								String vertexLabel=ht.get(s[0]);
								if (!loadedPath)
								{
									sub=(Glycan)allElements.get(vertexLabel);
								}
								else
								{
									sub=(Glycan)pw.getNodeByLabel(vertexLabel);
								}

							}

						}

						if (countProducts>0)
						{
							
							for (DBColumn column_prod : products)
							{
								String[] p=column_prod.getColumn();
								edge.addProduct(p[0]);

								if (p[0].startsWith("C"))
								{
									if (!ht.containsKey(p[0]))
									{
										ht.put(s[0], s[0]);

										// add object type to the elementsVector
										// of
										// related elements
										checkObjects("Compound");

										prod=new CompoundNode(p[0], "", null);
										prod.setAbstract(false);
										prod.setReference(false);
										prod.setDB(db);
										allElements.put(p[0], prod);
										createDAWISNode(prod);

										addAllIDsToElementsHashtable(prod);

									}
									else
									{
										String vertexLabel=ht.get(p[0]);
										if (!loadedPath)
										{
											prod=(CompoundNode)allElements.get(vertexLabel);
										}
										else
										{
											prod=(CompoundNode)pw.getNodeByLabel(vertexLabel);
										}

									}

								}
								else if (p[0].startsWith("G"))
								{

									if (!ht.containsKey(p[0]))
									{
										ht.put(p[0], p[0]);

										// add object type to the elementsVector
										// of
										// related elements
										checkObjects("Glycan");

										prod=new Glycan(p[0], "", null);
										prod.setAbstract(false);
										prod.setReference(false);
										prod.setDB(db);
										allElements.put(p[0], prod);
										createDAWISNode(prod);

										addAllIDsToElementsHashtable(prod);

									}
									else
									{
										String vertexLabel=ht.get(p[0]);
										if (!loadedPath)
										{
											prod=(Glycan)allElements.get(vertexLabel);
										}
										else
										{
											prod=(Glycan)pw.getNodeByLabel(vertexLabel);
										}

									}

								}
								String[] entry={sub.getLabel(), prod.getLabel(), "True"};
								edges.add(entry);
								keggEdges.put(sub.getLabel()+prod.getLabel(), edge);

							}
						}
					}
				}
			}
			else if (transpathReaction)
			{

				if (countSubstrates>0)
				{	
					for (DBColumn column_sub : substrate)
					{
						String[] s=column_sub.getColumn();
						edge.addSubstrate(s[0]);

						if (s[0].startsWith("M"))
						{

							if (!ht.containsKey(s[0]))
							{
								ht.put(s[0], s[0]);
								// add object type to the elementsVector of
								// related
								// elements
								checkObjects("Compound");

								sub=new CompoundNode(s[0], "", null);
								sub.setAbstract(false);
								sub.setReference(false);
								sub.setDB(db);
								allElements.put(s[0], sub);
								createDAWISNode(sub);
								CompoundNode cn=(CompoundNode)sub;

								addAllIDsToElementsHashtable(cn);

							}
							else
							{
								String vertexLabel=ht.get(s[0]);
								if (!loadedPath)
								{
									sub=(CompoundNode)allElements.get(vertexLabel);
								}
								else
								{
									sub=(CompoundNode)pw.getNodeByLabel(vertexLabel);
								}

							}

						}
						else if (s[0].startsWith("G"))
						{

							if (!ht.containsKey(s[0]))
							{
								ht.put(s[0], s[0]);
								// add object type to the elementsVector of
								// related
								// elements
								checkObjects("Gene");

								sub=new Gene(s[0], "", null);
								sub.setAbstract(false);
								sub.setReference(false);
								sub.setDB(db);
								allElements.put(s[0], sub);
								createDAWISNode(sub);
								Gene gn=(Gene)sub;
								gn.lookUpAtAllDatabases();

								addAllIDsToElementsHashtable(gn);
							}
							else
							{
								String vertexLabel=ht.get(s[0]);
								if (!loadedPath)
								{
									sub=(Gene)allElements.get(vertexLabel);
								}
								else
								{
									sub=(Gene)pw.getNodeByLabel(vertexLabel);
								}

							}

						}

						if (countProducts>0)
						{
							
							for (DBColumn column_prod : products)
							{
								String[] p=column_prod.getColumn();
								edge.addProduct(p[0]);

								if (p[0].startsWith("M"))
								{
									if (!ht.containsKey(s[0]))
									{
										ht.put(s[0], s[0]);
										// add object type to the elementsVector
										// of
										// related elements
										checkObjects("Compound");

										prod=new CompoundNode(p[0], "", null);
										prod.setAbstract(false);
										prod.setReference(false);
										prod.setDB(db);
										allElements.put(p[0], prod);
										createDAWISNode(prod);
										CompoundNode cn=(CompoundNode)prod;

										addAllIDsToElementsHashtable(cn);

									}
									else
									{
										String vertexLabel=ht.get(p[0]);
										if (!loadedPath)
										{
											prod=(CompoundNode)allElements.get(vertexLabel);
										}
										else
										{
											prod=(CompoundNode)pw.getNodeByLabel(vertexLabel);
										}

									}

								}
								else if (p[0].startsWith("G"))
								{

									if (!ht.containsKey(p[0]))
									{
										ht.put(p[0], p[0]);

										// add object type to the elementsVector
										// of
										// related elements
										checkObjects("Gene");

										prod=new Gene(p[0], "", null);
										prod.setAbstract(false);
										prod.setReference(false);
										prod.setDB(db);
										allElements.put(p[0], prod);
										createDAWISNode(prod);
										Gene gn=(Gene)prod;
										gn.lookUpAtAllDatabases();

										addAllIDsToElementsHashtable(gn);

									}
									else
									{
										String vertexLabel=ht.get(p[0]);
										if (!loadedPath)
										{
											prod=(Gene)allElements.get(vertexLabel);
										}
										else
										{
											prod=(Gene)pw.getNodeByLabel(vertexLabel);
										}

									}
								}
								String[] entry={sub.getLabel(), prod.getLabel(), "True"};
								edges.add(entry);
								keggEdges.put(sub.getLabel()+prod.getLabel(), edge);

							}
						}
					}
				}
			}

		}
	}

//	private void pasteElementsToVector(Vector<String> sub, Vector<String[]> substrate)
//	{
//		Iterator<String> it=sub.iterator();
//		while (it.hasNext())
//		{
//			String[] elem={it.next()};
//			substrate.add(elem);
//		}
//	}

	private void fillKEGGEdgeWithTPElementInformation(KEGGEdge edge)
	{

		String[] elem={edge.getKEEGReactionID()};
		ArrayList<DBColumn> reactionDetails=new Wrapper().requestDbContent(3, DAWISQueries.getTranspathReactionDetails, elem);

		for (DBColumn column : reactionDetails)
		{
			String[] det=column.getColumn();

			edge.setEffect(det[0]);
			edge.setReactionType(det[1]);
		}

		ArrayList<DBColumn> reactionComment=new Wrapper().requestDbContent(3, DAWISQueries.getTranspathReactionComment, elem);
		
		for (DBColumn column : reactionComment)
		{
			String[] det=column.getColumn();
			edge.setComment(det[0]);
		}

		ArrayList<DBColumn> reactionCatalysts=new Wrapper().requestDbContent(3, DAWISQueries.getTRANSPATHReactionCatalysts, elem);
		
		for (DBColumn column : reactionCatalysts)
		{
			String[] det=column.getColumn();
			
			edge.setCatalysts(det[0]);
			edge.setCatalystsName(det[1]);
		}

		ArrayList<DBColumn> reactionInhibitors=new Wrapper().requestDbContent(3, DAWISQueries.getTRANSPATHReactionInhibitors, elem);
		
		for (DBColumn column : reactionInhibitors)
		{
			String[] det=column.getColumn();
			
			edge.setInhibitors(det[0]);
			edge.setInhibitorsName(det[1]);
		}

	}

	/**
	 * add the object type to the elementsVector of other objects if needed
	 * 
	 * @param object
	 */
	private void checkObjects(String string)
	{

		Iterator<String> objectIterator=data.getObjects().iterator();
		while (objectIterator.hasNext())
		{
			String elem=(String)objectIterator.next();
			if (elem.equals("Compound"))
			{
				if (string.equals("Enzyme")|string.equals("Gene")|string.equals("Protein"))
				{
					if (!data.getCompoundRelatedDomains().contains(string))
					{
						data.addElementToCompoundVector(string);
					}
				}
			}
			else if (elem.equals("Enzyme"))
			{
				if (string.equals("Compound"))
				{
					if (!data.getEnzymeRelatedDomains().contains("Compound"))
					{
						data.addElementToEnzymeVector("Compound");
					}
				}
				if (string.equals("Glycan"))
				{
					if (!data.getEnzymeRelatedDomains().contains("Glycan"))
					{
						data.addElementToEnzymeVector("Glycan");
					}
				}
			}
			else if (elem.equals("Pathway"))
			{
				if (string.equals("Enzyme"))
				{
					if (!data.getPathwayRelatedDomains().contains("Enzyme"))
					{
						data.addElementToPathwayVector("Enzyme");
					}
				}
				if (string.equals("Glycan"))
				{
					if (!data.getPathwayRelatedDomains().contains("Glycan"))
					{
						data.addElementToPathwayVector("Glycan");
					}
				}
				if (string.equals("Compound"))
				{
					if (!data.getPathwayRelatedDomains().contains("Compound"))
					{
						data.addElementToPathwayVector("Compound");
					}
				}
			}
			else if (elem.equals("Glycan"))
			{
				if (string.equals("Enzyme"))
				{
					if (data.getGlycanRelatedDomains().contains("Enzyme"))
					{
						data.addElementToGlycanVector("Enzyme");
					}
				}
			}
			else if (elem.equals("Gene"))
			{
				if (string.equals("Enzyme"))
				{
					if (!data.getGeneRelatedDomains().contains("Enzyme"))
					{
						data.addElementToGeneVector("Enzyme");
					}
				}
				if (string.equals("Compound"))
				{
					if (!data.getGeneRelatedDomains().contains("Compound"))
					{
						data.addElementToGeneVector("Compound");
					}
				}
			}
			else if (elem.equals("Gene Ontology"))
			{
				if (string.equals("Enzyme"))
				{
					if (!data.getGeneOntologyRelatedDomains().contains("Enzyme"))
					{
						data.addElementToGeneOntologyVector("Enzyme");
					}
				}
				if (string.equals("Gene"))
				{
					if (!data.getGeneOntologyRelatedDomains().contains("Gene"))
					{
						data.addElementToGeneOntologyVector("Gene");
					}
				}
			}
		}
	}

	/**
	 * fill KEGGEdge with element information
	 * 
	 * @param edge
	 */
	private void fillKEGGEdgeWithElementInformation(KEGGEdge edge)
	{

		String[] elem={edge.getKEEGReactionID()};
		ArrayList<DBColumn> reactionDetails=new Wrapper().requestDbContent(3, DAWISQueries.getReactionDetails, elem);

		for (DBColumn column : reactionDetails)
		{
			String[] det=column.getColumn();
			
			edge.setName(det[4]);
			edge.setRemark(det[6]);
			edge.setReference(det[5]);
			edge.setComment(det[1]);
			edge.setDefinition(det[2]);
			edge.setEquation(det[3]);
			edge.setRpair(det[7]);
		}

		ArrayList<DBColumn> reactionType=new Wrapper().requestDbContent(3, DAWISQueries.getReactionType, elem);
		
		for (DBColumn column : reactionType)
		{
			String[] det=column.getColumn();
			
			edge.setReactionType(det[0]);
		}

		ArrayList<DBColumn> reactionOrthology=new Wrapper().requestDbContent(3, DAWISQueries.getReactionOrthology, elem);
		
		for (DBColumn column : reactionOrthology)
		{
			String[] det=column.getColumn();
			
			edge.setOrthology(det[1]+": "+det[2]);
		}
	}

	/**
	 * test dependences between elements
	 * 
	 * @param element
	 *            data: <element_id, object, databaseName>
	 * @param vector
	 *            with all loaded element_ids
	 * @param vector
	 *            of related objects
	 */
	private void testDependences(String[] elem, Vector<String> relatedElementsVector)
	{

		Iterator<String[]> it1=data.getElements().iterator();
		while (it1.hasNext())
		{
			String[] singleElementDet=(String[])it1.next();

			if (relatedElementsVector.contains(singleElementDet[1]))
			{
				testRelation2(elem, singleElementDet);
			}
		}
	}

	private void testRelation2(String[] elem, String[] singleElementDet)
	{

		String query="";
		ArrayList<DBColumn> result=null;

		String object=elem[1];
		String id=elem[0];

		Hashtable<String, String> idRelations=data.getIdIdRelationsAsHashtable();
		if (idRelations!=null)
		{
			if (idRelations.get(id)!=null)
			{
				id=idRelations.get(id);
			}
		}

		BiologicalNodeAbstract one=null;

		if (allElements.containsKey(id))
		{
			one=(BiologicalNodeAbstract)allElements.get(id);
		}
		else
		{
			if (pw.getAllNodeLabels().contains(id))
			{
				one=(BiologicalNodeAbstract)pw.getNodeByLabel(id);
			}
		}

		String testObject=singleElementDet[1];
		String testID=singleElementDet[0];

		if (idRelations!=null)
		{
			if (idRelations.get(singleElementDet[0])!=null)
			{

				testID=idRelations.get(singleElementDet[0]);

			}
		}

		BiologicalNodeAbstract two=null;

		if (allElements.containsKey(testID))
		{

			two=(BiologicalNodeAbstract)allElements.get(testID);

		}
		else
		{
			if (pw.getAllNodeLabels().contains(testID))
			{

				two=(BiologicalNodeAbstract)pw.getNodeByLabel(testID);

			}
		}

		if (one!=null&two!=null)
		{

			String actualDB="";
			String actualONE="";
			Hashtable<String, String> ht=one.getDAWISNode().getAllIDDBRelationsAsHashtable();
			Set<String> s=ht.keySet();
			Iterator<String> it=s.iterator();

			String actualTestDB="";
			String actualTWO="";

			Hashtable<String, String> ht2=two.getDAWISNode().getAllIDDBRelationsAsHashtable();

			Set<String> s2=ht2.keySet();
			Iterator<String> it2=s2.iterator();

			while (it.hasNext())
			{
				actualDB=it.next();

				actualONE=ht.get(actualDB);

				while (it2.hasNext())
				{
					actualTestDB=it2.next();

					actualTWO=ht2.get(actualTestDB);

					if (object.equals("Pathway"))
					{
						if (testObject.equals("Pathway"))
						{
							if (actualDB.equalsIgnoreCase("Transpath"))
							{
								if (actualTestDB.equalsIgnoreCase("Transpath"))
								{
									query=DAWISQueries.getTPPathwayFromTPPathway2+"'"+actualONE+"' and tpp.super_id = '"+actualTWO+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (actualDB.equalsIgnoreCase("KEGG"))
							{
								if (actualTestDB.equalsIgnoreCase("KEGG"))
								{
									query=DAWISQueries.getPathwayFromPathway2+"'"+actualONE+"' and ken.entry_name = '"+actualTWO+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}

						}
						else if (testObject.equals("Enzyme"))
						{
							query=DAWISQueries.getEnzymeFromPathway2+"'"+actualONE+"' and ke.entry = '"+actualTWO+"'";
							result=new Wrapper().requestDbContent(3, query);
						}
						else if (testObject.equals("Gene"))
						{
							if (actualDB.equalsIgnoreCase("KEGG"))
							{

								if (actualTestDB.equalsIgnoreCase("KEGG"))
								{

									query=DAWISQueries.getGeneFromPathway2+"'"+actualONE+"' and kgp.entry = '"+actualTWO+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
						}
						else if (testObject.equals("Drug"))
						{
							if (actualDB.equalsIgnoreCase("KEGG"))
							{
								String number=getPathwayNumber(actualONE);

								query=DAWISQueries.getPathwayFromDrug2+"'"+actualTWO+"' and kpd.kegg_number = '"+number+"'";
								result=new Wrapper().requestDbContent(3, query);
							}

						}
						else if (testObject.equals("Compound"))
						{
							if (actualDB.equalsIgnoreCase("Transpath"))
							{
								if (actualTestDB.equalsIgnoreCase("Transpath"))
								{
									query=DAWISQueries.getCompoundFromTPPathway2+"'"+actualONE+"' and t.molecule_id = '"+actualTWO+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (actualDB.equalsIgnoreCase("KEGG"))
							{
								if (actualTestDB.equalsIgnoreCase("KEGG"))
								{
									query=DAWISQueries.getCompoundFromPathway+"'"+actualONE+"' and kpc.entry = '"+actualTWO+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}

						}
						else if (testObject.equals("Glycan"))
						{
							query=DAWISQueries.getGlycanFromPathway2+"'"+actualONE+"' and kpg.entry = '"+actualTWO+"'";
							result=new Wrapper().requestDbContent(3, query);
						}
						else if (testObject.equals("Reaction"))
						{
							if (actualDB.equalsIgnoreCase("Transpath"))
							{
								if (actualTestDB.equalsIgnoreCase("Transpath"))
								{
									query=DAWISQueries.getTRANSPATHReactionFromPathway2+"'"+actualONE+"' and pri.reaction_id = '"+actualTWO+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (actualDB.equalsIgnoreCase("KEGG"))
							{
								if (actualTestDB.equalsIgnoreCase("KEGG"))
								{
									query=DAWISQueries.getReactionFromPathway2+"'"+actualONE+"' and kpr.entry = '"+actualTWO+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}

						}
					}
					else if (object.equals("Enzyme"))
					{
						if (testObject.equals("Pathway"))
						{
							if (actualTestDB.equalsIgnoreCase("KEGG"))
							{
								query=DAWISQueries.getEnzymeFromPathway2+"'"+actualTWO+"' and ke.entry = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (testObject.equals("Compound"))
						{
							if (actualTestDB.equalsIgnoreCase("Transpath"))
							{
								query=DAWISQueries.getEnzymeFromTranspathCompound+"'"+actualTWO+"' and tpl.database_identifier = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (actualTestDB.equalsIgnoreCase("KEGG"))
							{
								query=DAWISQueries.getCompoundFromEnzyme2+"'"+actualONE+"' and kce.entry = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (testObject.equals("Glycan"))
						{
							query=DAWISQueries.getGlycanFromEnzyme2+"'"+actualONE+"' and kge.entry = '"+actualTWO+"'";
							result=new Wrapper().requestDbContent(3, query);
						}
						else if (testObject.equals("Reaction"))
						{
							if (actualTestDB.equalsIgnoreCase("KEGG"))
							{
								query=DAWISQueries.getReactionFromEnzyme2+"'"+actualONE+"' and kre.entry = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (testObject.equals("Gene"))
						{
							if (actualTestDB.equalsIgnoreCase("KEGG"))
							{
								query=DAWISQueries.getGeneFromEnzyme2+"'"+actualONE+"' and g.entry = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (actualTestDB.equalsIgnoreCase("Transpath"))
							{
								query=DAWISQueries.getTPGeneFromEnzyme2+"'"+actualONE+"' and tpgl.gene_id = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (actualTestDB.equalsIgnoreCase("Transfac"))
							{
								query=DAWISQueries.getTFGeneFromEnzyme2+"'"+actualONE+"' and t.gene_id = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (testObject.equals("Gene Ontology"))
						{
							query=DAWISQueries.getGOFromEnzyme2+"'"+actualONE+"' and gt.acc = '"+actualTWO+"'";
							result=new Wrapper().requestDbContent(3, query);
						}
						else if (testObject.equals("Protein"))
						{
							query=DAWISQueries.getProteinFromEnzyme2+"'"+actualONE+"' and eeu.enzyme_id = '"+actualTWO+"'";
							result=new Wrapper().requestDbContent(3, query);
						}

					}
					else if (object.equals("Protein"))
					{
						if (actualDB.equalsIgnoreCase("HPRD"))
						{
							if (testObject.equals("Disease"))
							{
								query=DAWISQueries.getHPRDProteinFromDisease2+"'"+actualTWO+"' and hhim.HPRD_ID = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Gene"))
							{
								if (actualTestDB.equalsIgnoreCase("KEGG"))
								{
									query=DAWISQueries.getHPRDProteinFromGene2+"'"+actualTWO+"' and hhim.HPRD_ID = '"+actualONE+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (testObject.equals("Gene Ontology"))
							{
								query=DAWISQueries.getGONumberFromHPRDProtein+"'"+actualONE+"' and go_number = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (actualDB.equalsIgnoreCase("UniProt"))
						{
							if (testObject.equals("Disease"))
							{
								query=DAWISQueries.getProteinFromDisease+" ud.uniprot_id = '"+actualONE+"' and ud.primary_id = ' "+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Gene"))
							{
								if (actualTestDB.equalsIgnoreCase("EMBL"))
								{
									query=DAWISQueries.getEMBLGeneFromUniProtProtein2+"'"+actualONE+"' or ua.uniprot_id = '"+actualONE+"') and u.primary_id = ' "+actualTWO+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
								else if (actualTestDB.equalsIgnoreCase("KEGG"))
								{
									query=DAWISQueries.getGeneFromUniProtProtein2+"'"+actualONE+"' or a.uniprot_id = '"+actualONE+"') and g.entry = '"+actualTWO+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (testObject.equals("Gene Ontology"))
							{
								query=DAWISQueries.getGOIDFromProtein2+"'"+actualONE+"' and ud.primary_id = ' "+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Compound"))
							{
								if (actualTestDB.equalsIgnoreCase("Transpath"))
								{
									query=DAWISQueries.getProteinFromTPCompound2+"'"+actualTWO+"' and (ua.uniprot_id = ' "+actualONE+"' or ua.accession_number = '"+actualONE+"')";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (testObject.equals("Enzyme"))
							{
								query=DAWISQueries.getEnzymeFromProtein2+"'"+actualONE+"' or eeu.uniprot_id = '"+actualONE+"') and eeu.enzyme_id = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							if (testObject.equals("Factor"))
							{
								query=DAWISQueries.getProteinFromFactor+"'"+actualONE+"' and tl.accession_number = ' "+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}

					}
					else if (object.equals("Gene"))
					{
						if (actualDB.equalsIgnoreCase("Transpath"))
						{
							if (testObject.equals("Disease"))
							{
								query=DAWISQueries.getDiseaseFromTRANSPATHGene2+"'"+actualONE+"' and tpedl.database_identifier = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Compound"))
							{
								if (actualTestDB.equalsIgnoreCase("Transpath"))
								{
									query=DAWISQueries.getTPGeneFromTPCompound2+"'"+actualTWO+"' and tpgd.gene_id = '"+actualONE+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (testObject.equals("Enzyme"))
							{
								query=DAWISQueries.getEnzymeFromTranspathGene2+"'"+actualONE+"' and ge.enzyme = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}

						}
						else if (actualDB.equalsIgnoreCase("Transfac"))
						{
							if (testObject.equals("Disease"))
							{
								query=DAWISQueries.getDiseaseFromTRANSFACGene2+"'"+actualONE+"' and tpedl.database_identifier = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Enzyme"))
							{
								query=DAWISQueries.getEnzymeFromTransfacGene2+"'"+actualONE+"' and database_id = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Factor"))
							{
								query=DAWISQueries.getTFGeneFactor+"'"+actualTWO+"' and t.chip_id = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Fragment"))
							{
								query=DAWISQueries.getTFGeneFragment+"'"+actualONE+"' and t.factor_id = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Site"))
							{
								query=DAWISQueries.getTFGeneSite+"'"+actualONE+"' and s.site_id = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (actualDB.equalsIgnoreCase("EMBL"))
						{
							if (testObject.equals("Protein"))
							{
								if (actualTestDB.equalsIgnoreCase("UniProt"))
								{
									query=DAWISQueries.getEMBLGeneFromUniProtProtein2+"'"+actualTWO+"' or ua.uniprot_id = '"+actualTWO+"') and u.primary_id = ' "+actualONE+"'";
									result=new Wrapper().requestDbContent(3, query);

								}
							}
							else if (testObject.equals("Compound"))
							{
								if (actualTestDB.equalsIgnoreCase("Transpath"))
								{
								}
							}
							else if (testObject.equals("Factor"))
							{
								query=DAWISQueries.getEMBLGeneFromFactor+"'"+actualONE+"' and  = ' "+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (actualDB.equalsIgnoreCase("KEGG"))
						{
							if (testObject.equals("Disease"))
							{
								query=DAWISQueries.getDiseaseFromGene2+"'"+actualONE+"' and d.MIM = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Protein"))
							{
								if (actualTestDB.equalsIgnoreCase("UniProt"))
								{
									query=DAWISQueries.getGeneFromUniProtProtein2+"'"+actualTWO+"' and g.entry = '"+actualONE+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
								else if (actualTestDB.equalsIgnoreCase("HPRD"))
								{
									query=DAWISQueries.getGeneFromHPRDProtein+"'"+actualTWO+"' and hm.entrezgene_id = '"+actualONE+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (testObject.equals("Enzyme"))
							{
								query=DAWISQueries.getGeneFromEnzyme2+"'"+actualTWO+"' and g.entry = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Pathway"))
							{
								if (actualTestDB.equalsIgnoreCase("KEGG"))
								{
									query=DAWISQueries.getGeneFromPathway2+"'"+actualTWO+"' and kgp.entry "+" = '"+actualONE+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
						}
					}
					else if (object.equals("Gene Ontology"))
					{
						if (testObject.equals("Protein"))
						{
							if (actualTestDB.equalsIgnoreCase("HPRD"))
							{
								query=DAWISQueries.getGONumberFromHPRDProtein+"'"+actualTWO+"' and go_number = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (actualTestDB.equalsIgnoreCase("UniProt"))
							{
								query=DAWISQueries.getGOIDFromProtein2+"'"+actualTWO+"' and ud.primary_id = ' "+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (testObject.equals("Enzyme"))
						{
							query=DAWISQueries.getGOFromEnzyme2+"'"+actualTWO+"' and gt.acc = '"+actualONE+"'";
							result=new Wrapper().requestDbContent(3, query);
						}
						else if (testObject.equals("Compound"))
						{

							if (actualTestDB.equalsIgnoreCase("Transpath"))
							{
								query=DAWISQueries.getTRANSPATHCompoundFromGeneOntology2+"'"+actualONE+"' and tpml.molecule_id = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
					}
					else if (object.equals("Disease"))
					{
						if (testObject.equals("Protein"))
						{
							if (actualTestDB.equalsIgnoreCase("HPRD"))
							{
								query=DAWISQueries.getHPRDProteinFromDisease2+"'"+actualONE+"' and hhim.HPRD_ID = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (actualTestDB.equalsIgnoreCase("KEGG"))
							{
								query=DAWISQueries.getProteinFromDisease+"ud.primary_id = ' "+actualTWO+"' and ud.uniprot_id = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (testObject.equals("Gene"))
						{
							if (actualTestDB.equalsIgnoreCase("Transpath"))
							{
								query=DAWISQueries.getDiseaseFromTRANSPATHGene2+"'"+actualTWO+"' and tpedl.database_identifier = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (actualTestDB.equalsIgnoreCase("Transfac"))
							{
								query=DAWISQueries.getDiseaseFromTRANSFACGene2+"'"+actualTWO+"' and tpedl.database_identifier = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (actualTestDB.equalsIgnoreCase("KEGG"))
							{
								query=DAWISQueries.getDiseaseFromGene2+"'"+actualTWO+"' and d.MIM = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (testObject.equals("Compound"))
						{
							if (actualTestDB.equalsIgnoreCase("Transpath"))
							{
								query=DAWISQueries.getCompoundFromDisease2+"'"+actualTWO+"' and tpedl.database_identifier = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}

					}
					else if (object.equals("Glycan"))
					{
						if (testObject.equals("Pathway"))
						{
							if (actualTestDB.equalsIgnoreCase("KEGG"))
							{
								query=DAWISQueries.getGlycanFromPathway2+"'"+actualTWO+"' and kpg.entry "+" = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (testObject.equals("Reaction"))
						{
							if (actualTestDB.equalsIgnoreCase("KEGG"))
							{
								query=DAWISQueries.getReactionFromGlycan2+"'"+actualONE+"' and kgr.reaction = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}

						}
						else if (testObject.equals("Enzyme"))
						{
							query=DAWISQueries.getGlycanFromEnzyme2+"'"+actualTWO+"' and kge.entry = '"+actualONE+"'";
							result=new Wrapper().requestDbContent(3, query);
						}
					}
					else if (object.equals("Compound"))
					{
						if (actualDB.equalsIgnoreCase("Transpath"))
						{
							if (testObject.equals("Pathway"))
							{
								if (actualTestDB.equalsIgnoreCase("Transpath"))
								{
									query=DAWISQueries.getCompoundFromTPPathway2+"'"+actualTWO+"' and t.molecule_id = '"+actualONE+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (testObject.equals("Enzyme"))
							{
								query=DAWISQueries.getEnzymeFromTranspathCompound+"'"+actualONE+"' and tpl.database_identifier = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Reaction"))
							{
								if (actualTestDB.equalsIgnoreCase("Transpath"))
								{
									query=DAWISQueries.getCompoundFromTranspathReaction+"'"+actualTWO+"' and molecule_id  = '"+actualONE+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (testObject.equals("Protein"))
							{
								if (actualTestDB.equalsIgnoreCase("UniProt"))
								{
									query=DAWISQueries.getCompoundFromProtein2+"'"+actualTWO+"' or ua.accession_number = '"+actualTWO+"') and t.molecule_id  = '"+actualONE+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (testObject.equals("Gene"))
							{
								if (actualTestDB.equalsIgnoreCase("Transpath"))
								{
									query=DAWISQueries.getTRANSPATHCompoundFromGene2+"'"+actualTWO+"' and tpgd.product_id  = '"+actualONE+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
								else if (actualTestDB.equalsIgnoreCase("EMBL"))
								{
								}
							}
							else if (testObject.equals("Gene Ontology"))
							{
								query=DAWISQueries.getTRANSPATHCompoundFromGeneOntology2+"'"+actualTWO+"' and tpgo.molecule_id  = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Disease"))
							{
								query=DAWISQueries.getCompoundFromDisease2+"'"+actualTWO+"' and tpml.molecule_id  = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Factor"))
							{
								query=DAWISQueries.getCompoundFromFactor+"'"+actualONE+"' and tl.accession_number = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (actualDB.equalsIgnoreCase("KEGG"))
						{
							if (testObject.equals("Pathway"))
							{
								if (actualTestDB.equalsIgnoreCase("KEGG"))
								{
									query=DAWISQueries.getCompoundFromPathway+"'"+actualTWO+"' and kpc.entry = '"+actualONE+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (testObject.equals("Enzyme"))
							{
								query=DAWISQueries.getCompoundFromEnzyme2+"'"+actualTWO+"' and kce.entry = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (testObject.equals("Reaction"))
							{
								if (actualTestDB.equalsIgnoreCase("KEGG"))
								{
									query=DAWISQueries.getReactionFromCompound2+"'"+actualONE+"' and kcr.reaction  = '"+actualTWO+"'";
									result=new Wrapper().requestDbContent(3, query);
								}
							}
							else if (testObject.equals("Reaction Pair"))
							{
								query=DAWISQueries.getReactionPairFromCompound2+"'"+actualONE+"' and krc.entry = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
					}
					else if (object.equals("Reaction Pair"))
					{
						if (testObject.equals("Reaction"))
						{
							if (actualTestDB.equalsIgnoreCase("KEGG"))
							{
								query=DAWISQueries.getReactionFromReactionPair2+"'"+actualONE+"' and krr.reaction = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (testObject.equals("Reaction Pair"))
						{
							query=DAWISQueries.getReactionPairFromReactionPair2+"'"+actualONE+"' and krprp.relatedpair = '"+actualTWO+"'";
							result=new Wrapper().requestDbContent(3, query);
						}
						else if (testObject.equals("Compound"))
						{
							if (actualTestDB.equalsIgnoreCase("KEGG"))
							{
								query=DAWISQueries.getReactionPairFromCompound2+"'"+actualTWO+"' and krc.entry = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}

					}
					else if (object.equals("Drug"))
					{
						if (testObject.equals("Pathway"))
						{
							String number=getPathwayNumber(actualTWO);
							if (actualTestDB.equalsIgnoreCase("KEGG"))
							{
								query=DAWISQueries.getPathwayFromDrug2+"'"+actualONE+"' and kpd.kegg_number = '"+number+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
					}
					else if (object.equals("Factor"))
					{
						if (testObject.equals("Gene"))
						{
							if (actualTestDB.equalsIgnoreCase("Transfac"))
							{
								query=DAWISQueries.getTFGeneFactor+"'"+actualTWO+"' and t.factor_id = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
							else if (actualTestDB.equalsIgnoreCase("EMBL"))
							{
								query=DAWISQueries.getEMBLGeneFromFactor+"'"+actualONE+"' and tl.accession_number = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (testObject.equals("Fragment"))
						{
							query=DAWISQueries.getFragmentsOfFactor2+"'"+actualONE+"' and tfbf.fragment_id = '"+actualTWO+"'";
							result=new Wrapper().requestDbContent(3, query);
						}
						else if (testObject.equals("Compound"))
						{
							if (actualTestDB.equalsIgnoreCase("Transpath"))
							{
								query=DAWISQueries.getCompoundFromFactor+"'"+actualONE+"' and tl.accession_number = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}

						}
						else if (testObject.equals("Protein"))
						{
							if (actualTestDB.equalsIgnoreCase("UniProt"))
							{
								query=DAWISQueries.getProteinFromFactor+"'"+actualONE+"' and tl.accession_number = '"+actualTWO+"'";
								result=new Wrapper().requestDbContent(3, query);
							}

						}
						else if (testObject.equals("Matrix"))
						{

							query=DAWISQueries.getMatrixOfFactor+"'"+actualONE+"' and t.matrix_id = '"+actualTWO+"'";
							result=new Wrapper().requestDbContent(3, query);

						}
						else if (testObject.equals("Site"))
						{

							query=DAWISQueries.getSiteOfFactor+"'"+actualONE+"' and t.site_id = '"+actualTWO+"'";
							result=new Wrapper().requestDbContent(3, query);

						}
					}
					else if (object.equals("Fragment"))
					{
						if (testObject.equals("Factor"))
						{
							query=DAWISQueries.getFragmentsOfFactor2+"'"+actualTWO+"' and tfbf.fragment_id = '"+actualONE+"'";
							result=new Wrapper().requestDbContent(3, query);
						}
						else if (testObject.equals("Gene"))
						{
							query=DAWISQueries.getTFGeneFactor+"'"+actualONE+"' and t.chip_id = '"+actualTWO+"'";
							result=new Wrapper().requestDbContent(3, query);
						}
					}
					else if (object.equals("Site"))
					{
						if (testObject.equals("Gene"))
						{
							if (actualTestDB.equalsIgnoreCase("Transfac"))
							{
								query=DAWISQueries.getTFGeneSite+"'"+actualTWO+"' and s.site_id = '"+actualONE+"'";
								result=new Wrapper().requestDbContent(3, query);
							}
						}
						else if (testObject.equals("Factor"))
						{

							query=DAWISQueries.getSiteOfFactor+"'"+actualTWO+"' and t.site_id = '"+actualONE+"'";
							result=new Wrapper().requestDbContent(3, query);

						}
						else if (testObject.equals("Matrix"))
						{
							query=DAWISQueries.getMatrixOfSite+"'"+actualONE+"' and t.matrix_id = '"+actualTWO+"'";
							result=new Wrapper().requestDbContent(3, query);
						}
					}
					else if (object.equals("Matrix"))
					{
						if (testObject.equals("Site"))
						{
							query=DAWISQueries.getMatrixOfSite+"'"+actualTWO+"' and t.matrix_id = '"+actualONE+"'";
							result=new Wrapper().requestDbContent(3, query);
						}

					}
				}
			}

			if (result!=null)
			{
				if (result.size()>0)
				{
					String[] entry={one.getLabel(), two.getLabel(), "False"};
					edges.add(entry);
					
					if (one.getBiologicalElement().equals("Gene")&&two.getBiologicalElement().equals("Protein"))
					{
						Gene gene=(Gene)one;
						String[] proteinID={two.getLabel()};
						gene.addProtein(proteinID);
					}
					
					if (two.getBiologicalElement().equals("Gene")&&one.getBiologicalElement().equals("Protein"))
					{
						Gene gene=(Gene)two;
						String[] proteinID={one.getLabel()};
						gene.addProtein(proteinID);
					}
				}
			}
		}

	}

	private String getPathwayNumber(String actualONE)
	{
		String number="";
		String[] det={actualONE};

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, DAWISQueries.getPathwayNumber, det);
		
		for (DBColumn column : results)
		{
			String[] numbers=column.getColumn();
			
			number=numbers[0];
		}
		return number;
	}

	/**
	 * 
	 * @param id
	 * @param object
	 * @param data
	 *            bank
	 */
	private void createVertexFor(String[] elementData)
	{

		String id=elementData[0].trim();
		String object=elementData[1];
		String db=elementData[2];

		Hashtable<String, String> ht=data.getIdIdRelationsAsHashtable();

		if (object.equals("Molecule"))
		{
			object="Compound";
		}

		if (object.equals("Pathway"))
		{

			if (!ht.containsKey(id))
			{
				ht.put(id, id);

				PathwayMap p=new PathwayMap(id, "", null);
				p.setAbstract(false);
				p.setReference(false);
				p.setDB(db);
				allElements.put(id, p);

				if (db.equalsIgnoreCase("KEGG"))
				{
					String org=getPathwayOrganism(id);
					p.setOrganism(org);
				}
				createDAWISNode(p);
				p.lookUpAtAllDatabases();

				addAllIDsToElementsHashtable(p);

			}

		}
		else if (object.equals("Disease"))
		{
			if (!ht.containsKey(id))
			{
				ht.put(id, id);
				Disease d=new Disease(id, "", null);
				d.setAbstract(false);
				d.setReference(false);
				d.setDB(db);

				allElements.put(id, d);

				createDAWISNode(d);

				addAllIDsToElementsHashtable(d);

			}

		}
		else if (object.equals("Gene"))
		{
			if (!ht.containsKey(id))
			{
				ht.put(id, id);
				Gene g=new Gene(id, "", null);
				g.setAbstract(false);
				g.setReference(false);
				g.setDB(db);
				allElements.put(id, g);
				createDAWISNode(g);
				g.lookUpAtAllDatabases();

				addAllIDsToElementsHashtable(g);

			}
		}
		else if (object.equals("Protein"))
		{

			if (!ht.containsKey(id))
			{
				ht.put(id, id);

				Protein p=new Protein(id, "", null);
				p.setAbstract(false);
				p.setReference(false);
				p.setDB(db);
				allElements.put(id, p);
				createDAWISNode(p);
				p.lookUpAtAllDatabases();

				addAllIDsToElementsHashtable(p);
			}

		}
		else if (object.equals("Enzyme"))
		{

			if (!ht.containsKey(id))
			{
				ht.put(id, id);
				Enzyme e=new Enzyme(id, "", null);
				e.setAbstract(false);
				e.setReference(false);
				e.setDB(db);
				allElements.put(id, e);
				createDAWISNode(e);
				e.lookUpAtAllDatabases();

				addAllIDsToElementsHashtable(e);
			}

		}
		else if (object.equals("Compound"))
		{

			if (!ht.containsKey(id))
			{
				ht.put(id, id);

				CompoundNode c=new CompoundNode(id, "", null);
				c.setAbstract(false);
				c.setReference(false);
				c.setDB(db);
				allElements.put(id, c);
				createDAWISNode(c);

				addAllIDsToElementsHashtable(c);

			}

		}
		else if (object.equals("Glycan"))
		{

			if (!ht.containsKey(id))
			{
				ht.put(id, id);

				Glycan gl=new Glycan(id, "", null);
				gl.setAbstract(false);
				gl.setReference(false);
				gl.setDB(db);
				allElements.put(id, gl);
				createDAWISNode(gl);

				addAllIDsToElementsHashtable(gl);
			}

		}
		else if (object.equals("Drug"))
		{

			if (!ht.containsKey(id))
			{
				ht.put(id, id);

				Drug dr=new Drug(id, "", null);
				dr.setAbstract(false);
				dr.setReference(false);
				dr.setDB(db);
				allElements.put(id, dr);
				createDAWISNode(dr);
				addAllIDsToElementsHashtable(dr);

			}

		}
		else if (object.equals("Gene Ontology"))
		{

			if (!ht.containsKey(id))
			{
				ht.put(id, id);

				GeneOntology go=new GeneOntology(id, "", null);
				go.setAbstract(false);
				go.setReference(false);
				go.setDB(db);
				allElements.put(id, go);
				createDAWISNode(go);
				addAllIDsToElementsHashtable(go);

			}
		}
		else if (object.equals("Factor"))
		{

			if (!ht.containsKey(id))
			{
				ht.put(id, id);

				Factor d=new Factor(id, "", null);
				d.setAbstract(false);
				d.setReference(false);
				d.setDB(db);
				allElements.put(id, d);
				createDAWISNode(d);

				addAllIDsToElementsHashtable(d);

			}

		}
		else if (object.equals("Site"))
		{

			if (!ht.containsKey(id))
			{
				ht.put(id, id);

				Site d=new Site(id, "", null);
				d.setAbstract(false);
				d.setReference(false);
				d.setDB(db);
				allElements.put(id, d);
				createDAWISNode(d);

				addAllIDsToElementsHashtable(d);

			}

		}
		else if (object.equals("Matrix"))
		{

			if (!ht.containsKey(id))
			{
				ht.put(id, id);

				Matrix d=new Matrix(id, "", null);
				d.setAbstract(false);
				d.setReference(false);
				d.setDB(db);
				allElements.put(id, d);
				createDAWISNode(d);

				addAllIDsToElementsHashtable(d);

			}

		}
		else if (object.equals("Fragment"))
		{

			if (!ht.containsKey(id))
			{
				ht.put(id, id);
				Fragment d=new Fragment(id, "", null);
				d.setAbstract(false);
				d.setReference(false);
				d.setDB(db);
				allElements.put(id, d);

				createDAWISNode(d);

				addAllIDsToElementsHashtable(d);

			}
		}
	}

	private String getPathwayOrganism(String id)
	{
		String org="";
		String[] det={id};

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, DAWISQueries.getPathwayOrganism, det);
		
		for (DBColumn column : results)
		{
			String[] orgs=column.getColumn();
			org=orgs[0];
		}
		return org;
	}

	/**
	 * create DAWISNode
	 * 
	 * @param bna
	 */
	private void createDAWISNode(BiologicalNodeAbstract bna)
	{

		String id=bna.getLabel();
		String db=bna.getDB();
		dawisNode=new DAWISNode(bna.getBiologicalElement());
		dawisNode.setID(id);
		dawisNode.setDB(db);
		dawisNode.addID(id, id);
		dawisNode.addIDDBRelation(db, id);
		bna.setDAWISNode(dawisNode);
	}

	private void setSettings()
	{

		Iterator<String> it=data.getObjects().iterator();
		while (it.hasNext())
		{
			String object=(String)it.next();
			if (object.equals("Pathway"))
			{
				settings[0]=true;
			}
			else if (object.equals("Disease"))
			{
				settings[1]=true;
			}
			else if (object.equals("Gene Ontology"))
			{
				settings[2]=true;
			}
			else if (object.equals("Gene"))
			{
				settings[3]=true;
			}
			else if (object.equals("Protein"))
			{
				settings[4]=true;
			}
			else if (object.equals("Enzyme"))
			{
				settings[5]=true;
			}
			else if (object.equals("Reaction Pair"))
			{
				settings[6]=true;
			}
			else if (object.equals("Compound"))
			{
				settings[7]=true;
			}
			else if (object.equals("Glycan"))
			{
				settings[8]=true;
			}
			else if (object.equals("Drug"))
			{
				settings[9]=true;
			}
			else if (object.equals("Reaction"))
			{
				settings[10]=true;
			}
		}

	}

	/**
	 * get objects as Vector <String>
	 * 
	 * @param Vector
	 *            <String[]> elements
	 * @return
	 */
	private void getObjects()
	{
		for (DBColumn column : newLoadedElements)
		{
			String[] resultDetails=column.getColumn();
			Vector<String> objects=data.getObjects();

			if (!objects.contains(resultDetails[1]))
			{
				if (resultDetails[1].equals("Realated Pair"))
				{
					resultDetails[1]="Reaction Pair";
				}
				if (!resultDetails[1].equals("Reaction")&!resultDetails[1].equals("Reaction Pair"))
				{
					if (resultDetails[1].equals("Molecule"))
					{
						resultDetails[1]="Compound";
					}

					if (resultDetails[1].equals("Factor"))
					{
						if (!data.getFragmentRelatedDomains().contains("Factor"))
						{
							data.addElementToFragmentVector("Factor");
						}
						if (!data.getGeneRelatedDomains().contains("Factor"))
						{
							data.addElementToGeneVector("Factor");
						}
						if (!data.getMatrixRelatedDomains().contains("Factor"))
						{
							data.addElementToMatrixVector("Factor");
						}
						if (!data.getSiteRelatedDomains().contains("Factor"))
						{
							data.addElementToSiteVector("Factor");
						}
					}
					if (resultDetails[1].equals("Fragment"))
					{
						if (!data.getFactorRelatedDomains().contains("Fragment"))
						{
							data.addElementToFactorVector("Fragment");
						}
						if (!data.getGeneRelatedDomains().contains("Fragment"))
						{
							data.addElementToGeneVector("Fragment");
						}
					}
					if (resultDetails[1].equals("Site"))
					{
						if (!data.getGeneRelatedDomains().contains("Site"))
						{
							data.addElementToGeneVector("Site");
						}
						if (!data.getMatrixRelatedDomains().contains("Site"))
						{
							data.addElementToMatrixVector("Site");
						}
						if (!data.getFactorRelatedDomains().contains("Site"))
						{
							data.addElementToFactorVector("Site");
						}

					}

					if (resultDetails[1].equals("Matrix"))
					{
						if (!data.getSiteRelatedDomains().contains("Matrix"))
						{
							data.addElementToSiteVector("Matrix");
						}
						if (!data.getFactorRelatedDomains().contains("Matrix"))
						{
							data.addElementToFactorVector("Matrix");
						}

					}

					data.addObject(resultDetails[1]);

				}
			}

			Hashtable<String, String> ht=data.getIdIdRelationsAsHashtable();

			if (resultDetails[1].equals("Reaction"))
			{
				if (!ht.containsKey(resultDetails[0]))
				{
					ht.put(resultDetails[0], resultDetails[0]);

					newReactions.add(resultDetails[0]);
					newReaction=true;
				}
			}
			else if (resultDetails[1].equals("Reaction Pair"))
			{

				if (!ht.containsKey(resultDetails[0]))
				{
					ht.put(resultDetails[0], resultDetails[0]);

					newReactionPairs.add(resultDetails[0]);
					newReactionPair=true;
				}
			}
			else
			{
				createVertexFor(resultDetails);
			}

		}
	}

	/**
	 * create empty fields for pathway headings
	 */
	public void getPathwayHeadings()
	{

		pathwayLink="";
		pathwayImage="";
		pathwayNumber="";

	}

	/**
	 * get the graph
	 * 
	 * @return MyGraph
	 */
	public MyGraph getGraph()
	{
		return myGraph;
	}

	/**
	 * create elements vector for elements to be shown
	 */
	private void createElementsVector()
	{

		showPathways=settings[0];
		showDiseases=settings[1];
		showGOs=settings[2];
		showGenes=settings[3];
		showProteins=settings[4];
		showEnzymes=settings[5];
		showCompounds=settings[6];
		showGlycans=settings[7];
		showDrugs=settings[8];

		if (showPathways)
		{
			if (!data.getPathwayRelatedDomains().contains("Pathway"))
			{
				data.addElementToPathwayVector("Pathway");
			}
			if (!data.getGeneRelatedDomains().contains("Pathway"))
			{
				data.addElementToGeneVector("Pathway");
			}
			if (!data.getEnzymeRelatedDomains().contains("Pathway"))
			{
				data.addElementToEnzymeVector("Pathway");
			}
			if (!data.getCompoundRelatedDomains().contains("Pathway"))
			{
				data.addElementToCompoundVector("Pathway");
			}
			if (!data.getGlycanRelatedDomains().contains("Pathway"))
			{
				data.addElementToGlycanVector("Pathway");
			}
			if (!data.getDrugRelatedDomains().contains("Pathway"))
			{
				data.addElementToDrugVector("Pathway");
			}
		}

		if (showProteins)
		{
			if (!data.getDiseaseRelatedDomains().contains("Protein"))
			{
				data.addElementToDiseaseVector("Protein");
			}
			if (!data.getGeneRelatedDomains().contains("Protein"))
			{
				data.addElementToGeneVector("Protein");
			}
			if (!data.getGeneOntologyRelatedDomains().contains("Protein"))
			{
				data.addElementToGeneOntologyVector("Protein");
			}
			if (!data.getEnzymeRelatedDomains().contains("Protein"))
			{
				data.addElementToEnzymeVector("Protein");
			}
			if (!data.getFactorRelatedDomains().contains("Protein"))
			{
				data.addElementToFactorVector("Protein");
			}
			if (!data.getCompoundRelatedDomains().contains("Protein"))
			{
				data.addElementToCompoundVector("Protein");
			}
		}

		if (showCompounds)
		{
			if (!data.getPathwayRelatedDomains().contains("Compound"))
			{
				data.addElementToPathwayVector("Compound");
			}
			if (!data.getEnzymeRelatedDomains().contains("Compound"))
			{
				data.addElementToEnzymeVector("Compound");
			}
			if (!data.getProteinRelatedDomains().contains("Compound"))
			{
				data.addElementToProteinVector("Compound");
			}
			if (!data.getReactionPairRelatedDomains().contains("Compound"))
			{
				data.addElementToReactionPairVector("Compound");
			}
			if (!data.getGeneRelatedDomains().contains("Compound"))
			{
				data.addElementToGeneVector("Compound");
			}
			if (!data.getGeneOntologyRelatedDomains().contains("Compound"))
			{
				data.addElementToGeneOntologyVector("Compound");
			}
			if (!data.getDiseaseRelatedDomains().contains("Compound"))
			{
				data.addElementToDiseaseVector("Compound");
			}
			if (!data.getFactorRelatedDomains().contains("Compound"))
			{
				data.addElementToFactorVector("Compound");
			}
		}

		if (showEnzymes)
		{
			if (!data.getPathwayRelatedDomains().contains("Enzyme"))
			{
				data.addElementToPathwayVector("Enzyme");
			}
			if (!data.getGeneRelatedDomains().contains("Enzyme"))
			{
				data.addElementToGeneVector("Enzyme");
			}
			if (!data.getGeneOntologyRelatedDomains().contains("Enzyme"))
			{
				data.addElementToGeneOntologyVector("Enzyme");
			}
			if (!data.getCompoundRelatedDomains().contains("Enzyme"))
			{
				data.addElementToCompoundVector("Enzyme");
			}
			if (!data.getGlycanRelatedDomains().contains("Enzyme"))
			{
				data.addElementToGlycanVector("Enzyme");
			}
			if (!data.getProteinRelatedDomains().contains("Enzyme"))
			{
				data.addElementToProteinVector("Enzyme");
			}
		}

		if (showDiseases)
		{
			if (!data.getGeneRelatedDomains().contains("Disease"))
			{
				data.addElementToGeneVector("Disease");
			}
			if (!data.getProteinRelatedDomains().contains("Disease"))
			{
				data.addElementToProteinVector("Disease");
			}
			if (!data.getCompoundRelatedDomains().contains("Disease"))
			{
				data.addElementToCompoundVector("Disease");
			}
		}

		if (showDrugs)
		{
			if (!data.getPathwayRelatedDomains().contains("Drug"))
			{
				data.addElementToPathwayVector("Drug");
			}
		}

		if (showGenes)
		{
			if (!data.getPathwayRelatedDomains().contains("Gene"))
			{
				data.addElementToPathwayVector("Gene");
			}
			if (!data.getDiseaseRelatedDomains().contains("Gene"))
			{
				data.addElementToDiseaseVector("Gene");
			}
			if (!data.getProteinRelatedDomains().contains("Gene"))
			{
				data.addElementToProteinVector("Gene");
			}
			if (!data.getEnzymeRelatedDomains().contains("Gene"))
			{
				data.addElementToEnzymeVector("Gene");
			}
			if (!data.getGeneOntologyRelatedDomains().contains("Gene"))
			{
				data.addElementToGeneOntologyVector("Gene");
			}
			if (!data.getFactorRelatedDomains().contains("Gene"))
			{
				data.addElementToFactorVector("Gene");
			}
			if (!data.getFragmentRelatedDomains().contains("Gene"))
			{
				data.addElementToFragmentVector("Gene");
			}
			if (!data.getSiteRelatedDomains().contains("Gene"))
			{
				data.addElementToSiteVector("Gene");
			}
			if (!data.getCompoundRelatedDomains().contains("Gene"))
			{
				data.addElementToCompoundVector("Gene");
			}

		}

		if (showGlycans)
		{
			if (!data.getPathwayRelatedDomains().contains("Glycan"))
			{
				data.addElementToPathwayVector("Glycan");
			}
			if (!data.getEnzymeRelatedDomains().contains("Glycan"))
			{
				data.addElementToEnzymeVector("Glycan");
			}
		}

		if (showGOs)
		{
			if (!data.getProteinRelatedDomains().contains("Gene Ontology"))
			{
				data.addElementToProteinVector("Gene Ontology");
			}
			if (!data.getEnzymeRelatedDomains().contains("Gene Ontology"))
			{
				data.addElementToEnzymeVector("Gene Ontology");
			}
			if (!data.getCompoundRelatedDomains().contains("Gene Ontology"))
			{
				data.addElementToCompoundVector("Gene Ontology");
			}
			if (!data.getGeneRelatedDomains().contains("Gene Ontology"))
			{
				data.addElementToGeneVector("Gene Ontology");
			}
		}
	}

	private void addAllIDsToElementsHashtable(BiologicalNodeAbstract newBNA)
	{

		Hashtable<String, String> ht=newBNA.getDAWISNode().getAllIDsAsHashtable();
		Set<String> s=ht.keySet();
		Iterator<String> it=s.iterator();
		while (it.hasNext())
		{
			String actualID=(String)it.next();
			data.setIdIdRelation(actualID, newBNA.getLabel());
		}

	}

	public void drawNodes()
	{

		Iterator<String> i=allElements.keySet().iterator();
		while (i.hasNext())
		{

			String key=i.next();

			Object temp_node=(BiologicalNodeAbstract)allElements.get(key);
			BiologicalNodeAbstract node=(BiologicalNodeAbstract)temp_node;

			node.setVertex(myGraph.createNewVertex());

			pw.addElement(temp_node);

			setPosition();
			myGraph.moveVertex(node.getVertex(), column*150, row*100);
		}
	}

	private void setPosition()
	{

		column++;
		if (column==10)
		{
			row++;
			column=0;
		}
	}

	public void setAdjazenzList(InternalGraphRepresentation igr)
	{
		adjazenzList=igr;
	}

	public void drawEdges(EdgeController edgeController)
	{

		Iterator it=edges.iterator();

		while (it.hasNext())
		{

			String[] entry=(String[])it.next();

			BiologicalNodeAbstract bna1=null;
			BiologicalNodeAbstract bna2=null;

			if (allElements.contains(entry[0]))
			{
				bna1=(BiologicalNodeAbstract)allElements.get(entry[0]);
			}
			else
			{
				bna1=(BiologicalNodeAbstract)pw.getNodeByLabel(entry[0]);
			}
			if (allElements.contains(entry[1]))
			{
				bna2=(BiologicalNodeAbstract)allElements.get(entry[1]);
			}
			else
			{
				bna2=(BiologicalNodeAbstract)pw.getNodeByLabel(entry[1]);
			}

			Vertex first=bna1.getVertex();
			Vertex second=bna2.getVertex();

			String key1=bna1.getLabel()+bna2.getLabel();
			String key2=bna2.getLabel()+bna1.getLabel();

			ReactionPairEdge rpEdge=null;
			if (rPairEdges.containsKey(key1))
			{
				rpEdge=rPairEdges.get(key1);
			}
			else if (rPairEdges.containsKey(key2))
			{
				rpEdge=rPairEdges.get(key2);
			}

			KEGGEdge keggEdge=null;
			if (keggEdges.containsKey(key1))
			{
				keggEdge=keggEdges.get(key1);
			}
			else if (keggEdges.containsKey(key2))
			{
				keggEdge=keggEdges.get(key2);
			}

			if (!adjazenzList.doesEdgeExist(first, second))
			{
				if (entry[2].equals("True"))
				{
					if (keggEdge!=null)
					{
						edgeController.createReactionEdge(first, second, keggEdge);
					}
					else if (rpEdge!=null)
					{
						edgeController.createRPairEdge(first, second, rpEdge);
					}

				}
				else
				{
					edgeController.buildSimpleEdge(bna1.getVertex(), bna2.getVertex());
				}
			}
		}
	}

}
