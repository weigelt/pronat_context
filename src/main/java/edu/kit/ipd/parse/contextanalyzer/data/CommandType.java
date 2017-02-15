package edu.kit.ipd.parse.contextanalyzer.data;

/**
 * This class represents all possible commandtypes of an instruction.
 * 
 * @author Vanessa Steurer
 */
public enum CommandType {
	//@formatter:off
	IF_STATEMENT("IF"),
	THEN_STATEMENT("THEN"),
	ELSE_STATEMENT("ELSE"),
	INDEPENDENT_STATEMENT("INDP");
	//@formatter: on
	private final String tag;
	
	private CommandType(String tag) {
		this.tag = tag;
	}

	public String toString() {
		return getTag();
	}

	protected String getTag() {
		return this.tag;
	}
	
	public static CommandType toCommandType(String tag) {
		if (tag.equals("IF")) {
			return CommandType.IF_STATEMENT;
		} else if (tag.equals("THEN")) {
			return CommandType.THEN_STATEMENT;
		} else if (tag.equals("ELSE")) {
			return CommandType.ELSE_STATEMENT;
		} else if (tag.equals("INDP")) {
			return CommandType.INDEPENDENT_STATEMENT;
		} else {
			return null;
		}
			
	}

}