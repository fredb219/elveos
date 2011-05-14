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
package com.bloatit.web.linkable.contribution;

import static com.bloatit.framework.webprocessor.context.Context.tr;

import com.bloatit.data.DaoTeamRight.UserTeamRight;
import com.bloatit.framework.exceptions.lowlevel.RedirectException;
import com.bloatit.framework.webprocessor.annotations.ParamConstraint;
import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.framework.webprocessor.annotations.RequestParam;
import com.bloatit.framework.webprocessor.annotations.tr;
import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.HtmlTitleBlock;
import com.bloatit.framework.webprocessor.components.form.FieldData;
import com.bloatit.framework.webprocessor.components.form.HtmlForm;
import com.bloatit.framework.webprocessor.components.form.HtmlMoneyField;
import com.bloatit.framework.webprocessor.components.form.HtmlSubmit;
import com.bloatit.framework.webprocessor.components.form.HtmlTextArea;
import com.bloatit.framework.webprocessor.components.meta.HtmlElement;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.model.Feature;
import com.bloatit.model.Member;
import com.bloatit.model.right.AuthenticatedUserToken;
import com.bloatit.web.components.SideBarFeatureBlock;
import com.bloatit.web.linkable.documentation.SideBarDocumentationBlock;
import com.bloatit.web.linkable.features.FeaturePage;
import com.bloatit.web.linkable.usercontent.AsTeamField;
import com.bloatit.web.linkable.usercontent.CreateUserContentPage;
import com.bloatit.web.pages.master.Breadcrumb;
import com.bloatit.web.pages.master.sidebar.TwoColumnLayout;
import com.bloatit.web.url.CheckContributionActionUrl;
import com.bloatit.web.url.ContributePageUrl;

/**
 * A page that hosts the form used to contribute on a Feature
 */
@ParamContainer("contribute")
public final class ContributePage extends CreateUserContentPage {
    @RequestParam(conversionErrorMsg = @tr("The process is closed, expired, missing or invalid."))
    @ParamConstraint(optionalErrorMsg = @tr("The process is closed, expired, missing or invalid."))
    private final ContributionProcess process;

    private final ContributePageUrl url;

    public ContributePage(final ContributePageUrl url) {
        super(url, new CheckContributionActionUrl(url.getProcess()));
        this.url = url;
        process = url.getProcess();
    }

    @Override
    public HtmlElement createRestrictedContent(final Member loggedUser) throws RedirectException {
        final TwoColumnLayout layout = new TwoColumnLayout(true, url);
        layout.addLeft(generateContributeForm(loggedUser));
        layout.addRight(new SideBarFeatureBlock(process.getFeature(), new AuthenticatedUserToken(loggedUser)));
        layout.addRight(new SideBarDocumentationBlock("markdown"));

        return layout;
    }

    private HtmlElement generateContributeForm(final Member me) {
        final CheckContributionActionUrl formActionUrl = new CheckContributionActionUrl(process);
        final HtmlForm contribForm = new HtmlForm(formActionUrl.urlString());

        // Input field : choose amount
        final FieldData amountData = formActionUrl.getAmountParameter().pickFieldData();
        final HtmlMoneyField contribInput = new HtmlMoneyField(amountData.getName(), tr("Choose amount: "));
        final String suggestedAmountValue = amountData.getSuggestedValue();
        if (suggestedAmountValue != null) {
            contribInput.setDefaultValue(suggestedAmountValue);
        } else if (process.getAmount() != null) {
            contribInput.setDefaultValue(process.getAmount().toPlainString());
        }
        contribInput.addErrorMessages(amountData.getErrorMessages());
        contribInput.setComment(Context.tr("The minimum is 1€. Don't use cents."));

        // Input field : As team
        final AsTeamField teamField = addAsTeamField(contribForm,
                                                     me,
                                                     UserTeamRight.BANK,
                                                     tr("In the name of"),
                                                     tr("Talk in the name of this team and use its money to make a contribution."));
        if (process.getTeam() != null) {
            teamField.getTeamInput().setDefaultValue(process.getTeam().getId().toString());
        }

        // Input field : comment
        final FieldData commentData = formActionUrl.getCommentParameter().pickFieldData();
        final HtmlTextArea commentInput = new HtmlTextArea(commentData.getName(), tr("Comment: "), 3, 60);
        final String suggestedCommentValue = commentData.getSuggestedValue();
        if (suggestedCommentValue != null) {
            commentInput.setDefaultValue(suggestedCommentValue);
        } else if (process.getComment() != null) {
            commentInput.setDefaultValue(process.getComment());
        }
        commentInput.addErrorMessages(commentData.getErrorMessages());
        commentInput.setComment(Context.tr("Optional. The comment will be publicly visible in the contribution list. Max 140 characters."));

        final HtmlSubmit submitButton = new HtmlSubmit(tr("Contribute"));

        // Create the form
        contribForm.add(contribInput);
        contribForm.add(commentInput);
        contribForm.add(submitButton);

        final HtmlTitleBlock contribTitle = new HtmlTitleBlock(tr("Contribute"), 1);
        contribTitle.add(contribForm);

        final HtmlDiv group = new HtmlDiv();
        group.add(contribTitle);

        return group;
    }

    @Override
    protected String createPageTitle() {
        return tr("Contribute to a feature");
    }

    @Override
    public boolean isStable() {
        return false;
    }

    @Override
    public String getRefusalReason() {
        return tr("You must be logged to contribute");
    }

    @Override
    protected Breadcrumb createBreadcrumb(Member member) {
        return ContributePage.generateBreadcrumb(process.getFeature(), process);
    }

    private static Breadcrumb generateBreadcrumb(final Feature feature, final ContributionProcess process) {
        final Breadcrumb breadcrumb = FeaturePage.generateBreadcrumbContributions(feature);
        breadcrumb.pushLink(new ContributePageUrl(process).getHtmlLink(tr("Contribute")));
        return breadcrumb;
    }
}
