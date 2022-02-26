/*
 * Copyright 2022 Oliver Yasuna
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oliveryasuna.idea.qemu.run.config;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.oliveryasuna.idea.qemu.run.QemuCommandLineState;
import com.oliveryasuna.idea.qemu.run.ui.QemuRunConfigurationEditor;

public final class QemuRunConfiguration extends RunConfigurationBase<QemuRunConfigurationOptions> {

  // Constructors
  //--------------------------------------------------

  QemuRunConfiguration(final Project project, final ConfigurationFactory factory, final String name) {
    super(project, factory, name);
  }

  // Option getters/setters
  //--------------------------------------------------

  public final String getQemuExecutable() {
    return getOptions().getQemuExecutable();
  }

  public final void setQemuExecutable(final String qemuExecutable) {
    getOptions().setQemuExecutable(qemuExecutable);
  }

  public final String getQemuArguments() {
    return getOptions().getQemuArguments();
  }

  public final void setQemuArguments(final String qemuArguments) {
    getOptions().setQemuArguments(qemuArguments);
  }

  public final QemuRunConfigurationOptions.DiskImageSource getDiskImageSource() {
    return getOptions().getDiskImageSource();
  }

  public final void setDiskImageSource(final QemuRunConfigurationOptions.DiskImageSource diskImageSource) {
    getOptions().setDiskImageSource(diskImageSource);
  }

  public final CMakeTarget getCmakeTarget() {
    return getOptions().getCmakeTarget();
  }

  public final void setCmakeTarget(final CMakeTarget cmakeTarget) {
    getOptions().setCmakeTarget(cmakeTarget);
  }

  public final String getCdromFile() {
    return getOptions().getCdromFile();
  }

  public final void setCdromFile(final String cdromFile) {
    getOptions().setCdromFile(cdromFile);
  }

  public final String getKernelFile() {
    return getOptions().getKernelFile();
  }

  public final void setKernelFile(final String kernelFile) {
    getOptions().setKernelFile(kernelFile);
  }

  public final boolean isEnableGdb() {
    return getOptions().isEnableGdb();
  }

  public final void setEnableGdb(final boolean enableGdb) {
    getOptions().setEnableGdb(enableGdb);
  }

  public final int getGdbTcpPort() {
    return getOptions().getGdbTcpPort();
  }

  public final void setGdbTcpPort(final int gdbTcpPort) {
    getOptions().setGdbTcpPort(gdbTcpPort);
  }

  public final boolean isQemuWaitForGdb() {
    return getOptions().isQemuWaitForGdb();
  }

  public final void setQemuWaitForGdb(final boolean qemuWaitForGdb) {
    getOptions().setQemuWaitForGdb(qemuWaitForGdb);
  }

  // RunConfigurationBase methods
  //--------------------------------------------------

  @Override
  protected final QemuRunConfigurationOptions getOptions() {
    return (QemuRunConfigurationOptions)super.getOptions();
  }

  // RunConfiguration methods
  //--------------------------------------------------

  @Override
  public final SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new QemuRunConfigurationEditor(getProject());
  }

  // RunProfile methods
  //--------------------------------------------------

  @Override
  public final RunProfileState getState(final Executor executor, final ExecutionEnvironment environment) throws ExecutionException {
    if(executor instanceof DefaultRunExecutor) {
      return new QemuCommandLineState(environment, this);
    } else {
      //noinspection DialogTitleCapitalization
      throw new ExecutionException("Expected DefaultRunExecutor.");
    }
  }

}
