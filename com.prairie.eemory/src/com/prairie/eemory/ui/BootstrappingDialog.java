package com.prairie.eemory.ui;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.prairie.eemory.Constants;
import com.prairie.eemory.Messages;
import com.prairie.eemory.client.masterdata.EvernoteBrand;
import com.prairie.eemory.util.EvernoteUtil;
import com.prairie.eemory.util.IDialogSettingsUtil;
import com.prairie.eemory.util.ListUtil;
import com.prairie.eemory.util.MapUtil;

public class BootstrappingDialog {

    private final BootstrappingMessageDialog dialog;

    private static List<EvernoteBrand> brands;

    static {
        EvernoteBrand brand = EvernoteUtil.brand();
        if (brand == EvernoteBrand.EVERNOTE_YINXIANG) {
            brands = ListUtil.list(EvernoteBrand.EVERNOTE_YINXIANG, EvernoteBrand.EVERNOTE_INTERNATIONAL); // make sure we have two brands here.
        } else if (brand == EvernoteBrand.EVERNOTE_INTERNATIONAL) {
            brands = ListUtil.list(EvernoteBrand.EVERNOTE_INTERNATIONAL, EvernoteBrand.EVERNOTE_YINXIANG); // make sure we have two brands here.
        } else {
            assert brand == EvernoteBrand.EVERNOTE_SANDBOX;
            brands = ListUtil.list(EvernoteBrand.EVERNOTE_SANDBOX);
        }
    }

    public BootstrappingDialog(final Shell shell) {
        dialog = new BootstrappingMessageDialog(shell);
    }

    public boolean open() {
        dialog.open();
        return dialog.isOkClicked();
    }

    public static boolean show(final Shell shell) {
        return new BootstrappingDialog(shell).open();
    }

    private static class BootstrappingMessageDialog extends MessageDialog {

        private int okClicked;
        private int switchClicked;
        private String[] allBtnLabels;

        private Map<EvernoteBrand, String> switchBtnLabels;

        public BootstrappingMessageDialog(final Shell parentShell) {
            super(parentShell, Messages.Plugin_OAuth_Title, null, Messages.bind(Messages.Plugin_OAuth_TokenNotConfigured, brands.get(0).brandName()), MessageDialog.NONE, ArrayUtils.EMPTY_STRING_ARRAY, 0);
        }

        @Override
        protected Control createContents(final Composite parent) {
            initializeDialogUnits(parent);

            createButtonProps();
            setButtonLabels(allBtnLabels);

            return super.createContents(parent);
        }

        @Override
        public Image getImage() {
            return new Image(Display.getDefault(), getClass().getClassLoader().getResourceAsStream(Constants.OAUTH_EVERNOTE_TRADEMARK));
        }

        @Override
        protected Control createButtonBar(final Composite parent) {
            Composite composite = new Composite(parent, 0);

            GridLayoutFactory.fillDefaults().numColumns(0).equalWidth(false).applyTo(composite); // make column not equal width.
            GridDataFactory.fillDefaults().align(16777224, 16777216).span(2, 1).applyTo(composite);

            composite.setFont(parent.getFont());

            createButtonsForButtonBar(composite);

            return composite;
        }

        @Override
        protected void buttonPressed(final int buttonId) {
            if (buttonId == switchClicked) {
                Collections.reverse(brands);
                messageLabel.setText(message = Messages.bind(Messages.Plugin_OAuth_TokenNotConfigured, brands.get(0).brandName()));
                getButton(0).setText(getSwitchButtonLabel(brands.get(1)));
            } else if (buttonId == okClicked) {
                IDialogSettingsUtil.set(Constants.PLUGIN_SETTINGS_KEY_BRAND, brands.get(0).name());
                super.buttonPressed(buttonId);
            } else {
                super.buttonPressed(buttonId);
            }
        }

        public boolean isOkClicked() {
            return getReturnCode() == okClicked;
        }

        private void createButtonProps() {
            if (brands.get(0) == EvernoteBrand.EVERNOTE_SANDBOX) {
                allBtnLabels = ArrayUtils.toArray(Messages.Plugin_OAuth_Configure, Messages.Plugin_OAuth_NotNow); // make sure we don't have Switch button for sandbox mode.
                switchClicked = -1;
                okClicked = 0;
            } else if (brands.get(0) == EvernoteBrand.EVERNOTE_INTERNATIONAL || brands.get(0) == EvernoteBrand.EVERNOTE_YINXIANG) {
                allBtnLabels = ArrayUtils.toArray(getSwitchButtonLabel(brands.get(1)), Messages.Plugin_OAuth_Configure, Messages.Plugin_OAuth_NotNow);
                switchClicked = 0;
                okClicked = 1;
            } else {
                // should not happen
            }
        }

        private String getSwitchButtonLabel(final EvernoteBrand brand) {
            if (MapUtil.isEmpty(switchBtnLabels)) {
                createSwitchButtonLabels();
            }
            return switchBtnLabels.get(brand);
        }

        private void createSwitchButtonLabels() {
            int maxLen = calculateLabelMaxLen();
            switchBtnLabels = MapUtil.map();
            switchBtnLabels.put(EvernoteBrand.EVERNOTE_INTERNATIONAL, StringUtils.center(Messages.bind(Messages.Plugin_OAuth_SWITCH_INTL, EvernoteBrand.EVERNOTE_INTERNATIONAL.brandName()), maxLen));
            switchBtnLabels.put(EvernoteBrand.EVERNOTE_YINXIANG, StringUtils.center(Messages.bind(Messages.Plugin_OAuth_SWITCH_YXBJ, EvernoteBrand.EVERNOTE_YINXIANG.brandName()), maxLen));
        }

        private int calculateLabelMaxLen() {
            int maxLen = 0;
            for (EvernoteBrand b : EvernoteBrand.values()) {
                String label = StringUtils.EMPTY;
                if (b == EvernoteBrand.EVERNOTE_YINXIANG) {
                    label = Messages.bind(Messages.Plugin_OAuth_SWITCH_YXBJ, b.brandName());
                } else if (b == EvernoteBrand.EVERNOTE_INTERNATIONAL) {
                    label = Messages.bind(Messages.Plugin_OAuth_SWITCH_INTL, b.brandName());
                }
                int pixels = convertHorizontalDLUsToPixels(label.length());
                if (pixels > maxLen) {
                    maxLen = pixels;
                }
            }
            return maxLen;
        }
    }

}
