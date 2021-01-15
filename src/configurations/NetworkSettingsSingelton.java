package configurations;

public class NetworkSettingsSingelton extends NetworkSettings {

	private static NetworkSettings settings = null;

	protected NetworkSettingsSingelton(){}

	public static NetworkSettings getInstance(){
		if (settings == null) {
			settings = new NetworkSettingsSingelton();
		}
		return settings;
	}
}

