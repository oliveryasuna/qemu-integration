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

package com.oliveryasuna.idea.qemu.qemu;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.oliveryasuna.idea.qemu.run.config.QemuRunConfig;

final class QemuProgramRunner extends GenericProgramRunner {

  // Static fields
  //--------------------------------------------------

  private static final String RUN_EXECUTOR = DefaultRunExecutor.EXECUTOR_ID;

  // Constructors
  //--------------------------------------------------

  private QemuProgramRunner() {
    super();
  }

  // ProgramRunner methods
  //--------------------------------------------------

  @Override
  public final String getRunnerId() {
    return getClass().getCanonicalName();
  }

  @Override
  public final boolean canRun(final String executorId, final RunProfile profile) {
    return (profile instanceof QemuRunConfig && executorId.equals(RUN_EXECUTOR));
  }

  @Override
  protected RunContentDescriptor doExecute(final RunProfileState state, final ExecutionEnvironment environment) throws ExecutionException {
    final ExecutionResult executionResult = state.execute(environment.getExecutor(), this);

    if(executionResult == null) {
      return null;
    }

    return new RunContentBuilder(executionResult, environment)
        .showRunContent(environment.getContentToReuse());
  }

}
