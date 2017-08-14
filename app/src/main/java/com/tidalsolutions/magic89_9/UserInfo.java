package com.tidalsolutions.magic89_9;

/**
 * Created by Jeffrey on 12/11/2015.
 */
public class UserInfo {

    private static String user_id;
    private static Boolean isZero = false;
    private static String firstname, gender, birthday, image, mobile;
    private static Integer login_type;

    public static void SetUserID(String user_id) {
        UserInfo.user_id = user_id;
    }

    public static String getUserID () {
        return UserInfo.user_id;
    }

    public static void SetIsZero(Boolean isZero) {
        UserInfo.isZero = isZero;
    }

    public static Boolean GetIsZero() {
        return UserInfo.isZero;
    }

    public static void SetFirstname(String firstname) {
        UserInfo.firstname = firstname;
    }

    public static String GetFirstname () {
        return UserInfo.firstname;
    }

    public static void SetGender(String gender) {
        UserInfo.gender = gender;
    }

    public static String GetGender () {
        return UserInfo.gender;
    }

    public static void SetBirtdate(String birthday) {
        UserInfo.birthday = birthday;
    }

    public static String GetBirthday () {
        return UserInfo.birthday;
    }

    public static void SetImage(String image) {
        UserInfo.image = image;
    }

    public static String GetImage () {
        return UserInfo.image;
    }

    public static String GetMobile () {
        return UserInfo.mobile;
    }

    public static void SetMobile(String mobile) {
        UserInfo.mobile = mobile;
    }

    public static Integer GetLoginType () {
        return UserInfo.login_type;
    }

    public static Integer SetLoginType(Integer login_type) {
        return UserInfo.login_type = login_type;
    }
}
