package gui;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;

public class JValidatedURLTextField extends JValidatedTextField {
	public JValidatedURLTextField() {
	}

	public JValidatedURLTextField(boolean validateLazy) {
		super(validateLazy);
	}

	@Override
	public String validateText(final String text) {
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		try {
			// Try parsing the api url to check if it's at least valid
			new URL(text);
			return null;
		} catch (final Exception e) {
			return "Needs to be a valid Url";
		}
	}
}
