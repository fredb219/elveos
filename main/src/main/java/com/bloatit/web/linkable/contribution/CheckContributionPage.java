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

import static com.bloatit.framework.webserver.Context.tr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bloatit.common.Log;
import com.bloatit.framework.exceptions.RedirectException;
import com.bloatit.framework.exceptions.UnauthorizedOperationException;
import com.bloatit.framework.utils.Image;
import com.bloatit.framework.utils.Image.ImageType;
import com.bloatit.framework.webserver.Context;
import com.bloatit.framework.webserver.annotations.ParamConstraint;
import com.bloatit.framework.webserver.annotations.ParamContainer;
import com.bloatit.framework.webserver.annotations.RequestParam;
import com.bloatit.framework.webserver.annotations.tr;
import com.bloatit.framework.webserver.components.HtmlDiv;
import com.bloatit.framework.webserver.components.HtmlImage;
import com.bloatit.framework.webserver.components.HtmlLink;
import com.bloatit.framework.webserver.components.HtmlSpan;
import com.bloatit.framework.webserver.components.HtmlTitle;
import com.bloatit.framework.webserver.components.HtmlTitleBlock;
import com.bloatit.framework.webserver.components.form.HtmlMoneyField;
import com.bloatit.framework.webserver.components.meta.HtmlElement;
import com.bloatit.framework.webserver.components.meta.HtmlMixedText;
import com.bloatit.model.Feature;
import com.bloatit.model.Member;
import com.bloatit.model.Payline;
import com.bloatit.web.components.SideBarFeatureBlock;
import com.bloatit.web.linkable.features.FeaturePage;
import com.bloatit.web.linkable.features.FeaturesTools;
import com.bloatit.web.linkable.members.MembersTools;
import com.bloatit.web.linkable.money.Quotation;
import com.bloatit.web.linkable.money.Quotation.QuotationAmountEntry;
import com.bloatit.web.linkable.money.Quotation.QuotationDifferenceEntry;
import com.bloatit.web.linkable.money.Quotation.QuotationPercentEntry;
import com.bloatit.web.linkable.money.Quotation.QuotationProxyEntry;
import com.bloatit.web.linkable.money.Quotation.QuotationTotalEntry;
import com.bloatit.web.linkable.money.QuotationEntry;
import com.bloatit.web.linkable.softwares.SoftwaresTools;
import com.bloatit.web.pages.LoggedPage;
import com.bloatit.web.pages.master.Breadcrumb;
import com.bloatit.web.pages.master.DefineParagraph;
import com.bloatit.web.pages.master.TwoColumnLayout;
import com.bloatit.web.url.CheckContributionPageUrl;
import com.bloatit.web.url.ContributePageUrl;
import com.bloatit.web.url.ContributionActionUrl;
import com.bloatit.web.url.FeaturePageUrl;
import com.bloatit.web.url.PaylineActionUrl;

/**
 * A page that hosts the form used to check the contribution on a Feature
 */
@ParamContainer("contribute/check")
public final class CheckContributionPage extends LoggedPage {

    @RequestParam
    @ParamConstraint(optionalErrorMsg = @tr("The process is closed, expired, missing or invalid."))
    private final ContributionProcess process;

    private final CheckContributionPageUrl url;

    public CheckContributionPage(final CheckContributionPageUrl url) {
        super(url);
        this.url = url;
        process = url.getProcess();

    }

    @Override
    public void processErrors() throws RedirectException {
        addNotifications(url.getMessages());
        if (url.getMessages().hasMessage()) {
            session.notifyList(url.getMessages());
            throw new RedirectException(Context.getSession().getLastStablePage());
        }

    }

    @Override
    public HtmlElement createRestrictedContent() throws RedirectException {

        final TwoColumnLayout layout = new TwoColumnLayout(true);
        layout.addLeft(generateCheckContributeForm());

        layout.addRight(new SideBarFeatureBlock(process.getFeature()));

        return layout;
    }

