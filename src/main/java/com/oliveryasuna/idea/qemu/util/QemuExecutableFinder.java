package com.oliveryasuna.idea.qemu.util;

import com.oliveryasuna.commons.language.exception.UnsupportedInstantiationException;
import com.oliveryasuna.commons.language.marker.Utility;

import java.io.File;
import java.util.List;

@Utility
public final class QemuExecutableFinder {

  // Static fields
  //--------------------------------------------------

  private static final String QEMU_COMMAND_REGEX = "^qemu-system-.+$";

  // Static utility methods
  //--------------------------------------------------

  public static List<File> findQemuExecutables() {
    return CommandUtils.findExecutables(QEMU_COMMAND_REGEX);
  }

  // Constructors
  //--------------------------------------------------

  private QemuExecutableFinder() {
    super();

    throw new UnsupportedInstantiationException();
  }

}
