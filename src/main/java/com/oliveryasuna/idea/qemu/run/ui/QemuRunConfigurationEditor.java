package com.oliveryasuna.idea.qemu.run.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.ListCellRendererWithRightAlignedComponent;
import com.intellij.ui.components.JBCheckBox;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import com.oliveryasuna.idea.qemu.run.config.QemuRunConfiguration;
import com.oliveryasuna.idea.qemu.run.config.QemuRunConfigurationOptions;
import com.oliveryasuna.idea.qemu.util.ExecutableUtils;
import com.oliveryasuna.idea.qemu.util.QemuExecutableFinder;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.io.File;

// I hate Swing...
public class QemuRunConfigurationEditor extends SettingsEditor<QemuRunConfiguration> {

  // Constructors
  //--------------------------------------------------

  public QemuRunConfigurationEditor(final Project project) {
    super();

    this.project = project;
  }

  // Fields
  //--------------------------------------------------

  private final Project project;

  private Border qemuExecutableFieldErrorBorder;

  // UI components
  //--------------------------------------------------

  // TODO: JBPanel.
  private JPanel rootPanel;

  private ComboBox<String> qemuExecutableField;

  // TODO: JBRadioButton.
  private JRadioButton cmakeTargetRadio;

  // TODO: JBRadioButton.
  private JRadioButton cdromFileRadio;

  private ComboBox<CMakeTarget> cmakeTargetField;

  private TextFieldWithBrowseButton cdromFileField;

  private JBCheckBox enableGdbCheckbox;

  private LabeledComponent<JBIntSpinner> gdbTcpPortField;

  private JCheckBox qemuWaitForGdbCheckbox;

  // SettingsEditor methods
  //--------------------------------------------------

  @Override
  protected final void resetEditorFrom(final QemuRunConfiguration runConfig) {
    qemuExecutableField.setSelectedItem(runConfig.getQemuExecutable());

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
  protected final void applyEditorTo(final QemuRunConfiguration runConfig) throws ConfigurationException {
    runConfig.setQemuExecutable((String)qemuExecutableField.getSelectedItem());

    if(cmakeTargetRadio.isSelected()) {
      runConfig.setDiskImageSource(QemuRunConfigurationOptions.DiskImageSource.CMAKE_TARGET);
    } else if(cdromFileRadio.isSelected()) {
      runConfig.setDiskImageSource(QemuRunConfigurationOptions.DiskImageSource.CDROM_FILE);
    }

    runConfig.setCmakeTarget((CMakeTarget)cmakeTargetField.getSelectedItem());
    runConfig.setCdromFile(cdromFileField.getText());

    runConfig.setEnableGdb(enableGdbCheckbox.isSelected());

    runConfig.setGdbTcpPort((int)gdbTcpPortField.getComponent().getValue());
    
    runConfig.setQemuWaitForGdb(qemuWaitForGdbCheckbox.isSelected());
  }

  @Override
  protected final JComponent createEditor() {
    qemuExecutableField.setModel(new DefaultComboBoxModel<>(QemuExecutableFinder.getQemuExecutables().stream()
        .map(File::getAbsolutePath)
        .toArray(String[]::new)));

    cmakeTargetRadio.addActionListener(this::diskImageSourceRadioChanged);
    cdromFileRadio.addActionListener(this::diskImageSourceRadioChanged);

    cmakeTargetField.setModel(new ListComboBoxModel<>(new CMakeBuildConfigurationHelper(project).getTargets()));
    cmakeTargetField.setRenderer(new CMakeTargetCellRenderer());

    enableGdbCheckbox.addActionListener(this::enableGdbCheckboxChanged);

    final JBIntSpinner gdbTcpPortFieldComponent = new JBIntSpinner(QemuRunConfigurationOptions.DEFAULT_GDB_TCP_PORT, 1, 65535);

    ((JSpinner.DefaultEditor)gdbTcpPortFieldComponent.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);

    gdbTcpPortField.setComponent(gdbTcpPortFieldComponent);

    addQemuExecutableFieldValidator();
    addCMakeTargetFieldValidator();
    addCdromFileFieldValidator();

    qemuExecutableField.addActionListener(event -> fireEditorStateChanged());

    return rootPanel;
  }

  // Validation methods
  //--------------------------------------------------

  private void addQemuExecutableFieldValidator() {
    new ComponentValidator(project)
        .withValidator(() -> {
          final String qemuExecutableCandidate = qemuExecutableField.getItem();

          if(StringUtils.isBlank(qemuExecutableCandidate)) return new ValidationInfo("Required field.", qemuExecutableField);
          if(!ExecutableUtils.canExecute(qemuExecutableCandidate)) return new ValidationInfo("Not found.", qemuExecutableField);

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
