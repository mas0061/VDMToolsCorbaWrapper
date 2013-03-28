package net.mas0061.vdm.corbaWrapper.wrapper;

import java.io.IOException;

import jp.vdmtools.api.ToolboxClient;
import jp.vdmtools.api.ToolboxClient.CouldNotResolveObjectException;
import jp.vdmtools.api.corba.ToolboxAPI.APIError;
import jp.vdmtools.api.corba.ToolboxAPI.ToolType;
import jp.vdmtools.api.corba.ToolboxAPI.VDMApplication;
import jp.vdmtools.api.corba.ToolboxAPI.VDMApplicationHelper;

import org.omg.CORBA.Object;

public class VDMAprCreator {
  private Process vppdeProcess;

  private VDMApplication vdmApp;
  private short clientId;

  public VDMAprCreator(String vppdePath) {
    launchVppde(vppdePath);
    registerApp();
  }

  private void launchVppde(String vppdePath) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(vppdePath);
      vppdeProcess = processBuilder.start();
      Thread.sleep(2500);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void registerApp() {
    try {
      ToolboxClient toolboxClient = new ToolboxClient();
      Object obj = toolboxClient.getVDMApplication(new String[] {},
          ToolType.PP_TOOLBOX);
      vdmApp = VDMApplicationHelper.narrow(obj);
      clientId = vdmApp.Register();
      vdmApp.PushTag(clientId);
    } catch (CouldNotResolveObjectException e) {
      throw new RuntimeException(e);
    }
  }

  public VDMApplication get() {
    return vdmApp;
  }

  public void destroy() {
    try {
      vdmApp.DestroyTag(clientId);
      vppdeProcess.destroy();
    } catch (APIError e) {
      e.printStackTrace();
    }
  }
}
