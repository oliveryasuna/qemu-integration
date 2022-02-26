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

package com.oliveryasuna.idea.qemu.run.producer;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.jetbrains.cmake.psi.CMakeArgument;
import com.jetbrains.cmake.psi.CMakeCommand;
import com.oliveryasuna.idea.qemu.run.config.QemuConfigType;
import com.oliveryasuna.idea.qemu.run.config.QemuRunConfig;
import com.oliveryasuna.idea.qemu.util.CmakePsiUtils;
import org.apache.commons.lang3.tuple.Pair;

final class QemuRunConfigProducer extends RunConfigurationProducer<QemuRunConfig> {

  // Constructors
  //--------------------------------------------------

  public QemuRunConfigProducer() {
    super(true);
  }

  // RunConfigurationProducer methods
  //--------------------------------------------------

  @Override
  protected final boolean setupConfigurationFromContext(final QemuRunConfig runConfig, final ConfigurationContext context, final Ref<PsiElement> elementRef) {
    if(runConfig == null || context == null || elementRef == null) return false;

    final Pair<CMakeCommand, CMakeArgument> cmakeCommandArgumentPair = CmakePsiUtils.getCmakeTargetArgumentFromLeafPsi(elementRef.get());

    if(cmakeCommandArgumentPair.getLeft() == null || cmakeCommandArgumentPair.getRight() == null) {
      return false;
    }

    // TODO: Set CMake target in runConfig.

    // TODO: User's cannot run directly.
    //       They have to select a QEMU executable.

    return true;
  }

  @Override
  public final boolean isConfigurationFromContext(final QemuRunConfig runConfig, final ConfigurationContext context) {
    if(runConfig == null || context == null) return false;

    return true; // TODO.
  }

  @Override
  public final ConfigurationFactory getConfigurationFactory() {
    return QemuConfigType.getInstance().getConfigurationFactories()[0];
  }

}
