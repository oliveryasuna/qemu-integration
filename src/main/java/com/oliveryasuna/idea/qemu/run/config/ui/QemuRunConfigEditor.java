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

package com.oliveryasuna.idea.qemu.run.config.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.ListCellRendererWithRightAlignedComponent;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import com.oliveryasuna.idea.qemu.run.config.QemuRunConfig;
import com.oliveryasuna.idea.qemu.run.config.QemuRunConfigOptions;
import com.oliveryasuna.idea.qemu.util.CommandUtils;
import com.oliveryasuna.idea.qemu.util.QemuExecutableFinder;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

// I hate Swing...
public class QemuRunConfigEditor extends SettingsEditor<QemuRunConfig> {

  // Constructors
  //--------------------------------------------------

  public QemuRunConfigEditor(final Project project) {
    super();

    this.project = project;
  }

  // Fields
  //--------------------------------------------------

  private final Project project;

  // UI components
  //--------------------------------------------------

  // TODO: JBPanel.
  private JPanel rootPanel;

  private ComboBox<String> qemuExecutableField;

  private ExpandableTextField qemuArgumentsField;

  // TODO: JBRadioButton.
  private JRadioButton cmakeTargetRadio;

  // TODO: JBRadioButton.
  private JRadioButton cdromFileRadio;

  private ComboBox<CMakeTarget> cmakeTargetField;

  private TextFieldWithBrowseButton cdromFileField;

  private JBCheckBox enableGdbCheckbox;

  private LabeledComponent<JBIntSpinner> gdbTcpPortField;

  // TODO: JBCheckBox.
  private JCheckBox qemuWaitForGdbCheckbox;

  // SettingsEditor methods
  //--------------------------------------------------

  @Override
  protected final void resetEditorFrom(final QemuRunConfig runConfig) {
    qemuExecutableField.setSelectedItem(runConfig.getQemuExecutable());
    qemuArgumentsField.setText(runConfig.getQemuArguments());

    switch(runConfig.getDiskImageSource()) {
      case CMAKE_TARGET:
        cmakeTargetRadio.doClick();
        break;
      case CDROM_FILE:
        cdromFileRadio.doClick();
        break;
    }

    cmakeTargetField.setSelectedItem(runConfig.getCmakeTarget());
    cdromFileField.setText(runConfig.getCdromFile());

    if(runConfig.isEnableGdb()) {
      enableGdbCheckbox.doClick();
    }

    gdbTcpPortField.getComponent().setValue(runConfig.getGdbTcpPort());

    if(runConfig.isQemuWaitForGdb()) {
      qemuWaitForGdbCheckbox.doClick();
    }
  }

  @Override
  protected final void applyEditorTo(final QemuRunConfig runConfig) throws ConfigurationException {
    runConfig.setQemuExecutable((String)qemuExecutableField.getSelectedItem());
    runConfig.setQemuArguments(qemuArgumentsField.getText());

    if(cmakeTargetRadio.isSelected()) {
      runConfig.setDiskImageSource(QemuRunConfigOptions.DiskImageSource.CMAKE_TARGET);
    } else if(cdromFileRadio.isSelected()) {
      runConfig.setDiskImageSource(QemuRunConfigOptions.DiskImageSource.CDROM_FILE);
    }

    runConfig.setCmakeTarget((CMakeTarget)cmakeTargetField.getSelectedItem());
    runConfig.setCdromFile(cdromFileField.getText());

    runConfig.setEnableGdb(enableGdbCheckbox.isSelected());

    runConfig.setGdbTcpPort((int)gdbTcpPortField.getComponent().getValue());

    runConfig.setQemuWaitForGdb(qemuWaitForGdbCheckbox.isSelected());
  }

