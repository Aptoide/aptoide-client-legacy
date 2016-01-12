package com.aptoide.models;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 21-11-2013
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public class InstalledPackage {

    public String getSignature() {
        return signature;
    }

    private final String signature;

    public InstalledPackage(String name, String package_name, int version_code, String versionName, String signature) {

        this.name = name;
        this.package_name = package_name;
        this.version_code = version_code;
        this.version_name = versionName;
        this.signature = signature;
    }



    public String getName() {
        return name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public int getVersion_code() {
        return version_code;
    }

    public String getVersion_name() {
        return version_name;
    }



    private String package_name;
    private String name;

    private String version_name;
    private int version_code;

    @Override
    public boolean equals(Object o) {

        InstalledPackage packageToCompare = (InstalledPackage) o;

        return package_name.equals(packageToCompare.getPackage_name()) && version_code==packageToCompare.getVersion_code();
    }

    @Override
    public int hashCode() {
        return package_name.hashCode();
    }


}
