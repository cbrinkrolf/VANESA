package transformation;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleNode {

	private String name;
	private String type;
	private double x = 0;
	private double y = 0;
	private boolean isExactIncidence;
	private Map<String, String> parameterMap = new HashMap<>();

	public RuleNode() {
	}
}
