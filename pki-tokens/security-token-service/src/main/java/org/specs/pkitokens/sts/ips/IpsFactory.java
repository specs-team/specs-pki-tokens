package org.specs.pkitokens.sts.ips;

public class IpsFactory {
    private static Ips ips;

    public static void init() {
        ips = new Ips();
    }

    public static Ips getIps() {
        return ips;
    }
}
