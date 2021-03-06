/*
 * Copyright (C) 2010 BloatIt. This file is part of BloatIt. BloatIt is free
 * software: you can redistribute it and/or modify it under the terms of the GNU
 * Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * BloatIt is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details. You should have received a copy of the GNU Affero General Public
 * License along with BloatIt. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bloatit.web.linkable.features.create;

import static com.bloatit.framework.webprocessor.context.Context.tr;

import com.bloatit.data.DaoTeamRight.UserTeamRight;
import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.HtmlParagraph;
import com.bloatit.framework.webprocessor.components.HtmlTitleBlock;
import com.bloatit.framework.webprocessor.components.advanced.showdown.MarkdownEditor;
import com.bloatit.framework.webprocessor.components.form.FieldData;
import com.bloatit.framework.webprocessor.components.form.FormBuilder;
import com.bloatit.framework.webprocessor.components.form.HtmlDateField;
import com.bloatit.framework.webprocessor.components.form.HtmlDropDown;
import com.bloatit.framework.webprocessor.components.form.HtmlFormField;
import com.bloatit.framework.webprocessor.components.form.HtmlMoneyField;
import com.bloatit.framework.webprocessor.components.form.HtmlSubmit;
import com.bloatit.framework.webprocessor.components.form.HtmlTextField;
import com.bloatit.framework.webprocessor.components.javascript.JsShowHide;
import com.bloatit.framework.webprocessor.components.meta.HtmlElement;
import com.bloatit.framework.webprocessor.components.meta.HtmlMixedText;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.framework.webprocessor.url.UrlString;
import com.bloatit.model.Member;
import com.bloatit.web.components.HtmlElveosForm;
import com.bloatit.web.components.SidebarMarkdownHelp;
import com.bloatit.web.linkable.documentation.SideBarDocumentationBlock;
import com.bloatit.web.linkable.features.FeatureListPage;
import com.bloatit.web.linkable.master.Breadcrumb;
import com.bloatit.web.linkable.master.sidebar.TwoColumnLayout;
import com.bloatit.web.linkable.softwares.SoftwaresTools;
import com.bloatit.web.linkable.usercontent.AsTeamField;
import com.bloatit.web.linkable.usercontent.CreateUserContentPage;
import com.bloatit.web.url.CreateFeatureAndOfferActionUrl;
import com.bloatit.web.url.CreateFeatureAndOfferPageUrl;
import com.bloatit.web.url.CreateFeaturePageUrl;

/**
 * Page that hosts the form to create a new Feature
 */
@ParamContainer("feature/createwithoffer")
public final class CreateFeatureAndOfferPage extends CreateUserContentPage {

    public static final int FILE_MAX_SIZE_MIO = 2;

    private final CreateFeatureAndOfferPageUrl url;

    public CreateFeatureAndOfferPage(final CreateFeatureAndOfferPageUrl url) {
        super(url);
        this.url = url;
    }

    @Override
    protected String createPageTitle() {
        return Context.tr("Create new feature");
    }

    @Override
    public boolean isStable() {
        return false;
    }

    @Override
    public HtmlElement createRestrictedContent(final Member loggedUser) {
        return generateFeatureCreationForm(loggedUser);
    }