    public HtmlElement generateCheckContributeForm() throws RedirectException {
        final HtmlTitleBlock group = new HtmlTitleBlock("Check contribution", 1);

        try {

            Feature feature = process.getFeature();
            Member member = session.getAuthToken().getMember();
            BigDecimal account = member.getInternalAccount().getAmount();

            if (process.getAmount().compareTo(account) <= 0) {

                generateWithMoneyContent(group, feature, member);

            } else {
                generateNoMoneyContent(group, feature, member, account);
            }

        } catch (UnauthorizedOperationException e) {
            Log.web().error("Fail to check contribution", e);
            throw new RedirectException(new FeaturePageUrl(process.getFeature()));
        }

        return group;
    }

    public void generateWithMoneyContent(final HtmlTitleBlock group, Feature feature, Member member) throws UnauthorizedOperationException {
        HtmlDiv contributionSummaryDiv = new HtmlDiv("contribution_summary");
        {
            contributionSummaryDiv.add(generateFeatureSummary(feature));

            HtmlDiv authorContributionSummary = new HtmlDiv("author_contribution_summary");
            {

                authorContributionSummary.add(new HtmlTitle(tr("Your account"), 2));

                HtmlDiv changeLine = new HtmlDiv("change_line");
                {

                    changeLine.add(new MoneyVariationBlock(member.getInternalAccount().getAmount(), member.getInternalAccount()
                                                                                                          .getAmount()
                                                                                                          .subtract(process.getAmount())));
                    changeLine.add(MembersTools.getMemberAvatar(member));
                }
                authorContributionSummary.add(changeLine);
                authorContributionSummary.add(new DefineParagraph(tr("Author:"), member.getDisplayName()));
                if (process.getComment() != null) {
                    authorContributionSummary.add(new DefineParagraph(tr("Comment:"), process.getComment()));
                } else {
                    authorContributionSummary.add(new DefineParagraph(tr("Comment:"), tr("No comment")));
                }

            }
            contributionSummaryDiv.add(authorContributionSummary);

        }
        group.add(contributionSummaryDiv);

        HtmlDiv buttonDiv = new HtmlDiv("contribution_actions");
        {
            ContributionActionUrl contributionActionUrl = new ContributionActionUrl(process);
            HtmlLink confirmContributionLink = contributionActionUrl.getHtmlLink(tr("Contribute {0}",
                                                                                    Context.getLocalizator()
                                                                                           .getCurrency(process.getAmount())
                                                                                           .getDefaultString()));
            confirmContributionLink.setCssClass("button");

            buttonDiv.add(confirmContributionLink);

            // Modify contribution button
            ContributePageUrl contributePageUrl = new ContributePageUrl(process);
            HtmlLink modifyContributionLink = contributePageUrl.getHtmlLink(tr("or modify contribution"));

            buttonDiv.add(modifyContributionLink);

        }
        group.add(buttonDiv);
    }

