package com.oliveryasuna.idea.qemu.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.cpp.cmake.model.CMakeConfiguration;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import com.oliveryasuna.commons.language.condition.Arguments;
import com.oliveryasuna.idea.qemu.cmake.CMakeBuildProcessHandler;
import com.oliveryasuna.idea.qemu.run.config.QemuRunConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.OptionalInt;
import java.util.Scanner;

public final class QemuCommandLineState extends CommandLineState {

  // Logging
  //--------------------------------------------------

  private static final Logger LOGGER = Logger.getInstance(QemuCommandLineState.class);

  // Static fields
  //--------------------------------------------------

  private static File ACTIVE_QEMU_PID_FILE;

  // Static methods
  //--------------------------------------------------

  private static void killActiveQemuProcess() {
    final OptionalInt pid = readActiveQemuProcessPid();

    pid.ifPresent(OSProcessUtil::killProcess);
  }

  private static OptionalInt readActiveQemuProcessPid() {
    final Scanner scanner;

    try {
      scanner = new Scanner(ACTIVE_QEMU_PID_FILE);
    } catch(final FileNotFoundException e) {
      return OptionalInt.empty();
    }

    if(!scanner.hasNextInt()) {
      return OptionalInt.empty();
    }

    return OptionalInt.of(scanner.nextInt());
  }

  // Static initializers
  //--------------------------------------------------

  static {
    try {
      ACTIVE_QEMU_PID_FILE = File.createTempFile("clion-qemu-", ".pid");
    } catch(final IOException e) {
      LOGGER.error(e); // TODO: Error reporting.
    }
  }

  // Constructors
  //--------------------------------------------------

  public QemuCommandLineState(final ExecutionEnvironment environment, final QemuRunConfiguration runConfig) {
    super(environment);

    Arguments.requireNotNull(runConfig, "runConfig");

    this.runConfig = runConfig;
  }

  // Fields
  //--------------------------------------------------

  private final QemuRunConfiguration runConfig;

  private ProcessHandler mainProcess;

  private ProcessHandler cmakeProcess;

  private ProcessHandler qemuProcess;

  private boolean processFailed = false;

  // CommandLineState methods
  //--------------------------------------------------

  @Override
  protected final ProcessHandler startProcess() throws ExecutionException {
    if(!runConfig.isAllowRunningInParallel()) {
      killActiveQemuProcess();
    }

    mainProcess = new MainProcessHandler();

    switch(runConfig.getDiskImageSource()) {
      case CMAKE_TARGET:
        cmakeProcess = runCMakeBuild();
        break;
      case CDROM_FILE:
        runQemu(new File(runConfig.getCdromFile()));
        break;
    }

    return mainProcess;
  }

  // Methods
  //--------------------------------------------------

  private void runQemu(final File diskImage) {
    final GeneralCommandLine commandLine = createRunQemuCommandLine(diskImage);

    try {
      qemuProcess = ProcessHandlerFactory.getInstance().createProcessHandler(commandLine);
    } catch(final ExecutionException e) {
      LOGGER.error(e); // TODO: Error reporting.

      processFailed();

      return;
    }

    qemuProcess.addProcessListener(new QemuProcessAdaptor());
    qemuProcess.startNotify();
  }

  private GeneralCommandLine createRunQemuCommandLine(final File diskImage) {
    final GeneralCommandLine commandLine = new GeneralCommandLine(runConfig.getQemuExecutable());

    commandLine.addParameters("-cdrom", diskImage.getAbsolutePath());
    commandLine.addParameters("-pidfile", ACTIVE_QEMU_PID_FILE.getAbsolutePath());

    return commandLine;
  }

  private ProcessHandler runCMakeBuild() throws ExecutionException {
    final CMakeTarget target = runConfig.getCMakeTarget();

    if(target == null) {
      throw new ExecutionException("No selected CMake target.");
    }

    final CMakeBuildConfigurationHelper buildConfigHelper = new CMakeBuildConfigurationHelper(getEnvironment().getProject());
    final CMakeConfiguration config = buildConfigHelper.getDefaultConfiguration(target);

    if(config == null) {
      throw new ExecutionException("No CMake configuration for selected target.");
    }

    final CMakeBuildProcessHandler processHandler = new CMakeBuildProcessHandler(getEnvironment().getProject(), config);

    processHandler.addProcessListener(new CMakeBuildProcessAdaptor(config.getProductFile()));

    return processHandler;
  }

  private void processFailed() {
    processFailed = true;

    if(qemuProcess != null) qemuProcess.destroyProcess();

    mainProcess.destroyProcess();
  }

  // MainProcessHandler class
  //--------------------------------------------------

  private final class MainProcessHandler extends NopProcessHandler {

    // Constructors
    //--------------------------------------------------

    private MainProcessHandler() {
      super();
    }

    // NopProcessHandler methods
    //--------------------------------------------------

    @Override
    protected final void destroyProcessImpl() {
      destroyOtherProcesses();

      notifyProcessTerminated(processFailed ? 1 : 0);
    }

    // Methods
    //--------------------------------------------------

    private void destroyOtherProcesses() {
      if(QemuCommandLineState.this.cmakeProcess != null && !QemuCommandLineState.this.cmakeProcess.isProcessTerminated()) {
        QemuCommandLineState.this.cmakeProcess.destroyProcess();
      }

      if(QemuCommandLineState.this.qemuProcess != null && !QemuCommandLineState.this.qemuProcess.isProcessTerminated()) {
        QemuCommandLineState.this.qemuProcess.destroyProcess();
      }
    }

  }

  // CMakeBuildProcessAdaptor class
  //--------------------------------------------------

  private final class CMakeBuildProcessAdaptor extends ProcessAdapter {

    // Constructors
    //--------------------------------------------------

    private CMakeBuildProcessAdaptor(final File product) {
      super();

      Arguments.requireNotNull(product, "product == null");

      this.product = product;
    }

    // Fields
    //--------------------------------------------------

    private final File product;

    // ProcessAdaptor methods
    //--------------------------------------------------

    @Override
    public final void processTerminated(final ProcessEvent event) {
      if(event.getExitCode() != 0) {
        QemuCommandLineState.this.processFailed();

        return;
      }

      runQemu(product);
    }

  }

  // QemuProcessAdaptor class
  //--------------------------------------------------

  private final class QemuProcessAdaptor extends ProcessAdapter {

    // Constructors
    //--------------------------------------------------

    private QemuProcessAdaptor() {
      super();
    }

    // ProcessAdaptor methods
    //--------------------------------------------------

    @Override
    public final void processTerminated(final ProcessEvent event) {
      if(event.getExitCode() != 0) {
        QemuCommandLineState.this.processFailed();

        return;
      }

      mainProcess.destroyProcess();
    }

    @Override
    public final void onTextAvailable(final ProcessEvent event, final Key outputType) {
      QemuCommandLineState.this.mainProcess.notifyTextAvailable(event.getText(), outputType);
    }

  }

}
