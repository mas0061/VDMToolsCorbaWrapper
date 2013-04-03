package net.mas0061.vdm.corbaWrapper.exception;

import jp.vdmtools.api.ToolboxClient;
import jp.vdmtools.api.corba.ToolboxAPI.Error;
import jp.vdmtools.api.corba.ToolboxAPI.ErrorListHolder;

public class VDMSyntaxException extends Exception {
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 7905300375257376545L;

	private ErrorListHolder errors;

	public VDMSyntaxException() {
		super();
	}

	public VDMSyntaxException(String message) {
		super(message);
	}

	public VDMSyntaxException(ErrorListHolder errors) {
	  this.errors = errors;
	}

	public ErrorListHolder getErrors() {
//    for (Error error : errors.value) {
//      System.out.println("Error -> " + error.fname + " : " + error.line);
//      System.out.println(ToolboxClient.fromISO(error.msg));
//    }
	  return errors;
	}
}
