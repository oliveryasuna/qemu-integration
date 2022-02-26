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

package com.oliveryasuna.idea.qemu.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.cmake.psi.CMakeArgument;
import com.jetbrains.cmake.psi.CMakeCommand;
import com.jetbrains.cmake.psi.CMakeCommandName;
import com.oliveryasuna.commons.language.condition.Arguments;
import com.oliveryasuna.commons.language.exception.UnsupportedInstantiationException;
import com.oliveryasuna.commons.language.marker.Utility;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

@Utility
public final class CmakePsiUtils {

  // Static fields
  //--------------------------------------------------

  private static final Set<String> CMAKE_TARGET_COMMANDS = Set.of("add_library", "add_executable", "add_custom_target");

  // Static utility methods
  //--------------------------------------------------

  public static Pair<CMakeCommand, CMakeArgument> getCmakeTargetArgumentFromLeafPsi(final PsiElement element) {
    Arguments.requireNotNull(element, "element == null");

    if(!(element instanceof LeafPsiElement)) {
      return Pair.of(null, null);
    }

    final CMakeArgument cmakeArgument = PsiTreeUtil.getParentOfType(element, CMakeArgument.class);

    if(cmakeArgument == null) {
      return Pair.of(null, null);
    }

    final CMakeCommand cmakeCommand = PsiTreeUtil.getParentOfType(cmakeArgument, CMakeCommand.class);

    if(cmakeCommand == null) {
      return Pair.of(null, null);
    }

    final CMakeCommandName cmakeCommandName = cmakeCommand.getCMakeCommandName();

    if(cmakeCommandName == null) {
      return Pair.of(null, null);
    }

    if(!CMAKE_TARGET_COMMANDS.contains(cmakeCommandName.getText())) {
      return Pair.of(null, null);
    }

    return Pair.of(cmakeCommand, cmakeArgument);
  }

  // Constructors
  //--------------------------------------------------

  private CmakePsiUtils() {
    super();

    throw new UnsupportedInstantiationException();
  }

}
