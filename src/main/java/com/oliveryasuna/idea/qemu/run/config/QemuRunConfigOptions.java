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

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.oliveryasuna.commons.language.condition.Arguments;

import java.util.Objects;

public final class QemuRunConfigOptions extends RunConfigurationOptions {

  // Static fields
  //--------------------------------------------------

  public static final int DEFAULT_GDB_TCP_PORT = 2345;

  // Constructors
  //--------------------------------------------------

  QemuRunConfigOptions() {
    super();
  }

  // Fields
  //--------------------------------------------------

  private final StoredProperty<String> qemuExecutable = string(null)
      .provideDelegate(this, "qemuExecutable");

  private final StoredProperty<String> qemuArguments = string(null)
      .provideDelegate(this, "qemuArguments");

  private final StoredProperty<DiskImageSource> diskImageSource = doEnum(DiskImageSource.CMAKE_TARGET, DiskImageSource.class)
      .provideDelegate(this, "diskImageSource");

  private final StoredProperty<CMakeTarget> cmakeTarget = property((CMakeTarget)null, Objects::isNull)
      .provideDelegate(this, "cmakeTarget");

  private final StoredProperty<String> cdromFile = string(null)
      .provideDelegate(this, "cdromFile");

  private final StoredProperty<String> kernelFile = string(null)
      .provideDelegate(this, "kernelFile");

  private final StoredProperty<Boolean> enableGdb = property(true)
      .provideDelegate(this, "enableGdb");

  private final StoredProperty<Integer> gdbTcpPort = property(DEFAULT_GDB_TCP_PORT)
      .provideDelegate(this, "gdbTcpPort");

  private final StoredProperty<Boolean> qemuWaitForGdb = property(true)
      .provideDelegate(this, "qemuWaitForGdb");

  // Option getters/setters
  //--------------------------------------------------

  final String getQemuExecutable() {
    return qemuExecutable.getValue(this);
  }

  final void setQemuExecutable(final String qemuExecutable) {
    this.qemuExecutable.setValue(this, qemuExecutable);
  }

  final String getQemuArguments() {
    return qemuArguments.getValue(this);
  }

  final void setQemuArguments(final String qemuArguments) {
    this.qemuArguments.setValue(this, qemuArguments);
  }

  final DiskImageSource getDiskImageSource() {
    return diskImageSource.getValue(this);
  }

  final void setDiskImageSource(final DiskImageSource diskImageSource) {
    Arguments.requireNotNull(diskImageSource, "diskImageSource == null");

    this.diskImageSource.setValue(this, diskImageSource);
  }

  final CMakeTarget getCmakeTarget() {
    return cmakeTarget.getValue(this);
  }

  final void setCmakeTarget(final CMakeTarget cmakeTarget) {
    this.cmakeTarget.setValue(this, cmakeTarget);
  }

  final String getCdromFile() {
    return cdromFile.getValue(this);
  }

  final void setCdromFile(final String cdromFile) {
    this.cdromFile.setValue(this, cdromFile);
  }

  final String getKernelFile() {
    return kernelFile.getValue(this);
  }

  final void setKernelFile(final String kernelFile) {
    this.kernelFile.setValue(this, kernelFile);
  }

  final boolean isEnableGdb() {
    return enableGdb.getValue(this);
  }

  final void setEnableGdb(final boolean enableGdb) {
    this.enableGdb.setValue(this, enableGdb);
  }

  final int getGdbTcpPort() {
    return gdbTcpPort.getValue(this);
  }

  final void setGdbTcpPort(final int gdbTcpPort) {
    this.gdbTcpPort.setValue(this, gdbTcpPort);
  }

  final boolean isQemuWaitForGdb() {
    return qemuWaitForGdb.getValue(this);
  }

  final void setQemuWaitForGdb(final boolean qemuWaitForGdb) {
    this.qemuWaitForGdb.setValue(this, qemuWaitForGdb);
  }

  // DiskImageSource enum
  //--------------------------------------------------

  public enum DiskImageSource {

    CMAKE_TARGET,

    CDROM_FILE

  }

}
