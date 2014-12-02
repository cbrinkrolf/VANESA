package biologicalObjects.nodes;

import java.util.Iterator;
import java.util.Vector;


public class BRENDANode {

	private String name,ec_number,sysName,enzymeClass,reaction,kmValue,kcat,substrate,product;
	
	private Vector<String> cofactor = new Vector<String>();
	private Vector<String> inhibitor = new Vector<String>();
	private Vector<String> comment = new Vector<String>();
	
	public String getLink() {
		return "http://www.brenda-enzymes.info/php/result_flat.php4?ecno="+ec_number;
	}

	public String getEc_number() {
		return ec_number;
	}

	public void setEc_number(String ec_number) {
		this.ec_number = ec_number;
	}

	public BRENDANode() {
		name = "";
		ec_number = "";
		sysName="";
		enzymeClass="";
		reaction="";
		kmValue="";
		kcat="";
		substrate="";
		product="";
	}

	public Object[][] getBrendaDeatails() {
		
		Object[][] values = {
				 {"Name",getName()},
				 {"SysName",getSysName()},
				 {"EC-Number",getEc_number()},
				 {"Reaction",getReaction()},
				 {"Km Value",getKmValue()},
				 {"kcat",getKcat()},
				 {"Substrate",getSubstrate()},
				 {"Product",getProduct()},
				 {"Cofactor",getCofactor()},
				 {"Inhibitor",getInhibitor()},
				 {"Comment",getComment()}
				
		};
		return values;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnzymeClass() {
		return enzymeClass;
	}

	public void setEnzymeClass(String enzymeClass) {
		this.enzymeClass = enzymeClass;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public String getCofactor() {
		
		String result="";
		Iterator<String> it = cofactor.iterator();
		boolean first = true;
		while (it.hasNext()){
			if(first){
				result=result+it.next();
				first=false;
			}else{
				result=result+" ; "+it.next();
			}
		}	
		return result;
	}
	
	public Vector<String> getCofactorsAsVector(){
		return cofactor;
	}

	public void setCofactor(String cofactor_str) {
		if(!cofactor.contains(cofactor_str)){
			cofactor.add(cofactor_str);
		}
	}

	public String getComment() {
		String result="";
		Iterator<String> it = comment.iterator();
		boolean first = true;
		while (it.hasNext()){
			if(first){
				result=result+it.next();
				first=false;
			}else{
				result=result+" ; "+it.next();
			}
		}	
		return result;
	}

	public Vector<String> getCommentsAsVector(){
		return comment;
	}
	
	
	public void setComment(String comment_str) {
		if(!comment.contains(comment_str)){
			comment.add(comment_str);
		}

	}

	public String getInhibitor() {
		String result="";
		Iterator<String> it = inhibitor.iterator();
		boolean first = true;
		while (it.hasNext()){
			if(first){
				result=result+it.next();
				first=false;
			}else{
				result=result+" ; "+it.next();
			}
		}	
		return result;
	}

	public Vector<String> getInhibitorAsVector(){
		return inhibitor;
	}
	
	public void setInhibitor(String inhibitor_str) {
		if(!inhibitor.contains(inhibitor_str)){
			inhibitor.add(inhibitor_str);
		}

	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getReaction() {
		return reaction;
	}

	public String getKmValue() {
		return kmValue;
	}

	public String getKcat() {
		return kcat;
	}

	public void setReaction(String reaction) {
		this.reaction = reaction;
	}

	public void setKmValue(String kmValue) {
		this.kmValue = (kmValue==null?"":kmValue);
	}

	public void setKcat(String kcat) {
		this.kcat = (kcat==null?"":kcat);
	}

	public String getSubstrate() {
		return substrate;
	}

	public void setSubstrate(String substrate) {
		this.substrate = substrate;
	}

	
}
