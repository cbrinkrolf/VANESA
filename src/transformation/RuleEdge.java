package transformation;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleEdge {

	private String name;
	private String type;
	private RuleNode from;
	private RuleNode to;
	private Map<String, String> parameterMap = new HashMap<>();

	public RuleEdge(String name, String type, RuleNode from, RuleNode to) {
		this.name = name;
		this.type = type;
		this.from = from;
		this.to = to;
	}
}
