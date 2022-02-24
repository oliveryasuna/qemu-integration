package com.oliveryasuna.idea.qemu.util;

import com.oliveryasuna.commons.language.exception.UnsupportedInstantiationException;
import com.oliveryasuna.commons.language.marker.Utility;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Utility
public final class QemuExecutableFinder {

  // Static fields
  //--------------------------------------------------

  private static final File[] QEMU_EXECUTABLE_SEARCH_PATHS = new File[] {
      // Linux.
      new File("/usr/bin/"),

      // macOS.
      new File("/usr/local/bin/"),

      // Windows.
      new File("C:\\Program Files\\qemu")
  };

  private static final Pattern QEMU_EXECUTABLE_FILENAME_PATTERN = Pattern.compile("^qemu-system-.+$");

  private static final FilenameFilter QEMU_EXECUTABLE_FILENAME_FILTER = new QemuExecutableFilenameFilter();

  private static final List<File> QEMU_EXECUTABLES = Arrays.stream(QEMU_EXECUTABLE_SEARCH_PATHS)
      .filter(File::exists)
      .map(path -> path.listFiles(QEMU_EXECUTABLE_FILENAME_FILTER))
      .filter(Objects::nonNull)
      .flatMap(Arrays::stream)
      .collect(Collectors.toUnmodifiableList());

  // Static utility methods
  //--------------------------------------------------

  public static List<File> getQemuExecutables() {
    return QEMU_EXECUTABLES;
  }

  // Constructors
  //--------------------------------------------------

  private QemuExecutableFinder() {
    super();

    throw new UnsupportedInstantiationException();
  }

  // QemuExecutableFilenameFilter class
  //--------------------------------------------------

  private static final class QemuExecutableFilenameFilter implements FilenameFilter {

    // Constructors
    //--------------------------------------------------

    private QemuExecutableFilenameFilter() {
      super();
    }

    // FilenameFilter methods
    //--------------------------------------------------

    @Override
    public final boolean accept(final File dir, final String name) {
      return QEMU_EXECUTABLE_FILENAME_PATTERN.matcher(name).matches();
    }

  }

}
