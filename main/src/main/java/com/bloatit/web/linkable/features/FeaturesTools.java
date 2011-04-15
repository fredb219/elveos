package com.bloatit.web.linkable.features;

import static com.bloatit.framework.webprocessor.context.Context.trn;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import com.bloatit.framework.exceptions.highlevel.ShallNotPassException;
import com.bloatit.framework.exceptions.lowlevel.UnauthorizedOperationException;
import com.bloatit.framework.utils.Image;
import com.bloatit.framework.utils.PageIterable;
import com.bloatit.framework.utils.i18n.CurrencyLocale;
import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.HtmlImage;
import com.bloatit.framework.webprocessor.components.HtmlParagraph;
import com.bloatit.framework.webprocessor.components.HtmlSpan;
import com.bloatit.framework.webprocessor.components.HtmlTitle;
import com.bloatit.framework.webprocessor.components.meta.HtmlBranch;
import com.bloatit.framework.webprocessor.components.meta.HtmlElement;
import com.bloatit.framework.webprocessor.components.meta.HtmlMixedText;
import com.bloatit.framework.webprocessor.components.meta.XmlNode;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.model.Comment;
import com.bloatit.model.Feature;
import com.bloatit.model.Offer;
import com.bloatit.model.Software;
import com.bloatit.model.Translation;
import com.bloatit.model.feature.FeatureImplementation;
import com.bloatit.web.WebConfiguration;
import com.bloatit.web.components.HtmlProgressBar;
import com.bloatit.web.linkable.softwares.SoftwaresTools;
import com.bloatit.web.url.FeaturePageUrl;

public class FeaturesTools {

    private static final String IMPORTANT_CSS_CLASS = "important";

    public static String getTitle(final Feature feature) throws UnauthorizedOperationException {
        final Locale defaultLocale = Context.getLocalizator().getLocale();
        final Translation translatedDescription = feature.getDescription().getTranslationOrDefault(defaultLocale);
        return translatedDescription.getTitle();
    }

    /**
     * Convenience method for {@link #generateFeatureTitle(Feature, boolean)}
     * with <i>isTitle</i> set to false
     * 
     * @param feature the feature for which a block will be generated
     * @return the generated block
     * @throws UnauthorizedOperationException when some operation cannot be
     *             accessed
     */

    public static HtmlBranch generateFeatureTitle(final Feature feature) throws UnauthorizedOperationException {
        return generateFeatureTitle(feature, false);
    }

    /**
     * Generates a block indicating the title of a given <code>feature</code>
     * <p>
     * The block contains:
     * <li>The software name (and a link to the software page) of the
     * <code>feature</code></li>
     * <li>The feature title</li>
     * </p>
     * <p>
     * Note:
     * <li>If <code>isTitle</code> is <i>true</i>, the whole block will be
     * rendered as a title</li>
     * <li>If <code>isTitle</code> is <i>true</i>, the whole block will be
     * rendered as a normal text, and a link to the feature page will be added</li>
     * </p>
     * 
     * @param feature the feature for which we want to generate a description
     *            block
     * @param isTitle <i>true</i> if the block has to be rendered as a title,
     *            <i>false</i> otherwise
     * @return the title block
     * @throws UnauthorizedOperationException when some operation cannot be
     *             accessed
     */
    public static HtmlBranch generateFeatureTitle(final Feature feature, boolean isTitle) throws UnauthorizedOperationException {
        HtmlBranch master;
        if (isTitle) {
            master = new HtmlTitle(1);
        } else {
            master = new HtmlSpan("feature_complete_title");
        }
        master.setCssClass("feature_title");

        if (isTitle) {
            master.addText(getTitle(feature));
        } else {
            master.add(new FeaturePageUrl(feature).getHtmlLink(getTitle(feature)));
        }
        HtmlSpan softwareLink = SoftwaresTools.getSoftwareLink(feature.getSoftware());
        HtmlMixedText mixed = new HtmlMixedText(Context.tr(" (<0:software:>)"), softwareLink);
        master.add(mixed);
        return master;
    }

    public static HtmlDiv generateProgress(final Feature feature) throws UnauthorizedOperationException {
        return generateProgress(feature, false, BigDecimal.ZERO);
    }

