package com.bloatit.data;


public class DaoExternalServiceTest extends DataTestUnit {

//    @Test
//    public final void testDaoExternalServiceDaoMemberStringStringEnumSetOfRightLevel() {
//        final EnumSet<RightLevel> set = EnumSet.of(RightLevel.CREATE_FEATURE, RightLevel.CONTRIBUTE, RightLevel.CREATE_OFFER);
//        final DaoExternalServiceMembership service = new DaoExternalServiceMembership(fred, "Test", "azertyuiop", set);
//
//        assertEquals(service.getLevels(), set);
//        assertEquals(service.getToken(), "azertyuiop");
//        assertEquals(service.isAuthorized(), false);
//    }
//
//    @Test
//    public final void testAuthorize() {
//        final EnumSet<RightLevel> set = EnumSet.of(RightLevel.CREATE_FEATURE, RightLevel.CONTRIBUTE, RightLevel.CREATE_OFFER);
//        final DaoExternalServiceMembership service = new DaoExternalServiceMembership(fred, "Test", "azertyuiop", set);
//        final Date tomorrow = DateUtils.tomorrow();
//        service.authorize("accesstoken", "refreshtoken", tomorrow);
//
//        assertEquals(service.isAuthorized(), true);
//
//        assertEquals(service.getToken(), "accesstoken");
//        assertEquals(service.getRefreshToken(), "refreshtoken");
//        assertEquals(service.getExpirationDate(), tomorrow);
//        assertEquals(service.getLevels(), set);
//    }
}
