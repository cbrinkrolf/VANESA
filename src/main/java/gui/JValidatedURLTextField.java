package gui;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;

public class JValidatedURLTextField extends JValidatedTextField {

	private static final long serialVersionUID = 6456678031956212811L;

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
			URI.create(text).toURL();
			return null;
		} catch (final Exception e) {
			return "Needs to be a valid Url";
		}
	}
}
