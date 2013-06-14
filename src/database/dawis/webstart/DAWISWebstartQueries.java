package database.dawis.webstart;

public class DAWISWebstartQueries {

	/** 
	-- Table structure for table `remote_control`
	--

	DROP TABLE IF EXISTS `remote_control`;
	CREATE TABLE `remote_control` (
	  `identifier` varchar(255) NOT NULL,
	  `rc_type` varchar(255) NOT NULL,
	  `rc_session` varchar(255) NOT NULL,
	  `dbname` varchar(255) NOT NULL,
	  rc_timestamp	timestamp,
	  PRIMARY KEY  (`identifier`,`session`,`db_name`)
	) ENGINE=InnoDB DEFAULT CHARSET=latin1;
	 
	 */
	
	/** OR 1 is only for testing purposes there, because you do not want,
	 *  to look up a session id every time to test the application
	 *  
	 *  in production environment it should be set to
	 *  select * from remote_control where rc_session=?
	 *  (without OR 1)
	 */
	public static final String getRCdata="select identifier, rc_type, dbname, rc_timestamp " +
			"from remote_control where (rc_timestamp>?) AND (rc_session=?)";
	//
	//public static final String getInhibitor="SELECT * ";
}