    private void generateNoMoneyContent(final HtmlTitleBlock group, Feature feature, Member member, BigDecimal account)
            throws UnauthorizedOperationException {

        HtmlDiv detailsLines = new HtmlDiv("quotation_details_lines");

        // Contribution

        detailsLines.add(new HtmlContributionLine(process));

        if (member.getInternalAccount().getAmount().compareTo(BigDecimal.ZERO) > 0) {
            detailsLines.add(new HtmlPrepaidLine(member));

        }

        detailsLines.add(new HtmlChargeAccountLine(member));

        BigDecimal missingAmount = process.getAmount().subtract(account);
        StandardQuotation quotation = new StandardQuotation(missingAmount);

        HtmlDiv totalsLines = new HtmlDiv("quotation_totals_lines");
        {

            HtmlDiv subtotal = new HtmlDiv("quotation_total_line");
            {
                subtotal.add(new HtmlDiv("label").addText(tr("Subtotal TTC")));
                subtotal.add(new HtmlDiv("money").addText(Context.getLocalizator()
                                                                 .getCurrency(quotation.subTotalTTCEntry.getValue())
                                                                 .getDecimalDefaultString()));
            }
            totalsLines.add(subtotal);

            HtmlDiv feesHT = new HtmlDiv("quotation_total_line_ht");
            {
                feesHT.add(new HtmlDiv("label").addText(tr("Fees HT")));
                feesHT.add(new HtmlDiv("money").addText(Context.getLocalizator().getCurrency(quotation.feesHT.getValue()).getDecimalDefaultString()));
            }
            totalsLines.add(feesHT);

            HtmlDiv feesTTC = new HtmlDiv("quotation_total_line");
            {

                HtmlLink showDetailLink = new HtmlLink("");

                HtmlSpan detailSpan = new HtmlSpan("details");
                detailSpan.add(new HtmlMixedText(tr("({0}% + {1}) <0::fees details>",
                                                    Payline.COMMISSION_VARIABLE_RATE.multiply(new BigDecimal("100")),
                                                    Context.getLocalizator().getCurrency(Payline.COMMISSION_FIX_RATE).getDecimalDefaultString()),
                                                 showDetailLink));

                feesTTC.add(new HtmlDiv("label").add(new HtmlMixedText(tr("Fees TTC <0::>"), detailSpan)));
                feesTTC.add(new HtmlDiv("money").addText(Context.getLocalizator().getCurrency(quotation.feesTTC.getValue()).getDecimalDefaultString()));
            }
            totalsLines.add(feesTTC);

            HtmlDiv totalHT = new HtmlDiv("quotation_total_line_ht");
            {
                totalHT.add(new HtmlDiv("label").addText(tr("Total HT")));
                totalHT.add(new HtmlDiv("money").addText(Context.getLocalizator().getCurrency(quotation.totalHT.getValue()).getDecimalDefaultString()));
            }
            totalsLines.add(totalHT);

            HtmlDiv totalTTC = new HtmlDiv("quotation_total_line_total");
            {
                totalTTC.add(new HtmlDiv("label").addText(tr("Total TTC")));
                totalTTC.add(new HtmlDiv("money").addText(Context.getLocalizator()
                                                                 .getCurrency(quotation.totalTTC.getValue())
                                                                 .getDecimalDefaultString()));
            }
            totalsLines.add(totalTTC);

        }

        // Pay block

        HtmlDiv payBlock = new HtmlDiv("pay_actions");
        {

            // Pay later button
            HtmlLink continueNavigation = new HtmlLink("", tr("Pay later"));
            payBlock.add(continueNavigation);

            final PaylineActionUrl payActionUrl = new PaylineActionUrl();
            payActionUrl.setAmount(quotation.subTotalTTCEntry.getValue());

            HtmlLink payContributionLink = payActionUrl.getHtmlLink(tr("Pay {0}", Context.getLocalizator()
                                                                                         .getCurrency(quotation.totalTTC.getValue())
                                                                                         .getDecimalDefaultString()));
            payContributionLink.setCssClass("button");
            payBlock.add(payContributionLink);

        }

        group.add(detailsLines);
        group.add(new HtmlDiv("quotation_totals_lines_block").add(totalsLines).add(payBlock));


    }

    public static class HtmlContributionLine extends HtmlDiv {

