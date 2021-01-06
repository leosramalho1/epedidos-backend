package br.com.inovasoft.epedidos.security.jwt;

import lombok.Getter;

@Getter
public class JwtRoles {

    public static final String USER_ADMIN = "user-admin";
    public static final String USER_BACKOFFICE = "user-backoffice";
    public static final String USER_APP_BUYER = "user-app-buyer";
    public static final String USER_APP_CUSTOMER = "user-app-customer";

}