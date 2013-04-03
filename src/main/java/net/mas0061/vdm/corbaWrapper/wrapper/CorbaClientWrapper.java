package net.mas0061.vdm.corbaWrapper.wrapper;

import java.util.List;

import jp.vdmtools.api.ToolboxClient;
import jp.vdmtools.api.corba.ToolboxAPI.APIError;
import jp.vdmtools.api.corba.ToolboxAPI.Error;
import jp.vdmtools.api.corba.ToolboxAPI.ErrorListHolder;
import jp.vdmtools.api.corba.ToolboxAPI.FileListHolder;
import jp.vdmtools.api.corba.ToolboxAPI.ModuleListHolder;
import jp.vdmtools.api.corba.ToolboxAPI.VDMApplication;
import jp.vdmtools.api.corba.ToolboxAPI.VDMInterpreter;
import jp.vdmtools.api.corba.ToolboxAPI.VDMParser;
import jp.vdmtools.api.corba.ToolboxAPI.VDMProject;
import jp.vdmtools.api.corba.VDM.VDMGeneric;
import net.mas0061.vdm.corbaWrapper.exception.VDMSyntaxException;
import net.mas0061.vdm.corbaWrapper.exception.VDMTypeCheckException;

public class CorbaClientWrapper {
  VDMAprCreator vdmAprCreator;
	VDMApplication vdmApr;

	public CorbaClientWrapper(String vppdePath) {
	  vdmAprCreator = new VDMAprCreator(vppdePath);
	  vdmApr = vdmAprCreator.get();
	}

	public VDMProject createProject(List<String> fileNameList) {
		VDMProject prj = vdmApr.GetProject();
		prj.New();

		try {
			for (String fileName : fileNameList) {
				prj.AddFile(ToolboxClient.toISO(fileName));
			}

			return prj;
		} catch (APIError e) {
			System.out.println(e.msg);
			destroy();
			throw new RuntimeException();
		}
	}

	public void syntaxCheck() throws VDMSyntaxException {
		FileListHolder fileList = new FileListHolder();
		vdmApr.GetProject().GetFiles(fileList);

		try {
			VDMParser parser = vdmApr.GetParser();

			if (!parser.ParseList(fileList.value)) {
				throw new VDMSyntaxException();
			}
		} catch (APIError e) {
      showErrors();
			raiseRuntimeException();
		} catch (VDMSyntaxException e) {
//      showErrors();
			System.out.println("Syntax check error!");
//			raiseRuntimeException();
			ErrorListHolder errors = getErrors();
      destroy();
      throw new VDMSyntaxException(errors);
		}
	}

	public void typeCheck() {
		ModuleListHolder moduleList = new ModuleListHolder();
		vdmApr.GetProject().GetModules(moduleList);

		try {
			if (!vdmApr.GetTypeChecker().TypeCheckList(moduleList.value)) {
				throw new VDMTypeCheckException();
			}
		} catch (APIError e) {
      showErrors();
			raiseRuntimeException();
		} catch (VDMTypeCheckException e) {
      showErrors();
			System.out.println("Type check error!");
			raiseRuntimeException();
		}
	}

	public void init() {
		VDMInterpreter interpreter = vdmApr.GetInterpreter();
		try {
			interpreter.Initialize();
		} catch (APIError e) {
      showErrors();
			System.out.println("init failed.");
			raiseRuntimeException();
		}
	}

	public void setEncode(String encode) {
		VDMInterpreter interpreter = vdmApr.GetInterpreter();
		try {
			interpreter.EvalCmd("encode " + encode);
		} catch (APIError e) {
			raiseRuntimeException();
		}
	}

	public void create(String objName, String expr) {
		final String createCmd = "create " + objName + " := " + expr;
		VDMInterpreter interpreter = vdmApr.GetInterpreter();
		try {
			interpreter.EvalCmd(createCmd);
		} catch (APIError e) {
      showErrors();
			raiseRuntimeException();
		}
	}

	public VDMGeneric run(String expr) {
		try {
			return vdmApr.GetInterpreter().EvalExpression((short) 0, expr);
		} catch (APIError e) {
      showErrors();
			destroy();
			throw new RuntimeException();
		}
	}

	public void destroy() {
	  vdmAprCreator.destroy();
	}

	public ErrorListHolder getErrors() {
    ErrorListHolder errors = new ErrorListHolder();
    vdmApr.GetErrorHandler().GetErrors(errors);
    return errors;
	}

	private void showErrors() {
		ErrorListHolder errors = new ErrorListHolder();
		vdmApr.GetErrorHandler().GetErrors(errors);

		for (Error error : errors.value) {
			System.out.println("Error -> " + error.fname + " : " + error.line);
			System.out.println(ToolboxClient.fromISO(error.msg));
		}
	}

	private void raiseRuntimeException() {
		destroy();
		throw new RuntimeException();
	}

	public VDMApplication getApp() {
	  return vdmApr;
	}
}