    public static HtmlDiv generateProgress(final Feature feature, final boolean slim) throws UnauthorizedOperationException {
        return generateProgress(feature, slim, BigDecimal.ZERO);
    }

    /**
     * @throws UnauthorizedOperationException
     */
    public static HtmlDiv generateProgress(final Feature feature, final boolean slim, final BigDecimal futureAmount)
            throws UnauthorizedOperationException {
        final HtmlDiv featureSummaryProgress = new HtmlDiv("feature_summary_progress");
        {

            // Progress bar

            final float progressValue = (float) Math.floor(feature.getProgression());
            float myProgressValue = 0;
            float futureProgressValue = 0;

            if (Context.getSession().isLogged()) {
                myProgressValue = feature.getMemberProgression(Context.getSession().getAuthToken().getMember());
                if (myProgressValue > 0.0f && myProgressValue < 5f) {
                    myProgressValue = 5f;

                }
                myProgressValue = (float) Math.floor(myProgressValue);
            }

            if (!futureAmount.equals(BigDecimal.ZERO)) {
                futureProgressValue = feature.getRelativeProgression(futureAmount);
                if (futureProgressValue > 0.0f && futureProgressValue < 5f) {
                    futureProgressValue = 5f;

                }
                futureProgressValue = (float) Math.floor(futureProgressValue);

            }

            float cappedProgressValue = progressValue;
            if (cappedProgressValue + futureProgressValue > FeatureImplementation.PROGRESSION_PERCENT) {
                cappedProgressValue = FeatureImplementation.PROGRESSION_PERCENT - futureProgressValue;
            }

            final HtmlProgressBar progressBar = new HtmlProgressBar(slim,
                                                                    cappedProgressValue - myProgressValue,
                                                                    cappedProgressValue,
                                                                    cappedProgressValue + futureProgressValue);
            featureSummaryProgress.add(progressBar);

            // Progress text
            featureSummaryProgress.add(generateProgressText(feature, progressValue));

        }
        return featureSummaryProgress;
    }

    private static HtmlElement generateProgressText(final Feature feature, final float progressValue) {

        Offer currentOffer = null;
        try {
            currentOffer = feature.getSelectedOffer();
        } catch (final UnauthorizedOperationException e1) {
            Context.getSession().notifyError(Context.tr("An error prevented us from displaying selected offer. Please notify us."));
            throw new ShallNotPassException("User cannot access selected offer", e1);
        }
        if (currentOffer == null) {

            final HtmlSpan amount = new HtmlSpan();
            amount.setCssClass(IMPORTANT_CSS_CLASS);

            CurrencyLocale currency;
            try {
                currency = Context.getLocalizator().getCurrency(feature.getContribution());
                amount.addText(currency.getDefaultString());
            } catch (final UnauthorizedOperationException e) {
                Context.getSession().notifyError(Context.tr("An error prevented us from displaying contribution amount. Please notify us."));
                throw new ShallNotPassException("User cannot access contribution amount", e);
            }

            final HtmlParagraph progressText = new HtmlParagraph();
            progressText.setCssClass("progress_text");

            progressText.add(amount);
            progressText.addText(Context.tr(" no offer"));

            return progressText;
        }

        // Amount
        CurrencyLocale amountCurrency;
        try {
            amountCurrency = Context.getLocalizator().getCurrency(feature.getContribution());
            final HtmlSpan amount = new HtmlSpan();
            amount.setCssClass(IMPORTANT_CSS_CLASS);
            amount.addText(amountCurrency.getDefaultString());

            // Target
            final CurrencyLocale targetCurrency = Context.getLocalizator().getCurrency(currentOffer.getAmount());
            final HtmlSpan target = new HtmlSpan();
            target.setCssClass(IMPORTANT_CSS_CLASS);
            target.addText(targetCurrency.getDefaultString());

            // Progress
            final HtmlSpan progress = new HtmlSpan();
            progress.setCssClass(IMPORTANT_CSS_CLASS);
            final NumberFormat format = NumberFormat.getNumberInstance();
            format.setMinimumFractionDigits(0);
            progress.addText("" + format.format(progressValue) + " %");

            final HtmlParagraph progressText = new HtmlParagraph();
            progressText.setCssClass("progress_text");

            progressText.add(new HtmlMixedText(trn("<0:1600€:> i.e <1:69%:> of <2:2300€:> requested",
                                                   "<0:1600€:> i.e <1:69%:> of <2:2300€:> requested",
                                                   currentOffer.getAmount().intValue()), amount, progress, target));

            return progressText;
        } catch (final UnauthorizedOperationException e) {
            Context.getSession().notifyError(Context.tr("An error prevented us from displaying contribution amount. Please notify us."));
            throw new ShallNotPassException("User cannot access contibution amount", e);
        }
    }

