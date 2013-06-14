package database.mirna;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import pojos.DBColumn;

import configurations.Wrapper;


public class GetPublications {

	private final String[] microRNAs={"hsa-miR-106a-3p","hsa-miR-106a-5p","hsa-miR-106b-3p","hsa-miR-106b-5p","hsa-miR-10a-3p","hsa-miR-10a-5p","hsa-miR-1193","hsa-miR-1197","hsa-miR-1224-3p","hsa-miR-1224-5p","hsa-miR-1249","hsa-miR-1251","hsa-miR-125a-3p","hsa-miR-125a-5p","hsa-miR-126-3p","hsa-miR-126-5p","hsa-miR-127-3p","hsa-miR-127-5p","hsa-miR-129-1-3p","hsa-miR-129-2-3p","hsa-miR-1298","hsa-miR-1306-3p","hsa-miR-1306-5p","hsa-miR-130a-3p","hsa-miR-130a-5p","hsa-miR-132-3p","hsa-miR-132-5p","hsa-miR-133b","hsa-miR-134","hsa-miR-135b-3p","hsa-miR-135b-5p","hsa-miR-136-3p","hsa-miR-136-5p","hsa-miR-137","hsa-miR-138-1-3p","hsa-miR-138-2-3p","hsa-miR-140-3p","hsa-miR-140-5p","hsa-miR-143-3p","hsa-miR-143-5p","hsa-miR-146b-3p","hsa-miR-146b-5p","hsa-miR-149-3p","hsa-miR-149-5p","hsa-miR-152","hsa-miR-154-3p","hsa-miR-154-5p","hsa-miR-155-3p","hsa-miR-155-5p","hsa-miR-15a-3p","hsa-miR-15a-5p","hsa-miR-15b-3p","hsa-miR-15b-5p","hsa-miR-16-1-3p","hsa-miR-17-3p","hsa-miR-17-5p","hsa-miR-181d","hsa-miR-182-3p","hsa-miR-182-5p","hsa-miR-183-3p","hsa-miR-183-5p","hsa-miR-184","hsa-miR-185-3p","hsa-miR-185-5p","hsa-miR-186-3p","hsa-miR-186-5p","hsa-miR-188-3p","hsa-miR-188-5p","hsa-miR-190b","hsa-miR-192-3p","hsa-miR-192-5p","hsa-miR-193b-3p","hsa-miR-193b-5p","hsa-miR-195-3p","hsa-miR-195-5p","hsa-miR-196b-3p","hsa-miR-196b-5p","hsa-miR-199b-3p","hsa-miR-199b-5p","hsa-miR-200a-3p","hsa-miR-200a-5p","hsa-miR-200b-3p","hsa-miR-200b-5p","hsa-miR-202-3p","hsa-miR-202-5p","hsa-miR-203","hsa-miR-204-3p","hsa-miR-204-5p","hsa-miR-205-3p","hsa-miR-205-5p","hsa-miR-206","hsa-miR-208a","hsa-miR-208b","hsa-miR-20a-3p","hsa-miR-20a-5p","hsa-miR-20b-3p","hsa-miR-20b-5p","hsa-miR-21-3p","hsa-miR-21-5p","hsa-miR-210","hsa-miR-211-3p","hsa-miR-211-5p","hsa-miR-212-3p","hsa-miR-212-5p","hsa-miR-214-3p","hsa-miR-214-5p","hsa-miR-215","hsa-miR-216a","hsa-miR-216b","hsa-miR-217","hsa-miR-218-2-3p","hsa-miR-219-1-3p","hsa-miR-219-2-3p","hsa-miR-22-3p","hsa-miR-22-5p","hsa-miR-221-3p","hsa-miR-221-5p","hsa-miR-222-3p","hsa-miR-222-5p","hsa-miR-223-3p","hsa-miR-223-5p","hsa-miR-224-3p","hsa-miR-224-5p","hsa-miR-23a-3p","hsa-miR-23a-5p","hsa-miR-23b-3p","hsa-miR-23b-5p","hsa-miR-24-2-5p","hsa-miR-25-3p","hsa-miR-25-5p","hsa-miR-27a-3p","hsa-miR-27a-5p","hsa-miR-27b-3p","hsa-miR-27b-5p","hsa-miR-296-3p","hsa-miR-296-5p","hsa-miR-29a-3p","hsa-miR-29a-5p","hsa-miR-29c-3p","hsa-miR-29c-5p","hsa-miR-301b","hsa-miR-302a-3p","hsa-miR-302a-5p","hsa-miR-302b-3p","hsa-miR-302b-5p","hsa-miR-302c-3p","hsa-miR-302c-5p","hsa-miR-302d-3p","hsa-miR-302d-5p","hsa-miR-30b-3p","hsa-miR-30b-5p","hsa-miR-30e-3p","hsa-miR-30e-5p","hsa-miR-31-3p","hsa-miR-31-5p","hsa-miR-324-3p","hsa-miR-324-5p","hsa-miR-325","hsa-miR-326","hsa-miR-330-3p","hsa-miR-330-5p","hsa-miR-335-3p","hsa-miR-335-5p","hsa-miR-337-3p","hsa-miR-337-5p","hsa-miR-338-3p","hsa-miR-338-5p","hsa-miR-339-3p","hsa-miR-339-5p","hsa-miR-342-3p","hsa-miR-342-5p","hsa-miR-346","hsa-miR-34a-3p","hsa-miR-34a-5p","hsa-miR-34b-3p","hsa-miR-34b-5p","hsa-miR-34c-3p","hsa-miR-34c-5p","hsa-miR-361-3p","hsa-miR-361-5p","hsa-miR-363-3p","hsa-miR-363-5p","hsa-miR-369-3p","hsa-miR-369-5p","hsa-miR-370","hsa-miR-379-3p","hsa-miR-379-5p","hsa-miR-381","hsa-miR-382-3p","hsa-miR-382-5p","hsa-miR-383","hsa-miR-410","hsa-miR-423-3p","hsa-miR-423-5p","hsa-miR-425-3p","hsa-miR-425-5p","hsa-miR-429","hsa-miR-431-3p","hsa-miR-431-5p","hsa-miR-432-3p","hsa-miR-432-5p","hsa-miR-449a","hsa-miR-449b-3p","hsa-miR-449b-5p","hsa-miR-449c-3p","hsa-miR-449c-5p","hsa-miR-450b-3p","hsa-miR-450b-5p","hsa-miR-455-3p","hsa-miR-455-5p","hsa-miR-483-3p","hsa-miR-483-5p","hsa-miR-484","hsa-miR-485-3p","hsa-miR-485-5p","hsa-miR-486-3p","hsa-miR-486-5p","hsa-miR-487b","hsa-miR-488-3p","hsa-miR-488-5p","hsa-miR-489","hsa-miR-491-3p","hsa-miR-491-5p","hsa-miR-495","hsa-miR-496","hsa-miR-497-3p","hsa-miR-497-5p","hsa-miR-503","hsa-miR-504","hsa-miR-505-3p","hsa-miR-505-5p","hsa-miR-539-3p","hsa-miR-539-5p","hsa-miR-541-3p","hsa-miR-541-5p","hsa-miR-582-3p","hsa-miR-582-5p","hsa-miR-592","hsa-miR-598","hsa-miR-599","hsa-miR-615-3p","hsa-miR-615-5p","hsa-miR-665","hsa-miR-670","hsa-miR-671-3p","hsa-miR-671-5p","hsa-miR-7-1-3p","hsa-miR-708-3p","hsa-miR-708-5p","hsa-miR-718","hsa-miR-744-3p","hsa-miR-744-5p","hsa-miR-759","hsa-miR-760","hsa-miR-761","hsa-miR-762","hsa-miR-764","hsa-miR-767-3p","hsa-miR-767-5p","hsa-miR-770-5p","hsa-miR-802","hsa-miR-874","hsa-miR-875-3p","hsa-miR-875-5p","hsa-miR-96-3p","hsa-miR-96-5p"};
	
	
	public GetPublications(String filePath){
		ArrayList<DBColumn> result;
		try {
			FileOutputStream fos =new FileOutputStream(filePath);

		for (int i=0; i<microRNAs.length; i++){
		result= new Wrapper().requestDbContent(2, 
		" select distinct db_mirna.mirbase.name, ref_title,  ref_group_author, ref_location from db_mirna.mirbase"
		+ " inner join db_mirna.mirbasepid_has_mirbase on mirbase. id=mirbase_ID"
		+ " inner join db_mirna.mirbasepid on mirbasepid_ID=mirbasepid.id"
		+ " inner join embl_cross_ref on embl_cross_ref .identifier=db_mirna.mirbasepid.PID"
		+ " inner join embl_ref on embl_cross_ref.ref_id=embl_ref.ref_id"
		+ " where"
		+ " mirbase.name='"+ microRNAs[i] + "';");
		
		if (result!=null && !result.isEmpty())
		for (int j=0; j<result.size();j++){
			String s=result.get(j).getColumn()[0]+"\t"+result.get(j).getColumn()[1]+"\t"+result.get(j).getColumn()[2]+"\t"+result.get(j).getColumn()[3]+"\r\n";
			System.out.print(s);
			fos.write(s.getBytes());
		}
		}
		fos.flush();
		fos.close();
		JOptionPane.showMessageDialog(null, "Finish");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
