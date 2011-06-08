//
// Copyright (c) 2011 Linkeos.
//
// This file is part of Elveos.org.
// Elveos.org is free software: you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation, either version 3 of the License, or (at your
// option) any later version.
//
// Elveos.org is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
// more details.
// You should have received a copy of the GNU General Public License along
// with Elveos.org. If not, see http://www.gnu.org/licenses/.
//
package com.bloatit.web;

import com.bloatit.common.Log;
import com.bloatit.framework.utils.parameters.Parameters;
import com.bloatit.framework.webprocessor.WebProcessor;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.framework.webprocessor.context.Session;
import com.bloatit.framework.webprocessor.context.SessionManager;
import com.bloatit.framework.webprocessor.masters.Linkable;
import com.bloatit.framework.webprocessor.url.PageForbiddenUrl;
import com.bloatit.framework.webprocessor.url.PageNotFoundUrl;
import com.bloatit.web.actions.AddAttachementAction;
import com.bloatit.web.actions.AddAttachementPage;
import com.bloatit.web.actions.CreateCommentAction;
import com.bloatit.web.actions.PopularityVoteAction;
import com.bloatit.web.linkable.admin.AdminHomePage;
import com.bloatit.web.linkable.admin.AdministrationAction;
import com.bloatit.web.linkable.admin.ConfigurationAdminAction;
import com.bloatit.web.linkable.admin.ConfigurationAdminPage;
import com.bloatit.web.linkable.admin.DeclareHightlightedFeatureAction;
import com.bloatit.web.linkable.admin.FeatureAdminPage;
import com.bloatit.web.linkable.admin.HightlightedFeatureAdminPage;
import com.bloatit.web.linkable.admin.KudosableAdminPageImplementation;
import com.bloatit.web.linkable.admin.MilestoneAdminPage;
import com.bloatit.web.linkable.admin.UserContentAdminPageImplementation;
import com.bloatit.web.linkable.admin.exception.ExceptionAdministrationAction;
import com.bloatit.web.linkable.admin.exception.ExceptionAdministrationPage;
import com.bloatit.web.linkable.admin.notify.AdminGlobalNotificationAction;
import com.bloatit.web.linkable.admin.notify.AdminGlobalNotificationPage;
import com.bloatit.web.linkable.admin.withdraw.MoneyWithdrawalAdminAction;
import com.bloatit.web.linkable.admin.withdraw.MoneyWithdrawalAdminPage;
import com.bloatit.web.linkable.bugs.BugPage;
import com.bloatit.web.linkable.bugs.ModifyBugAction;
import com.bloatit.web.linkable.bugs.ModifyBugPage;
import com.bloatit.web.linkable.bugs.ReportBugAction;
import com.bloatit.web.linkable.bugs.ReportBugPage;
import com.bloatit.web.linkable.contribution.CheckContributionAction;
import com.bloatit.web.linkable.contribution.CheckContributionPage;
import com.bloatit.web.linkable.contribution.ContributePage;
import com.bloatit.web.linkable.contribution.ContributionAction;
import com.bloatit.web.linkable.contribution.ContributionProcess;
import com.bloatit.web.linkable.contribution.StaticCheckContributionPage;
import com.bloatit.web.linkable.contribution.UnlockContributionProcessAction;
import com.bloatit.web.linkable.errors.PageForbidden;
import com.bloatit.web.linkable.errors.PageNotFound;
import com.bloatit.web.linkable.features.CreateFeatureAction;
import com.bloatit.web.linkable.features.CreateFeaturePage;
import com.bloatit.web.linkable.features.FeatureListPage;
import com.bloatit.web.linkable.features.FeaturePage;
import com.bloatit.web.linkable.invoice.ContributionInvoicingInformationsPage;
import com.bloatit.web.linkable.invoice.ContributionInvoicingProcess;
import com.bloatit.web.linkable.invoice.InvoiceResource;
import com.bloatit.web.linkable.invoice.InvoicingContactPage;
import com.bloatit.web.linkable.invoice.ModifyInvoicingContactAction;
import com.bloatit.web.linkable.invoice.ModifyInvoicingContactProcess;
import com.bloatit.web.linkable.language.ChangeLanguageAction;
import com.bloatit.web.linkable.language.ChangeLanguagePage;
import com.bloatit.web.linkable.login.LoginAction;
import com.bloatit.web.linkable.login.LoginPage;
import com.bloatit.web.linkable.login.LogoutAction;
import com.bloatit.web.linkable.login.LostPasswordAction;
import com.bloatit.web.linkable.login.LostPasswordPage;
import com.bloatit.web.linkable.login.MemberActivationAction;
import com.bloatit.web.linkable.login.RecoverPasswordAction;
import com.bloatit.web.linkable.login.RecoverPasswordPage;
import com.bloatit.web.linkable.login.SignUpAction;
import com.bloatit.web.linkable.login.SignUpPage;
import com.bloatit.web.linkable.members.ChangeAvatarAction;
import com.bloatit.web.linkable.members.MemberPage;
import com.bloatit.web.linkable.members.MembersListPage;
import com.bloatit.web.linkable.members.ModifyMemberAction;
import com.bloatit.web.linkable.members.ModifyMemberPage;
import com.bloatit.web.linkable.meta.bugreport.MetaBugDeleteAction;
import com.bloatit.web.linkable.meta.bugreport.MetaBugEditPage;
import com.bloatit.web.linkable.meta.bugreport.MetaBugsListPage;
import com.bloatit.web.linkable.meta.bugreport.MetaEditBugAction;
import com.bloatit.web.linkable.meta.bugreport.MetaReportBugAction;
import com.bloatit.web.linkable.money.AccountChargingPage;
import com.bloatit.web.linkable.money.AccountChargingProcess;
import com.bloatit.web.linkable.money.CancelWithdrawMoneyAction;
import com.bloatit.web.linkable.money.PaylineAction;
import com.bloatit.web.linkable.money.PaylineNotifyAction;
import com.bloatit.web.linkable.money.PaylineProcess;
import com.bloatit.web.linkable.money.PaylineReturnAction;
import com.bloatit.web.linkable.money.StaticAccountChargingPage;
import com.bloatit.web.linkable.money.UnlockAccountChargingProcessAction;
import com.bloatit.web.linkable.money.WithdrawMoneyAction;
import com.bloatit.web.linkable.money.WithdrawMoneyPage;
import com.bloatit.web.linkable.offer.MakeOfferPage;
import com.bloatit.web.linkable.offer.OfferAction;
import com.bloatit.web.linkable.release.AddReleaseAction;
import com.bloatit.web.linkable.release.AddReleasePage;
import com.bloatit.web.linkable.release.ReleasePage;
import com.bloatit.web.linkable.softwares.AddSoftwareAction;
import com.bloatit.web.linkable.softwares.AddSoftwarePage;
import com.bloatit.web.linkable.softwares.SoftwareListPage;
import com.bloatit.web.linkable.softwares.SoftwarePage;
import com.bloatit.web.linkable.team.CreateTeamAction;
import com.bloatit.web.linkable.team.CreateTeamPage;
import com.bloatit.web.linkable.team.GiveRightAction;
import com.bloatit.web.linkable.team.HandleJoinTeamInvitationAction;
import com.bloatit.web.linkable.team.JoinTeamAction;
import com.bloatit.web.linkable.team.ModifyTeamAction;
import com.bloatit.web.linkable.team.ModifyTeamPage;
import com.bloatit.web.linkable.team.SendTeamInvitationAction;
import com.bloatit.web.linkable.team.SendTeamInvitationPage;
import com.bloatit.web.linkable.team.TeamPage;
import com.bloatit.web.linkable.team.TeamsPage;
import com.bloatit.web.pages.CommentReplyPage;
import com.bloatit.web.pages.DocumentationPage;
import com.bloatit.web.pages.IndexPage;
import com.bloatit.web.pages.SiteMapPage;
import com.bloatit.web.pages.TestPage;
import com.bloatit.web.url.*;

