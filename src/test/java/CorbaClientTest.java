

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.vdmtools.api.corba.ToolboxAPI.ModuleListHolder;
import jp.vdmtools.api.corba.VDM.VDMError;
import jp.vdmtools.api.corba.VDM.VDMGeneric;
import jp.vdmtools.api.corba.VDM.VDMSequence;
import jp.vdmtools.api.corba.VDM.VDMSequenceHelper;
import junit.framework.Assert;
import net.mas0061.vdm.corbaWrapper.wrapper.CorbaClientWrapper;

import org.junit.After;
import org.junit.Test;

public class CorbaClientTest {
  private static final String VPPDE = "/Applications/vdmpp/bin/vppde";
//private static final String VPPDE = "C:\\Program Files\\The VDM++ Toolbox v9.0.2\\bin\\vppde.exe";

	private CorbaClientWrapper client = new CorbaClientWrapper(VPPDE);

	@Test
	public void testClient() {
	  String prjPath = "/Users/mas/Dropbox/repository/masrepo/VDM/PP/";
//		String prjPath = "D:\\svn\\masRepoGit\\VDM\\PP\\";
		List<String> fileNameList = Arrays.asList(prjPath + "FizzBuzz.vpp",
				prjPath + "io.vpp");

		client.createProject(fileNameList);
		client.setEncode("UTF-8");

		client.syntaxCheck();
		System.out.println("Syntax check is success.");

		client.typeCheck();
		System.out.println("Type check is success.");

		client.init();
		System.out.println("init is success.");

		String objName = "fizzBuzz";
		String className = "new FizzBuzz()";
		client.create(objName, className);
		System.out.println("create is success.");

		String expr = objName + ".FizzBuzzFunc()";
		VDMGeneric res = client.run(expr);
		if (res.IsSequence()) {
			List<String> list = seqToList(VDMSequenceHelper.narrow(res));
			for (String str : list) {
				System.out.println(str);
			}
		} else {
			System.out.println(convertToOiginal(res.ToAscii()));
		}
		System.out.println("run is success.");

		ModuleListHolder moduleList = new ModuleListHolder();
		client.getApp().GetProject().GetModules(moduleList);
		Assert.assertEquals(moduleList.value.length, 2);
	}

	@After
	public void afterTest() {
		client.destroy();
	}

	private List<String> seqToList(VDMSequence seq) {
		List<String> list = new ArrayList<>();

		for (int i = 1; i <= seq.Length(); i++) {
			try {
				list.add(convertToOiginal(seq.Index(i).ToAscii()));
			} catch (VDMError e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		return list;
	}

	private static String convertToOiginal(String unicode) {
		String org = unicode.length() > 2 ? unicode.substring(1,
				unicode.length() - 1) : unicode;

		if (org.startsWith("\\u")) {
			String[] codeStrs = org.split("\\\\u");
			int[] codePoints = new int[codeStrs.length - 1]; // 最初が空文字なのでそれを抜かす
			for (int i = 0; i < codePoints.length; i++) {
				codePoints[i] = Integer.parseInt(codeStrs[i + 1], 16);
			}
			return new String(codePoints, 0, codePoints.length);
		} else {
			return org;
		}
	}

}
