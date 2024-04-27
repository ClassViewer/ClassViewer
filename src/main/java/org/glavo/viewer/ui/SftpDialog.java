/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.ui;

import com.google.common.net.InternetDomainName;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.textfield.CustomTextField;
import org.glavo.viewer.file.roots.sftp.SftpRoot;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.util.Stylesheet;

public final class SftpDialog extends Dialog<SftpDialog.Result> {

    public record Result(SftpRoot root, String password, String initPath) {
    }

    public SftpDialog() {
        DialogPane dialogPane = getDialogPane();
        Stylesheet.setStylesheet(dialogPane.getStylesheets());

        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(32);

        Label hostLabel = new Label(I18N.getString("sftp.host"));
        TextField hostField = new TextField();
        hostField.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHalignment(hostField, HPos.RIGHT);

        Label portLabel = new Label(I18N.getString("sftp.port"));
        TextField portField = new TextField();
        portField.setPromptText("22");
        portField.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHalignment(portField, HPos.RIGHT);
        portField.setMaxWidth(100);

        Label userLabel = new Label(I18N.getString("sftp.user"));
        CustomTextField userField = new CustomTextField();
        userField.setAlignment(Pos.CENTER_RIGHT);

        Label passwordLabel = new Label(I18N.getString("sftp.password"));
        PasswordField passwordField = new PasswordField();
        passwordField.setAlignment(Pos.CENTER_RIGHT);

        Label initPathLabel = new Label(I18N.getString("sftp.initPath"));
        CustomTextField initPathField = new CustomTextField();
        initPathField.setAlignment(Pos.CENTER_RIGHT);
        initPathField.setPromptText("/");

        grid.add(hostLabel, 0, 0);
        grid.add(hostField, 1, 0);
        grid.add(portLabel, 0, 1);
        grid.add(portField, 1, 1);
        grid.add(userLabel, 0, 2);
        grid.add(userField, 1, 2);
        grid.add(passwordLabel, 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(initPathLabel, 0, 4);
        grid.add(initPathField, 1, 4);

        dialogPane.setContent(grid);

        ButtonType connectButtonType = new ButtonType(I18N.getString("sftp.connect"), ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().setAll(connectButtonType, ButtonType.CANCEL);


        BooleanBinding invalid = Bindings.createBooleanBinding(() -> {
                    String host = hostField.getText();

                    if (host == null || host.isEmpty() || !InternetDomainName.isValid(host)) {
                        return true;
                    }

                    String port = portField.getText();
                    if (port != null && !port.isEmpty()) {
                        try {
                            int portNumber = Integer.parseInt(port);

                            if (portNumber <= 0 || portNumber > 65536) {
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            return true;
                        }
                    }
                    return false;
                },
                hostField.textProperty(), portField.textProperty(), userField.textProperty(), passwordField.textProperty()
        );

        dialogPane.lookupButton(connectButtonType).disableProperty().bind(invalid);

        this.setResultConverter(dialogButton -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            if (dialogButton != connectButtonType || invalid.get()) {
                return null;
            }

            String host = hostField.getText();
            String port = portField.getText();
            String user = userField.getText();
            String password = passwordField.getText();
            String initPath = initPathField.getText();

            return new Result(new SftpRoot(host, port == null || port.isEmpty() ? 22 : Integer.parseInt(port), user), password, initPath == null || initPath.isEmpty() ? "/" : initPath);
        });
    }
}