    public static HtmlDiv generateDetails(final Feature feature, final boolean showBugs) throws UnauthorizedOperationException {
        final HtmlDiv featureSummaryDetails = new HtmlDiv("feature_summary_details");
        {

            final PageIterable<Comment> comments = feature.getComments();
            final Long commentsCount = feature.getCommentsCount();

            final int offersCount = feature.getOffers().size();

            final int contributionsCount = feature.getContributions().size();

            final FeaturePageUrl commentsFeatureUrl = new FeaturePageUrl(feature);
            commentsFeatureUrl.setAnchor("comments_block");

            final FeaturePageUrl offersFeatureUrl = new FeaturePageUrl(feature);
            offersFeatureUrl.getFeatureTabPaneUrl().setActiveTabKey(FeatureTabPane.OFFERS_TAB);
            offersFeatureUrl.setAnchor("feature_tab_pane");

            final FeaturePageUrl contributionsFeatureUrl = new FeaturePageUrl(feature);
            contributionsFeatureUrl.getFeatureTabPaneUrl().setActiveTabKey(FeatureTabPane.CONTRIBUTIONS_TAB);
            contributionsFeatureUrl.setAnchor("feature_tab_pane");

            featureSummaryDetails.add(commentsFeatureUrl.getHtmlLink(Context.trn("{0} comment",
                                                                                 "{0} comments",
                                                                                 commentsCount,
                                                                                 new Long(commentsCount))));
            featureSummaryDetails.addText(" – ");
            featureSummaryDetails.add(offersFeatureUrl.getHtmlLink(Context.trn("{0} offer", "{0} offers", offersCount, new Integer(offersCount))));
            featureSummaryDetails.addText(" – ");
            featureSummaryDetails.add(contributionsFeatureUrl.getHtmlLink(Context.trn("{0} contribution",
                                                                                      "{0} contributions",
                                                                                      contributionsCount,
                                                                                      new Integer(contributionsCount))));
            if (showBugs) {
                final int bugCount = feature.getOpenBugs().size();
                if (bugCount > 0) {

                    final FeaturePageUrl bugsFeatureUrl = new FeaturePageUrl(feature);
                    bugsFeatureUrl.getFeatureTabPaneUrl().setActiveTabKey(FeatureTabPane.BUGS_TAB);
                    bugsFeatureUrl.setAnchor("feature_tab_pane");

                    featureSummaryDetails.addText(" – ");
                    featureSummaryDetails.add(bugsFeatureUrl.getHtmlLink(Context.trn("{0} bug", "{0} bugs", bugCount, new Integer(bugCount))));
                }
            }

        }
        return featureSummaryDetails;
    }

    public static HtmlDiv generateState(final Feature feature) {
        // Progress state
        final HtmlDiv progressState = new HtmlDiv("feature_summary_state");
        {
            String imageName = "";
            String imageLabel = "";
            final String languageCode = Context.getLocalizator().getLanguageCode();
            switch (feature.getFeatureState()) {
                case FINISHED:
                    imageName = WebConfiguration.getImgFeatureStateSuccess(languageCode);
                    imageLabel = "success";
                    break;
                case DISCARDED:
                    imageName = WebConfiguration.getImgFeatureStateFailed(languageCode);
                    imageLabel = "failed";
                    break;
                case DEVELOPPING:
                    imageName = WebConfiguration.getImgFeatureStateDeveloping(languageCode);
                    imageLabel = "success";
                    break;
                case PENDING:
                case PREPARING:
                    imageName = WebConfiguration.getImgFeatureStateFunding(languageCode);
                    imageLabel = "success";
                    break;
            }

            progressState.add(new HtmlImage(new Image(imageName), imageLabel));
        }
        return progressState;
    }
}
