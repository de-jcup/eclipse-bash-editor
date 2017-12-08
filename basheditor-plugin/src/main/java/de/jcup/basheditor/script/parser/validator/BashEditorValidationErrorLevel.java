package de.jcup.basheditor.script.parser.validator;

public enum BashEditorValidationErrorLevel {

	INFO("info"),
	
	WARNING("warning"), 

	ERROR("error"), 
	;
	

	private String id;

	private BashEditorValidationErrorLevel(String id) {
		if (id == null) {
			throw new IllegalArgumentException("id may not be null!");
		}
		this.id = id;
	}

	public String getId() {
		return id;
	}


	/**
	 * @param id
	 * @return error level, never <code>null</code>. If not identifiable by id the default will be returned: {@link BashEditorValidationErrorLevel#ERROR}
	 */
	public static BashEditorValidationErrorLevel fromId(String id) {
		if (WARNING.getId().equals(id)) {
			return WARNING;
		}
		if (INFO.getId().equals(id)) {
			return INFO;
		}
		return ERROR;
	}
}