public class BloatitWebServer extends WebProcessor {

    public BloatitWebServer() {
        super();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Linkable constructLinkable(final String pageCode, final Parameters params, final Session session) {

        // Pages
        if (pageCode.equals(PageForbiddenUrl.getPageName())) {
            return new PageForbidden(new PageForbiddenUrl());
        }
        if (pageCode.equals(IndexPageUrl.getPageName())) {
            return new IndexPage(new IndexPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(LoginPageUrl.getPageName())) {
            return new LoginPage(new LoginPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(FeatureListPageUrl.getPageName())) {
            return new FeatureListPage(new FeatureListPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(CreateFeaturePageUrl.getPageName())) {
            return new CreateFeaturePage(new CreateFeaturePageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(FeaturePageUrl.getPageName())) {
            return new FeaturePage(new FeaturePageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(SiteMapPageUrl.getPageName())) {
            return new SiteMapPage(new SiteMapPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MembersListPageUrl.getPageName())) {
            return new MembersListPage(new MembersListPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MemberPageUrl.getPageName())) {
            return new MemberPage(new MemberPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ContributePageUrl.getPageName())) {
            return new ContributePage(new ContributePageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(CheckContributionPageUrl.getPageName())) {
            return new CheckContributionPage(new CheckContributionPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(StaticCheckContributionPageUrl.getPageName())) {
            return new StaticCheckContributionPage(new StaticCheckContributionPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MakeOfferPageUrl.getPageName())) {
            return new MakeOfferPage(new MakeOfferPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(TestPageUrl.getPageName())) {
            return new TestPage(new TestPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AccountChargingPageUrl.getPageName())) {
            return new AccountChargingPage(new AccountChargingPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(SignUpPageUrl.getPageName())) {
            return new SignUpPage(new SignUpPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(CommentReplyPageUrl.getPageName())) {
            return new CommentReplyPage(new CommentReplyPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(SoftwarePageUrl.getPageName())) {
            return new SoftwarePage(new SoftwarePageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AddSoftwarePageUrl.getPageName())) {
            return new AddSoftwarePage(new AddSoftwarePageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(SoftwareListPageUrl.getPageName())) {
            return new SoftwareListPage(new SoftwareListPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(UserContentAdminPageUrl.getPageName())) {
            return new UserContentAdminPageImplementation(new UserContentAdminPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(DocumentationPageUrl.getPageName())) {
            return new DocumentationPage(new DocumentationPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(TeamsPageUrl.getPageName())) {
            return new TeamsPage(new TeamsPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(TeamPageUrl.getPageName())) {
            return new TeamPage(new TeamPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(CreateTeamPageUrl.getPageName())) {
            return new CreateTeamPage(new CreateTeamPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(SendTeamInvitationPageUrl.getPageName())) {
            return new SendTeamInvitationPage(new SendTeamInvitationPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(KudosableAdminPageUrl.getPageName())) {
            return new KudosableAdminPageImplementation(new KudosableAdminPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(FeatureAdminPageUrl.getPageName())) {
            return new FeatureAdminPage(new FeatureAdminPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(BugPageUrl.getPageName())) {
            return new BugPage(new BugPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ReportBugPageUrl.getPageName())) {
            return new ReportBugPage(new ReportBugPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AddReleasePageUrl.getPageName())) {
            return new AddReleasePage(new AddReleasePageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ModifyBugPageUrl.getPageName())) {
            return new ModifyBugPage(new ModifyBugPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ReleasePageUrl.getPageName())) {
            return new ReleasePage(new ReleasePageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MilestoneAdminPageUrl.getPageName())) {
            return new MilestoneAdminPage(new MilestoneAdminPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MetaBugsListPageUrl.getPageName())) {
            return new MetaBugsListPage(new MetaBugsListPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MetaBugEditPageUrl.getPageName())) {
            return new MetaBugEditPage(new MetaBugEditPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ConfigurationAdminPageUrl.getPageName())) {
            return new ConfigurationAdminPage(new ConfigurationAdminPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AdminHomePageUrl.getPageName())) {
            return new AdminHomePage(new AdminHomePageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(HightlightedFeatureAdminPageUrl.getPageName())) {
            return new HightlightedFeatureAdminPage(new HightlightedFeatureAdminPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ChangeLanguagePageUrl.getPageName())) {
            return new ChangeLanguagePage(new ChangeLanguagePageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ModifyMemberPageUrl.getPageName())) {
            return new ModifyMemberPage(new ModifyMemberPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(LostPasswordPageUrl.getPageName())) {
            return new LostPasswordPage(new LostPasswordPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(RecoverPasswordPageUrl.getPageName())) {
            return new RecoverPasswordPage(new RecoverPasswordPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ModifyTeamPageUrl.getPageName())) {
            return new ModifyTeamPage(new ModifyTeamPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(WithdrawMoneyPageUrl.getPageName())) {
            return new WithdrawMoneyPage(new WithdrawMoneyPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MoneyWithdrawalAdminPageUrl.getPageName())) {
            return new MoneyWithdrawalAdminPage(new MoneyWithdrawalAdminPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(InvoicingContactPageUrl.getPageName())) {
            return new InvoicingContactPage(new InvoicingContactPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ExceptionAdministrationPageUrl.getPageName())) {
            return new ExceptionAdministrationPage(new ExceptionAdministrationPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ContributionInvoicingInformationsPageUrl.getPageName())) {
            return new ContributionInvoicingInformationsPage(new ContributionInvoicingInformationsPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AdminGlobalNotificationPageUrl.getPageName())) {
            return new AdminGlobalNotificationPage(new AdminGlobalNotificationPageUrl(params, session.getParameters()));
        }

        // ////////
        // Actions
        // ////////
        if (pageCode.equals(LoginActionUrl.getPageName())) {
            return new LoginAction(new LoginActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(LogoutActionUrl.getPageName())) {
            return new LogoutAction(new LogoutActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ContributionActionUrl.getPageName())) {
            return new ContributionAction(new ContributionActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(CheckContributionActionUrl.getPageName())) {
            return new CheckContributionAction(new CheckContributionActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(OfferActionUrl.getPageName())) {
            return new OfferAction(new OfferActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(CreateFeatureActionUrl.getPageName())) {
            return new CreateFeatureAction(new CreateFeatureActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(SignUpActionUrl.getPageName())) {
            return new SignUpAction(new SignUpActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(PopularityVoteActionUrl.getPageName())) {
            return new PopularityVoteAction(new PopularityVoteActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(CreateCommentActionUrl.getPageName())) {
            return new CreateCommentAction(new CreateCommentActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(PaylineActionUrl.getPageName())) {
            return new PaylineAction(new PaylineActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(PaylineNotifyActionUrl.getPageName())) {
            if (params.containsKey(PaylineNotifyAction.TOKEN_CODE)) {
                final String token = params.look(PaylineNotifyAction.TOKEN_CODE).getSimpleValue();
                final Session fakeSession = SessionManager.pickTemporarySession(token);
                if (fakeSession != null) {
                    Context.reInitializeContext(Context.getHeader(), fakeSession);
                }
            }
            return new PaylineNotifyAction(new PaylineNotifyActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AddSoftwareActionUrl.getPageName())) {
            return new AddSoftwareAction(new AddSoftwareActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MemberActivationActionUrl.getPageName())) {
            return new MemberActivationAction(new MemberActivationActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(PaylineReturnActionUrl.getPageName())) {
            return new PaylineReturnAction(new PaylineReturnActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AdministrationActionUrl.getPageName())) {
            return new AdministrationAction(new AdministrationActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(CreateTeamActionUrl.getPageName())) {
            return new CreateTeamAction(new CreateTeamActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(JoinTeamActionUrl.getPageName())) {
            return new JoinTeamAction(new JoinTeamActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(SendTeamInvitationActionUrl.getPageName())) {
            return new SendTeamInvitationAction(new SendTeamInvitationActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AddAttachementActionUrl.getPageName())) {
            return new AddAttachementAction(new AddAttachementActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AddAttachementPageUrl.getPageName())) {
            return new AddAttachementPage(new AddAttachementPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(HandleJoinTeamInvitationActionUrl.getPageName())) {
            return new HandleJoinTeamInvitationAction(new HandleJoinTeamInvitationActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ReportBugActionUrl.getPageName())) {
            return new ReportBugAction(new ReportBugActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AddReleaseActionUrl.getPageName())) {
            return new AddReleaseAction(new AddReleaseActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ModifyBugActionUrl.getPageName())) {
            return new ModifyBugAction(new ModifyBugActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ChangeAvatarActionUrl.getPageName())) {
            return new ChangeAvatarAction(new ChangeAvatarActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(GiveRightActionUrl.getPageName())) {
            return new GiveRightAction(new GiveRightActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ConfigurationAdminActionUrl.getPageName())) {
            return new ConfigurationAdminAction(new ConfigurationAdminActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MetaReportBugActionUrl.getPageName())) {
            return new MetaReportBugAction(new MetaReportBugActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MetaEditBugActionUrl.getPageName())) {
            return new MetaEditBugAction(new MetaEditBugActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MetaBugDeleteActionUrl.getPageName())) {
            return new MetaBugDeleteAction(new MetaBugDeleteActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ChangeLanguageActionUrl.getPageName())) {
            return new ChangeLanguageAction(new ChangeLanguageActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(UnlockAccountChargingProcessActionUrl.getPageName())) {
            return new UnlockAccountChargingProcessAction(new UnlockAccountChargingProcessActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(UnlockContributionProcessActionUrl.getPageName())) {
            return new UnlockContributionProcessAction(new UnlockContributionProcessActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(StaticAccountChargingPageUrl.getPageName())) {
            return new StaticAccountChargingPage(new StaticAccountChargingPageUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ModifyMemberActionUrl.getPageName())) {
            return new ModifyMemberAction(new ModifyMemberActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(LostPasswordActionUrl.getPageName())) {
            return new LostPasswordAction(new LostPasswordActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(RecoverPasswordActionUrl.getPageName())) {
            return new RecoverPasswordAction(new RecoverPasswordActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ModifyTeamActionUrl.getPageName())) {
            return new ModifyTeamAction(new ModifyTeamActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(DeclareHightlightedFeatureActionUrl.getPageName())) {
            return new DeclareHightlightedFeatureAction(new DeclareHightlightedFeatureActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(WithdrawMoneyActionUrl.getPageName())) {
            return new WithdrawMoneyAction(new WithdrawMoneyActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(MoneyWithdrawalAdminActionUrl.getPageName())) {
            return new MoneyWithdrawalAdminAction(new MoneyWithdrawalAdminActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(CancelWithdrawMoneyActionUrl.getPageName())) {
            return new CancelWithdrawMoneyAction(new CancelWithdrawMoneyActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ModifyInvoicingContactActionUrl.getPageName())) {
            return new ModifyInvoicingContactAction(new ModifyInvoicingContactActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ExceptionAdministrationActionUrl.getPageName())) {
            return new ExceptionAdministrationAction(new ExceptionAdministrationActionUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AdminGlobalNotificationActionUrl.getPageName())) {
            return new AdminGlobalNotificationAction(new AdminGlobalNotificationActionUrl(params, session.getParameters()));
        }

        // ////////
        // Process
        // ////////
        if (pageCode.equals(ContributionProcessUrl.getPageName())) {
            return new ContributionProcess(new ContributionProcessUrl(params, session.getParameters()));
        }
        if (pageCode.equals(AccountChargingProcessUrl.getPageName())) {
            return new AccountChargingProcess(new AccountChargingProcessUrl(params, session.getParameters()));
        }
        if (pageCode.equals(PaylineProcessUrl.getPageName())) {
            return new PaylineProcess(new PaylineProcessUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ModifyInvoicingContactProcessUrl.getPageName())) {
            return new ModifyInvoicingContactProcess(new ModifyInvoicingContactProcessUrl(params, session.getParameters()));
        }
        if (pageCode.equals(ContributionInvoicingProcessUrl.getPageName())) {
            return new ContributionInvoicingProcess(new ContributionInvoicingProcessUrl(params, session.getParameters()));
        }

        // Resource page
        if (pageCode.equals(FileResourceUrl.getPageName())) {
            return new FileResource(new FileResourceUrl(params, session.getParameters()));
        }
        if (pageCode.equals(InvoiceResourceUrl.getPageName())) {
            return new InvoiceResource(new InvoiceResourceUrl(params, session.getParameters()));
        }
        Log.web().warn("Failed to find the page code '" + pageCode + "' in the linkable list. Maybe you forgot to declare it in BloatitWebServer ?");
        return new PageNotFound(new PageNotFoundUrl());
    }

    @Override
    public boolean initialize() {
        WebConfiguration.load();
        return true;
    }

}
