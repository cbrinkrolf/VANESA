package transformation;

import java.util.HashMap;
import java.util.Map;

public class RuleNode {
	private String name;
	private String type;
	private double x = 0;
	private double y = 0;
	private boolean isExactIncidence;
	private Map<String, String> parameterMap = new HashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public boolean isExactIncidence() {
		return isExactIncidence;
	}

	public void setExactIncidence(boolean exactIncidence) {
		isExactIncidence = exactIncidence;
	}

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<String, String> parameterMap) {
		this.parameterMap = parameterMap;
	}
}
