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

public final class QemuRunConfigurationOptions extends RunConfigurationOptions {

  // Constructors
  //--------------------------------------------------

  QemuRunConfigurationOptions() {
    super();
  }

  // Fields
  //--------------------------------------------------

  private final StoredProperty<String> qemuExecutable = string(null)
      .provideDelegate(this, "qemuExecutable");

  private final StoredProperty<DiskImageSource> diskImageSource = doEnum(DiskImageSource.CMAKE_TARGET, DiskImageSource.class)
      .provideDelegate(this, "diskImageSource");

  private final StoredProperty<CMakeTarget> cmakeTarget = property((CMakeTarget)null, Objects::isNull)
      .provideDelegate(this, "cmakeTarget");

  private final StoredProperty<String> cdromFile = string(null)
      .provideDelegate(this, "cdromFile");

  // Option getters/setters
  //--------------------------------------------------

  final String getQemuExecutable() {
    return qemuExecutable.getValue(this);
  }

  final void setQemuExecutable(final String qemuExecutable) {
    this.qemuExecutable.setValue(this, qemuExecutable);
  }

  final DiskImageSource getDiskImageSource() {
    return diskImageSource.getValue(this);
  }

  final void setDiskImageSource(final DiskImageSource diskImageSource) {
    Arguments.requireNotNull(diskImageSource, "diskImageSource == null");

    this.diskImageSource.setValue(this, diskImageSource);
  }

  final CMakeTarget getCMakeTarget() {
    return cmakeTarget.getValue(this);
  }

  final void setCMakeTarget(final CMakeTarget cmakeTarget) {
    this.cmakeTarget.setValue(this, cmakeTarget);
  }

  final String getCdromFile() {
    return cdromFile.getValue(this);
  }

  final void setCdromFile(final String cdromFile) {
    this.cdromFile.setValue(this, cdromFile);
  }

  // DiskImageSource enum
  //--------------------------------------------------

  public enum DiskImageSource {

    CMAKE_TARGET,

    CDROM_FILE

  }

}
