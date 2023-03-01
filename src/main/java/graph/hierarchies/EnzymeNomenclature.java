package graph.hierarchies;

public class EnzymeNomenclature extends HierarchyStructure<String>{

	private static final long serialVersionUID = 6320259890705232607L;
	public static String OXIDOREDUCTASES = "Oxidoreductases";
	public static String TRANSFERASES = "Transferases";
	public static String HYDROLASES = "Hydrolases";
	public static String LYASES = "Lyases";
	public static String ISOMERASES = "Isomerases";
	public static String LIGASES = "Ligases";

	public static String CHOH_DONOR = "CH-OH group of donors";
	public static String ALD_DONOR = "Aldehyde or oxo group of donors";
	public static String CHCH_DONOR = "CH-CH group of donors";
	public static String CHNH2_DONOR = "CH-NH2 group of donors";
	public static String CHNH_DONOR = "CH-NH group of donors";
	public static String NADH_NADPH = "NADH or NADPH";
	public static String OTHER_NITRO_DONORS = "Other nitrogenous compunds as donors";
	public static String SULFUR_DONOR = "Sulfur group of donors";
	public static String HEME_DONOR = "Heme group of donors";
	public static String DIPHENOL_DONOR = "Diphenol and related substances as donors";
	public static String PEROXIDE_ACCEPTOR = "Peroxide as an acceptor";
	public static String HYDROGEN_DONOR = "Hydrogen as donors";
	public static String SINGLE_DONOR = "Single donors with incorporation of molecular oxygen";
	public static String PAIRED_DONOR = "Paired donors with incorporation of molecular oxygen";
	public static String SUPEROXIDE_ACCEPTOR = "Superoxide radicals as acceptor";
	public static String OXIDIZE_METAL = "Oxidize metal ions";
	public static String CH_CH2 = "CH or CH2 groups";
	public static String IRONSULFUR_DONOR = "Iron-sulfur proteins as donors";
	public static String REDUCEDFLAVODOXIN_DONOR = "Reduced Flavodoxin as donor";
	public static String PHOSPHORUSARSENIC_DONOR = "Phosphorus or arsenic in donors";
	public static String XH_YH = "X-H and Y-H to form an X-Y bound";
	public static String OTHER_OXIDOREDUCTASES = "other oxidoreducatses";

	public static String SINGLE_CARBON = "single-carbon groups transferred";
	public static String ALDEHYDE_KETONE = "aldehyde or ketone groups transferred";
	public static String ACYL_ALKYL = "Acyl groups that become alkyl groups during transfer";
	public static String GLYCOSYL = "glycosyl groups as well as hexoses and pentoses transferred";
	public static String ALKYL_ARYL = "Alkyl or Aryl groups, other than methyl groups transferred";
	public static String NITROGENOUS = "Nitrogenous groups transferred";
	public static String PHOSPHORUS = "phosphorus-containing groups transferred";
	public static String SULFUR = "Sulfur-containing groups transferred";
	public static String SELENIUM = "Selenium-containing groups transferred";
	public static String MOLYBDENUM_TUNGSTEN = "Molybdenum or tungsten transferred";

	public static String ESTER = "Ester bonds";
	public static String SUGARS = "Sugars";
	public static String ETHER = "Ether bonds";
	public static String PEPTIDE = "Peptide bonds";
	public static String CARBON_NITROGEN = "Carbon-nitrogen bonds";
	public static String ACID_ANHYDRIDES = "Acid anhydride hydrolases, including helicases and GTPase";
	public static String CARBON_CARBON = "Carbon-carbon bonds";
	public static String HALIDE = "Halide bonds";
	public static String PHOSPHORUS_NITROGEN = "Phosphorus-nitrogen bonds";
	public static String SULPHUR_NITROGEN = "Sulphur-nitrogen bonds";
	public static String CARBON_PHOSPHORUS = "Carbon-Phosphorus bonds";
	public static String SULFUR_SULFUR = "Sulfur-sulfur bonds";
	public static String CARBON_SULFUR = "Carbon-sulfur bonds";
	
	public static String CARBON_CARBON_LYASE = "Carbon-carbon bonds cleaved";
	public static String CARBON_OXYGEN_LYASE = "Carbon-oxygen bonds cleaved";
	public static String CARBON_NITROGEN_LYASE = "Carbon-nitrogen bonds cleaved";
	public static String CARBON_SULFUR_LYASE = "Carbon-sulfur bonds cleaved";
	public static String CARBON_HALIDE_LYASE = "Carbon-halide bonds cleaved";
	public static String PHOSPHORUS_OXYGEN_LYASE = "Phosphorus-oxygen bonds cleaved, such as adenylate cyclase and guanylate cyclase";
	public static String OTHER_LYASE = "Other lyases";
	
