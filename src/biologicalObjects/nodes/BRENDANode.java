package biologicalObjects.nodes;

import java.util.Iterator;
import java.util.Vector;


public class BRENDANode {

	private String name,ec_number,sysName,enzymeClass,reaction,kmValue,kcat,substrate,product;
	
	@SuppressWarnings("unchecked")
	private Vector cofactor = new Vector();
	@SuppressWarnings("unchecked")
	private Vector inhibitor = new Vector();
	@SuppressWarnings("unchecked")
	private Vector comment = new Vector();
	
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

	@SuppressWarnings("unchecked")
	public String getCofactor() {
		
		String result="";
		Iterator it = cofactor.iterator();
		boolean first = true;
		while (it.hasNext()){
			if(first){
				result=result+it.next().toString();
				first=false;
			}else{
				result=result+" ; "+it.next().toString();
			}
		}	
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Vector getCofactorsAsVector(){
		return cofactor;
	}

	@SuppressWarnings("unchecked")
	public void setCofactor(String cofactor_str) {
		if(!cofactor.contains(cofactor_str)){
			cofactor.add(cofactor_str);
		}
	}

	@SuppressWarnings("unchecked")
	public String getComment() {
		String result="";
		Iterator it = comment.iterator();
		boolean first = true;
		while (it.hasNext()){
			if(first){
				result=result+it.next().toString();
				first=false;
			}else{
				result=result+" ; "+it.next().toString();
			}
		}	
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector getCommentsAsVector(){
		return comment;
	}
	
	
	@SuppressWarnings("unchecked")
	public void setComment(String comment_str) {
		if(!comment.contains(comment_str)){
			comment.add(comment_str);
		}

	}

	@SuppressWarnings("unchecked")
	public String getInhibitor() {
		String result="";
		Iterator it = inhibitor.iterator();
		boolean first = true;
		while (it.hasNext()){
			if(first){
				result=result+it.next().toString();
				first=false;
			}else{
				result=result+" ; "+it.next().toString();
			}
		}	
		return result;
	}

	@SuppressWarnings("unchecked")
	public Vector getInhibitorAsVector(){
		return inhibitor;
	}
	
	@SuppressWarnings("unchecked")
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