        public HtmlContributionLine(ContributionProcess contribution) throws UnauthorizedOperationException {
            super("quotation_detail_line");

            add(SoftwaresTools.getSoftwareLogoSmall(contribution.getFeature().getSoftware()));

            add(new HtmlDiv("quotation_detail_line_money").addText(Context.getLocalizator()
                                                                          .getCurrency(contribution.getFeature().getContribution())
                                                                          .getDefaultString()));
            add(new HtmlDiv().setCssClass("quotation_detail_line_money_image").add(new HtmlImage(new Image("money_up_small.png", ImageType.LOCAL),
                                                                                                 "money up")));
            add(new HtmlDiv("quotation_detail_line_money").addText(Context.getLocalizator()
                                                                          .getCurrency(contribution.getFeature()
                                                                                                   .getContribution()
                                                                                                   .add(contribution.getAmount()))
                                                                          .getDefaultString()));

            add(new HtmlDiv("quotation_detail_line_categorie").addText(tr("Contribution")));
            add(new HtmlDiv("quotation_detail_line_description").addText(FeaturesTools.getTitle(contribution.getFeature())));

            HtmlDiv amountBlock = new HtmlDiv("quotation_detail_line_amount");

            amountBlock.add(new HtmlDiv("quotation_detail_line_amount_money").addText(Context.getLocalizator()
                                                                                             .getCurrency(contribution.getAmount())
                                                                                             .getDecimalDefaultString()));

            // Modify contribution button
            ContributePageUrl contributePageUrl = new ContributePageUrl(contribution);
            HtmlLink modifyContributionLink = contributePageUrl.getHtmlLink(tr("modify"));
            HtmlLink deleteContributionLink = contributePageUrl.getHtmlLink(tr("delete"));
            // TODO: real delete button
            amountBlock.add(new HtmlDiv("quotation_detail_line_amount_modify").add(modifyContributionLink).addText(" - ").add(deleteContributionLink));

            add(amountBlock);

        }
    }

    public static class HtmlPrepaidLine extends HtmlDiv {

        public HtmlPrepaidLine(Member member) throws UnauthorizedOperationException {
            super("quotation_detail_line");

            add(MembersTools.getMemberAvatarSmall(member));

            add(new HtmlDiv("quotation_detail_line_money").addText(Context.getLocalizator()
                                                                          .getCurrency(member.getInternalAccount().getAmount())
                                                                          .getDefaultString()));
            add(new HtmlDiv().setCssClass("quotation_detail_line_money_image").add(new HtmlImage(new Image("money_down_small.png", ImageType.LOCAL),
                                                                                                 "money up")));
            add(new HtmlDiv("quotation_detail_line_money").addText(Context.getLocalizator().getCurrency(BigDecimal.ZERO).getDefaultString()));

            add(new HtmlDiv("quotation_detail_line_categorie").addText(tr("Prepaid from internal account")));

            HtmlDiv amountBlock = new HtmlDiv("quotation_detail_line_amount");

            amountBlock.add(new HtmlDiv("quotation_detail_line_amount_money").addText(Context.getLocalizator()
                                                                                             .getCurrency(member.getInternalAccount()
                                                                                                                .getAmount()
                                                                                                                .negate())
                                                                                             .getDecimalDefaultString()));

            add(amountBlock);

        }
    }

    public static class HtmlChargeAccountLine extends HtmlDiv {

        public HtmlChargeAccountLine(Member member) throws UnauthorizedOperationException {
            super("quotation_detail_line");

            add(MembersTools.getMemberAvatarSmall(member));

            add(new HtmlDiv("quotation_detail_line_money").addText(Context.getLocalizator().getCurrency(BigDecimal.ZERO).getDefaultString()));
            add(new HtmlDiv().setCssClass("quotation_detail_line_money_image").add(new HtmlImage(new Image("money_up_small.png", ImageType.LOCAL),
                                                                                                 "money up")));
            add(new HtmlDiv("quotation_detail_line_money").addText(Context.getLocalizator().getCurrency(BigDecimal.ZERO).getDefaultString()));

            add(new HtmlDiv("quotation_detail_line_categorie").addText(tr("Internal account")));
            add(new HtmlDiv("quotation_detail_line_description").addText(tr("Load money in your internal account for future contributions.")));

            HtmlDiv amountBlock = new HtmlDiv("quotation_detail_line_field");

            HtmlMoneyField moneyField = new HtmlMoneyField("preload_field");
            moneyField.setDefaultValue("0");
            amountBlock.add(moneyField);

            add(amountBlock);

        }
    }

