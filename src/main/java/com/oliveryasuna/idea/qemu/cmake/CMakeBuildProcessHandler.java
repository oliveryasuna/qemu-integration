package com.oliveryasuna.idea.qemu.cmake;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.cpp.cmake.model.CMakeConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.cpp.execution.build.CMakeBuild;

import java.io.OutputStream;

public final class CMakeBuildProcessHandler extends ProcessHandler {

  // Constructors
  //--------------------------------------------------

  public CMakeBuildProcessHandler(final Project project, final CMakeConfiguration cmakeConfig) {
    super();

    CMakeBuild.build(project, new CMakeAppRunConfiguration.BuildAndRunConfigurations(cmakeConfig), new CMakeProcessAdaptor());
  }

  // Fields
  //--------------------------------------------------

  private ProcessHandler cmakeProcessHandler;

  // ProcessHandler methods
  //--------------------------------------------------

  @Override
  protected final void destroyProcessImpl() {
    if(cmakeProcessHandler != null) {
      cmakeProcessHandler.destroyProcess();
    }
  }

  @Override
  protected final void detachProcessImpl() {
    if(cmakeProcessHandler != null) {
      cmakeProcessHandler.detachProcess();
    }
  }

  @Override
  public final boolean detachIsDefault() {
    return (cmakeProcessHandler != null && cmakeProcessHandler.detachIsDefault());
  }

  @Override
  public final OutputStream getProcessInput() {
    return null;
  }

  // CMakeProcessAdaptor class
  //--------------------------------------------------

  private final class CMakeProcessAdaptor extends ProcessAdapter {

    // Constructors
    //--------------------------------------------------

    private CMakeProcessAdaptor() {
      super();
    }

    // ProcessAdaptor methods
    //--------------------------------------------------

    @Override
    public final void startNotified(final ProcessEvent event) {
      CMakeBuildProcessHandler.this.cmakeProcessHandler = event.getProcessHandler();

      CMakeBuildProcessHandler.this.startNotify();
    }

    @Override
    public final void processTerminated(final ProcessEvent event) {
      CMakeBuildProcessHandler.this.notifyProcessTerminated(event.getExitCode());
    }

    @Override
    public final void onTextAvailable(final ProcessEvent event, final Key outputType) {
      CMakeBuildProcessHandler.this.notifyTextAvailable(event.getText(), outputType);
    }

  }

}