	public static String RACEMASES_EPIMERASES = "Racemases and Epimerases";
	public static String CIS_TRANS = "Cis-trans isomerization catalyzed";
	public static String INTRAMOLECULAR_OXIDOREDUCTASES = "Intramolecular oxidoreductases";
	public static String INTRAMOLECULAR_TRANSFERASES = "Intramolecular transferases";
	public static String INTRAMOLECULAR_LYASES = "Intramolecular lyases";
	public static String OTHER_ISOMERASES = "Other isomerases";
	
	public static String CARBON_OXYGEN_LIGASE = "Carbon-oxygen bonds formed";
	public static String CARBON_SULFUR_LIGASE = "Carbon-sulfur bonds formed";
	public static String CARBON_NITROGEN_LIGASE = "Carbon-nitrogen bonds formed";
	public static String CARBON_CARBON_LIGASE = "Carbon-carbon bonds formed";
	public static String PHOSPHORICESTER_LIGASE = "Phosphoric ester bonds formed";
	public static String NITROGEN_METAL_LIGASE = "Nitrogen-metal bonds formed";
	
	public EnzymeNomenclature(){
		super();
		put(CHOH_DONOR,OXIDOREDUCTASES);
		put(ALD_DONOR,OXIDOREDUCTASES);
		put(CHCH_DONOR,OXIDOREDUCTASES);
		put(CHNH2_DONOR,OXIDOREDUCTASES);
		put(CHNH_DONOR,OXIDOREDUCTASES);
		put(NADH_NADPH,OXIDOREDUCTASES);
		put(OTHER_NITRO_DONORS,OXIDOREDUCTASES);
		put(SULFUR_DONOR,OXIDOREDUCTASES);
		put(HEME_DONOR,OXIDOREDUCTASES);
		put(DIPHENOL_DONOR,OXIDOREDUCTASES);
		put(PEROXIDE_ACCEPTOR,OXIDOREDUCTASES);
		put(HYDROGEN_DONOR,OXIDOREDUCTASES);
		put(SINGLE_DONOR,OXIDOREDUCTASES);
		put(PAIRED_DONOR,OXIDOREDUCTASES);
		put(SUPEROXIDE_ACCEPTOR,OXIDOREDUCTASES);
		put(OXIDIZE_METAL,OXIDOREDUCTASES);
		put(CH_CH2,OXIDOREDUCTASES);
		put(IRONSULFUR_DONOR,OXIDOREDUCTASES);
		put(REDUCEDFLAVODOXIN_DONOR,OXIDOREDUCTASES);
		put(PHOSPHORUSARSENIC_DONOR,OXIDOREDUCTASES);
		put(XH_YH,OXIDOREDUCTASES);
		put(OTHER_OXIDOREDUCTASES,OXIDOREDUCTASES);
		
		put(SINGLE_CARBON,TRANSFERASES);
		put(ALDEHYDE_KETONE,TRANSFERASES);
		put(ACYL_ALKYL,TRANSFERASES);
		put(GLYCOSYL,TRANSFERASES);
		put(ALKYL_ARYL,TRANSFERASES);
		put(NITROGENOUS,TRANSFERASES);
		put(PHOSPHORUS,TRANSFERASES);
		put(SULFUR,TRANSFERASES);
		put(SELENIUM,TRANSFERASES);
		put(MOLYBDENUM_TUNGSTEN,TRANSFERASES);
		
		put(ESTER,HYDROLASES);
		put(SUGARS,HYDROLASES);
		put(ETHER,HYDROLASES);
		put(PEPTIDE,HYDROLASES);
		put(CARBON_NITROGEN,HYDROLASES);
		put(ACID_ANHYDRIDES,HYDROLASES);
		put(CARBON_CARBON,HYDROLASES);
		put(HALIDE,HYDROLASES);
		put(PHOSPHORUS_NITROGEN,HYDROLASES);
		put(SULPHUR_NITROGEN,HYDROLASES);
		put(CARBON_PHOSPHORUS,HYDROLASES);
		put(SULFUR_SULFUR,HYDROLASES);
		put(CARBON_SULFUR,HYDROLASES);
		
		put(CARBON_CARBON_LYASE,LYASES);
		put(CARBON_OXYGEN_LYASE,LYASES);
		put(CARBON_NITROGEN_LYASE,LYASES);
		put(CARBON_SULFUR_LYASE,LYASES);
		put(CARBON_HALIDE_LYASE,LYASES);
		put(PHOSPHORUS_OXYGEN_LYASE,LYASES);
		put(OTHER_LYASE,LYASES);
		
		put(RACEMASES_EPIMERASES,ISOMERASES);
		put(CIS_TRANS,ISOMERASES);
		put(INTRAMOLECULAR_OXIDOREDUCTASES,ISOMERASES);
		put(INTRAMOLECULAR_LYASES,ISOMERASES);
		put(INTRAMOLECULAR_TRANSFERASES,ISOMERASES);
		put(OTHER_ISOMERASES,ISOMERASES);

		put(CARBON_OXYGEN_LIGASE,LIGASES);
		put(CARBON_SULFUR_LIGASE,LIGASES);
		put(CARBON_NITROGEN_LIGASE,LIGASES);
		put(CARBON_CARBON_LIGASE,LIGASES);
		put(PHOSPHORICESTER_LIGASE,LIGASES);
		put(NITROGEN_METAL_LIGASE,LIGASES);

	}
	