    public HtmlDiv generateFeatureSummary(Feature feature) throws UnauthorizedOperationException {
        HtmlDiv featureContributionSummary = new HtmlDiv("feature_contribution_summary");
        {
            featureContributionSummary.add(new HtmlTitle(tr("The feature"), 2));

            HtmlDiv changeLine = new HtmlDiv("change_line");
            {

                changeLine.add(SoftwaresTools.getSoftwareLogo(feature.getSoftware()));
                changeLine.add(new MoneyVariationBlock(feature.getContribution(), feature.getContribution().add(process.getAmount())));
            }
            featureContributionSummary.add(changeLine);

            featureContributionSummary.add(new DefineParagraph(tr("Target feature:"), FeaturesTools.getTitle(feature)));

        }
        return featureContributionSummary;
    }

    private class StandardQuotation {

        final public QuotationEntry subTotalTTCEntry;
        final public QuotationEntry feesHT;
        final public QuotationEntry feesTTC;
        final public QuotationEntry totalHT;
        final public QuotationEntry totalTTC;
        final public QuotationEntry feesDetails;

        public StandardQuotation(BigDecimal amount) {

            String fixBank = "0.30";
            String variableBank = "0.03";
            String TVAInvertedRate = "0.836120401";

            Quotation quotation = new Quotation(Payline.computateAmountToPay(amount));

            subTotalTTCEntry = new QuotationAmountEntry("Subtotal TTC", null, amount);

            // Fees TTC
            QuotationTotalEntry feesTotal = new QuotationTotalEntry(null, null, null);
            QuotationPercentEntry feesVariable = new QuotationPercentEntry("Fees", null, subTotalTTCEntry, Payline.COMMISSION_VARIABLE_RATE);
            QuotationAmountEntry feesFix = new QuotationAmountEntry("Fees", null, Payline.COMMISSION_FIX_RATE);
            feesTotal.addEntry(feesVariable);
            feesTotal.addEntry(feesFix);

            feesTTC = feesTotal;

            // Fees HT

            feesHT = new QuotationPercentEntry("Fees HT", null, feesTotal, new BigDecimal(TVAInvertedRate));

            // Total TTC
            totalTTC = quotation;

            // Total HT
            totalHT = new QuotationTotalEntry("Fees HT", null, null).addEntry(feesHT).addEntry(subTotalTTCEntry);

            // Fees details
            // Bank fees
            QuotationTotalEntry bankFeesTotal = new QuotationTotalEntry("Bank fees", null, "Total bank fees");

            QuotationAmountEntry fixBankFee = new QuotationAmountEntry("Fix fee", null, new BigDecimal(fixBank));

            QuotationPercentEntry variableBankFee = new QuotationPercentEntry("Variable fee",
                                                                              "" + Float.valueOf(variableBank) * 100 + "%",
                                                                              quotation,
                                                                              new BigDecimal(variableBank));
            bankFeesTotal.addEntry(variableBankFee);
            bankFeesTotal.addEntry(fixBankFee);

            // Our fees
            QuotationDifferenceEntry commission = new QuotationDifferenceEntry("Elveos's commission TTC", null, feesTotal, bankFeesTotal);

            // Fees Details proxy
            feesDetails = new QuotationProxyEntry("Fees details", null, feesTTC);
            feesDetails.addEntry(commission);
            feesDetails.addEntry(bankFeesTotal);

            quotation.addEntry(subTotalTTCEntry);
            quotation.addEntry(feesTTC);

        }
    }

    @Override
    protected String getPageTitle() {
        return tr("Contribute to a feature - check");
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
    protected Breadcrumb getBreadcrumb() {
        return CheckContributionPage.generateBreadcrumb(process.getFeature(), process);
    }

    public static Breadcrumb generateBreadcrumb(Feature feature, ContributionProcess process) {
        Breadcrumb breadcrumb = FeaturePage.generateBreadcrumbContributions(feature);

        breadcrumb.pushLink(new CheckContributionPageUrl(process).getHtmlLink(tr("Contribute - Check")));

        return breadcrumb;
    }

    @Override
    protected List<String> getCustomCss() {
        List<String> cssList = new ArrayList<String>();
        cssList.add("check_contribution.css");
        return cssList;
    }

}