  @Override
  protected final JComponent createEditor() {
    qemuExecutableField.setModel(new DefaultComboBoxModel<>(QemuExecutableFinder.findQemuExecutables().stream()
        .map(File::getAbsolutePath)
        .toArray(String[]::new)));

    cmakeTargetRadio.addActionListener(this::diskImageSourceRadioChanged);
    cdromFileRadio.addActionListener(this::diskImageSourceRadioChanged);

    cmakeTargetField.setModel(new ListComboBoxModel<>(new CMakeBuildConfigurationHelper(project).getTargets()));
    cmakeTargetField.setRenderer(new CMakeTargetCellRenderer());

    enableGdbCheckbox.addActionListener(this::enableGdbCheckboxChanged);

    final JBIntSpinner gdbTcpPortFieldComponent = new JBIntSpinner(QemuRunConfigOptions.DEFAULT_GDB_TCP_PORT, 1, 65535);

    ((JSpinner.DefaultEditor)gdbTcpPortFieldComponent.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);

    gdbTcpPortField.setComponent(gdbTcpPortFieldComponent);

    addQemuExecutableFieldValidator();
    addCMakeTargetFieldValidator();
    addCdromFileFieldValidator();

    return rootPanel;
  }

  // Validation methods
  //--------------------------------------------------

  private void addQemuExecutableFieldValidator() {
    new ComponentValidator(project)
        .withValidator(() -> {
          final String qemuExecutableCandidate = qemuExecutableField.getItem();

          if(StringUtils.isBlank(qemuExecutableCandidate)) return new ValidationInfo("Required field.", qemuExecutableField);
          if(!(QemuExecutableFinder.findQemuExecutables().contains(new File(qemuExecutableCandidate)) ||
              !CommandUtils.findExecutables(qemuExecutableCandidate).isEmpty()))
            return new ValidationInfo("Not found.", qemuExecutableField);

          return null;
        })
        .installOn(qemuExecutableField);

    qemuExecutableField.addActionListener(event -> ComponentValidator.getInstance(qemuExecutableField)
        .ifPresent(ComponentValidator::revalidate));
  }

  private void addCMakeTargetFieldValidator() {
    new ComponentValidator(project)
        .withValidator(() -> {
          final CMakeTarget cmakeTargetCandidate = cmakeTargetField.getItem();

          if(cmakeTargetField.isEnabled() && cmakeTargetCandidate == null) {
            return new ValidationInfo("Required field.", cmakeTargetField);
          }

          return null;
        })
        .installOn(cmakeTargetField);

    cmakeTargetField.addActionListener(event -> ComponentValidator.getInstance(cmakeTargetField)
        .ifPresent(ComponentValidator::revalidate));
  }

  private void addCdromFileFieldValidator() {
    // TODO: Doesn't work.

    new ComponentValidator(project)
        .withValidator(() -> {
          final String cdromFileCandidate = cdromFileField.getText();

          if(cdromFileField.isEnabled() && StringUtils.isBlank(cdromFileCandidate)) {
            return new ValidationInfo("Required field.", cdromFileField);
          }

          return null;
        })
        .installOn(cdromFileField);

    cdromFileField.addActionListener(event -> ComponentValidator.getInstance(cdromFileField)
        .ifPresent(ComponentValidator::revalidate));
  }

  // Listener callbacks
  //--------------------------------------------------

  private void diskImageSourceRadioChanged(final ActionEvent event) {
    final JRadioButton source = (JRadioButton)event.getSource();

    if(source == cmakeTargetRadio) {
      cmakeTargetField.setEnabled(true);
      cdromFileField.setEnabled(false);
    } else if(source == cdromFileRadio) {
      cmakeTargetField.setEnabled(false);
      cdromFileField.setEnabled(true);
    }
  }

  private void enableGdbCheckboxChanged(final ActionEvent event) {
    final boolean gdbFieldsEnabled = enableGdbCheckbox.isSelected();

    gdbTcpPortField.setEnabled(gdbFieldsEnabled);
    qemuWaitForGdbCheckbox.setEnabled(gdbFieldsEnabled);
  }

  // CMakeTargetCellRenderer class
  //--------------------------------------------------

  private static final class CMakeTargetCellRenderer extends ListCellRendererWithRightAlignedComponent<CMakeTarget> {

    // Constructors
    //--------------------------------------------------

    private CMakeTargetCellRenderer() {
      super();
    }

    // ListCellRendererWithRightAlignedComponent methods
    //--------------------------------------------------

    @Override
    protected final void customize(final CMakeTarget cmakeTarget) {
      if(cmakeTarget != null) {
        setIcon(cmakeTarget.getIcon());
        setLeftText(cmakeTarget.getName());
      }
    }

  }

}