	public String ECtoClass(String ec){
		String[] ecSplit = ec.split("\\.");
		if(ecSplit.length<2){
			return null;
		}
		if(ecSplit[0].equals("1")){
			switch(ecSplit[1]){
			case "1":
				return CHOH_DONOR;
			case "2":
				return ALD_DONOR;
			case "3":
				return CHCH_DONOR;
			case "4":
				return CHNH2_DONOR;
			case "5":
				return CHNH_DONOR;
			case "6":
				return NADH_NADPH;
			case "7":
				return OTHER_NITRO_DONORS;
			case "8":
				return SULFUR_DONOR;
			case "9":
				return HEME_DONOR;
			case "10":
				return DIPHENOL_DONOR;
			case "11":
				return PEROXIDE_ACCEPTOR;
			case "12":
				return HYDROGEN_DONOR;
			case "13":
				return SINGLE_DONOR;
			case "14":
				return PAIRED_DONOR;
			case "15":
				return SUPEROXIDE_ACCEPTOR;
			case "16":
				return OXIDIZE_METAL;
			case "17":
				return CH_CH2;
			case "18":
				return IRONSULFUR_DONOR;
			case "19":
				return REDUCEDFLAVODOXIN_DONOR;
			case "20":
				return PHOSPHORUSARSENIC_DONOR;
			case "21":
				return XH_YH;
			case "97":
				return OTHER_OXIDOREDUCTASES;
			default:
				return OXIDOREDUCTASES;
			}
			
		} else if(ecSplit[0].equals("2")){
			switch(ecSplit[1]){
			case "1":
				return SINGLE_CARBON;
			case "2":
				return ALDEHYDE_KETONE;
			case "3":
				return ACYL_ALKYL;
			case "4":
				return GLYCOSYL;
			case "5":
				return ALKYL_ARYL;
			case "6":
				return NITROGENOUS;
			case "7":
				return PHOSPHORUS;
			case "8":
				return SULFUR;
			case "9":
				return SELENIUM;
			case "10":
				return MOLYBDENUM_TUNGSTEN;
			default:
				return TRANSFERASES;
			}
			
		} else if(ecSplit[0].equals("3")){
			switch(ecSplit[1]){
			case "1":
				return ESTER;
			case "2":
				return SUGARS;
			case "3":
				return ETHER;
			case "4":
				return PEPTIDE;
			case "5":
				return CARBON_NITROGEN;
			case "6":
				return ACID_ANHYDRIDES;
			case "7":
				return CARBON_CARBON;
			case "8":
				return HALIDE;
			case "9":
				return PHOSPHORUS_NITROGEN;
			case "10":
				return SULPHUR_NITROGEN;
			case "11":
				return CARBON_PHOSPHORUS;
			case "12":
				return SULFUR_SULFUR;
			case "13":
				return CARBON_SULFUR;
			default:
				return HYDROLASES;
			}
			
		} else if(ecSplit[0].equals("4")){
			switch(ecSplit[1]){
				case "1":
					return CARBON_CARBON_LYASE;
				case "2":
					return CARBON_OXYGEN_LYASE;
				case "3":
					return CARBON_NITROGEN_LYASE;
				case "4":
					return CARBON_SULFUR_LYASE;
				case "5":
					return CARBON_HALIDE_LYASE;
				case "6":
					return PHOSPHORUS_OXYGEN_LYASE;
				case "99":
					return OTHER_LYASE;
				default:
					return LYASES;
			}
			
		} else if(ecSplit[0].equals("5")){
			switch(ecSplit[1]){
			case "1":
				return RACEMASES_EPIMERASES;
			case "2":
				return CIS_TRANS;
			case "3":
				return INTRAMOLECULAR_OXIDOREDUCTASES;
			case "4":
				return INTRAMOLECULAR_TRANSFERASES;
			case "5":
				return INTRAMOLECULAR_LYASES;
			case "99":
				return OTHER_ISOMERASES;
			default:
				return ISOMERASES;
			}

		} else if(ecSplit[0].equals("6")){
			switch(ecSplit[1]){
			case "1":
				return CARBON_OXYGEN_LIGASE;
			case "2":
				return CARBON_SULFUR_LIGASE;
			case "3":
				return CARBON_NITROGEN_LIGASE;
			case "4":
				return CARBON_CARBON_LIGASE;
			case "5":
				return PHOSPHORICESTER_LIGASE;
			case "6":
				return NITROGEN_METAL_LIGASE;
			default:
				return LIGASES;
			}
		}
		return null;
	}
}