    private HtmlElement generateFeatureCreationForm(final Member loggedUser) {
        final TwoColumnLayout layout = new TwoColumnLayout(true, url);

        final HtmlTitleBlock offerPageContainer = new HtmlTitleBlock(Context.tr("Create a feature"), 1);

        // Create offer form
        final CreateFeatureAndOfferActionUrl targetUrl = new CreateFeatureAndOfferActionUrl(getSession().getShortKey());
        final HtmlElveosForm form = new HtmlElveosForm(targetUrl.urlString());
        FormBuilder ftool = new FormBuilder(CreateFeatureAndOfferAction.class, targetUrl);

        form.addLanguageChooser(targetUrl.getLocaleParameter().getName(), Context.getLocalizator().getLanguageCode());
        form.addAsTeamField(new AsTeamField(targetUrl,
                                            loggedUser,
                                            UserTeamRight.TALK,
                                            Context.tr("In the name of "),
                                            Context.tr("Write this offer in the name of a team, and offer the contributions to this team.")));

        // Title of the feature
        ftool.add(form, new HtmlTextField(targetUrl.getDescriptionParameter().getName()));

        // Linked software
        final FieldData newSoftwareNameFD = targetUrl.getNewSoftwareNameParameter().pickFieldData();
        final FieldData newSoftwareFD = targetUrl.getNewSoftwareParameter().pickFieldData();
        final SoftwaresTools.SoftwareChooserElement software = new SoftwaresTools.SoftwareChooserElement(targetUrl.getSoftwareParameter().getName(),
                                                                                                         newSoftwareNameFD.getName(),
                                                                                                         newSoftwareFD.getName());
        ftool.add(form, software);
        if (newSoftwareNameFD.getSuggestedValue() != null) {
            software.setNewSoftwareDefaultValue(newSoftwareNameFD.getSuggestedValue());
        }
        if (newSoftwareFD.getSuggestedValue() != null) {
            software.setNewSoftwareCheckboxDefaultValue(newSoftwareFD.getSuggestedValue());
        }

        // license
        final FieldData licenseData = targetUrl.getLicenseParameter().pickFieldData();
        final HtmlDropDown licenseInput = new HtmlDropDown(licenseData.getName());
        licenseInput.addDropDownElement("", Context.tr("Select a license…")).setDisabled().setSelected();
        licenseInput.addDropDownElement("Apache License 2.0", "Apache License 2.0");
        licenseInput.addDropDownElement("Artistic License/GPL", "Artistic License/GPL");
        licenseInput.addDropDownElement("GNU GPL v3", "GNU GPL v3");
        licenseInput.addDropDownElement("GNU GPL v2", "GNU GPL v2");
        licenseInput.addDropDownElement("GNU AGPL v3", "GNU AGPL v3");
        licenseInput.addDropDownElement("GNU Lesser GPL", "GNU Lesser GPL");
        licenseInput.addDropDownElement("MIT License", "MIT License");
        licenseInput.addDropDownElement("New BSD License", "New BSD License");
        licenseInput.addDropDownElement("Mozilla Public License 1.1", "Mozilla Public License 1.1");
        licenseInput.addDropDownElement("Eclipse Public License", "Eclipse Public License");
        licenseInput.addDropDownElement("Other Open Source", Context.tr("Other Open Source"));

        ftool.add(form, licenseInput);
        licenseInput.setComment(new HtmlMixedText(Context.tr("Licenses must be <0::OSI-approved>. You should use one of the listed licenses to avoid <1::license proliferation>."),
                                                  new UrlString("http://opensource.org/licenses").getHtmlLink(),
                                                  new UrlString(Context.tr("http://en.wikipedia.org/wiki/License_proliferation")).getHtmlLink()));

        //@formatter:off
        final String suggestedValue = tr(
                "Be precise, don't forget to specify :\n" +
                " - The expected result\n" +
                " - On which system it has to work (Windows/Mac/Linux ...)\n" +
                " - When do you want to have the result\n" +
                " - In which free license the result must be.\n" +
                "\n" +
                "You can also join a diagram, or a design/mockup of the expected user interface.\n" +
                "\n" +
                "Do not forget to specify if you want the result to be integrated upstream (in the official version of the software)"
                );
        //@formatter:on
        HtmlFormField specifInput = ftool.add(form, new MarkdownEditor(targetUrl.getSpecificationParameter().getName(), 10, 80));
        ftool.setDefaultValueIfNeeded(specifInput, suggestedValue);

        // Price field
        ftool.add(form, new HtmlMoneyField(targetUrl.getPriceParameter().getName()));

        // Date field
        ftool.add(form, new HtmlDateField(targetUrl.getExpiryDateParameter().getName(), Context.getLocalizator().getLocale()));

        final HtmlDiv validationDetails = new HtmlDiv();
        final HtmlParagraph showHideLink = new HtmlParagraph(Context.tr("Show validation details"));
        showHideLink.setCssClass("fake_link");

        final JsShowHide showHideValidationDetails = new JsShowHide(form, ftool.suggestedValueChanged(targetUrl.getPercentMajorParameter().getName())
                || ftool.suggestedValueChanged(targetUrl.getPercentFatalParameter().getName())
                || ftool.suggestedValueChanged(targetUrl.getDaysBeforeValidationParameter().getName()));
        showHideValidationDetails.setHasFallback(false);
        showHideValidationDetails.addActuator(showHideLink);
        showHideValidationDetails.addListener(validationDetails);
        // form.add(showHideLink);
        // form.add(validationDetails);
        showHideValidationDetails.apply();

        // days before validation
        ftool.add(validationDetails, new HtmlTextField(targetUrl.getDaysBeforeValidationParameter().getName()));

        // percent Fatal
        ftool.add(validationDetails, new HtmlTextField(targetUrl.getPercentFatalParameter().getName()));

        // percent Major
        ftool.add(validationDetails, new HtmlTextField(targetUrl.getPercentMajorParameter().getName()));

        form.addSubmit(new HtmlSubmit(Context.tr("Add another milestone")));
        form.addSubmit(new HtmlSubmit(Context.tr("Finish your Offer")).setName(targetUrl.getIsFinishedParameter().getName()));

        offerPageContainer.add(form);

        layout.addLeft(offerPageContainer);

        // RightColunm
        layout.addRight(new SideBarDocumentationBlock("create_feature"));
        layout.addRight(new SideBarDocumentationBlock("cc_by"));
        layout.addRight(new SidebarMarkdownHelp());

        return layout;
    }

    @Override
    public String getRefusalReason() {
        return tr("You must be logged to create a new feature.");
    }

    @Override
    protected Breadcrumb createBreadcrumb(final Member member) {
        return CreateFeatureAndOfferPage.generateBreadcrumb();
    }

    private static Breadcrumb generateBreadcrumb() {
        final Breadcrumb breadcrumb = FeatureListPage.generateBreadcrumb();
        breadcrumb.pushLink(new CreateFeaturePageUrl().getHtmlLink(tr("Create a feature")));
        return breadcrumb;
    }
}
