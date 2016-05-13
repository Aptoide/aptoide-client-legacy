package com.aptoide.dataprovider.webservices.models.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsousa on 28/10/15.
 */
public class GetAppMeta {

    public Info info;
    public App data;

    public static class App {
        public Number id;
        public String name;
        @JsonProperty("package")
        public String packageName;
        public Number size;
        public String icon;
        public String graphic;
        public String added;
        public String modified;
        public Developer developer;
        public GetStoreMeta.Data store;
        public File file;
        public Media media;
        public Urls urls;
        public Stats stats;
        public Obb obb;
        public Pay pay;
    }


    public static class Developer {
        public String name;
        public String website;
        public String email;
        public String privacy;
    }

    public static class File {
        public String vername;
        public Number vercode;
        public String md5sum;
        public String path;
        @JsonProperty("path_alt")
        public String pathAlt;
        public Number filesize;
        public Signature signature;
        public Hardware hardware;
        public Malware malware;
        public Flags flags;
        @JsonProperty("used_features")
        public List<String> usedFeatures = new ArrayList<>();
        @JsonProperty("used_permissions")
        public List<String> usedPermissions = new ArrayList<>();

        public static class Signature {
            public String sha1;
            public String owner;
        }

        public static class Hardware {
            public Number sdk;
            public String screen;
            public Number gles;
            public List<String> cpus = new ArrayList<>();
            /**
             * Second array contains only two values:
             * First value is the screen, second value is the density
             */
            public List<List<Number>> densities = new ArrayList<>();
        }

        /**
         * List of various malware reasons
         *  http://ws2.aptoide.com/api/7/getApp/apk_md5sum/7de07d96488277d8d76eafa2ef66f5a8
         *
         *
         * RANK2:
         *  http://ws2.aptoide.com/api/7/getApp/apk_md5sum/7de07d96488277d8d76eafa2ef66f5a8
         *  http://ws2.aptoide.com/api/7/getApp/apk_md5sum/06c9eb56b787b6d3b606d68473a38f47
         *
         * RANK3:
         *  http://ws2.aptoide.com/api/7/getApp/apk_md5sum/18f0d5bdb9df1e0e27604890113c3331
         *  http://ws2.aptoide.com/api/7/getApp/apk_md5sum/74cbfde9dc6da43d3d14f4df9cdb9f2f
         *
         * Rank can be: TRUSTED, WARNING, UNKNOWN
         */
        public static class Malware {

            public static final String PASSED = "passed";
            public static final String WARN = "warn";

            public static final String TRUSTED = "TRUSTED";
            public static final String WARNING = "WARNING";
            public static final String UNKNOWN = "UNKNOWN";

            public static final String GOOGLE_PLAY = "Google Play";


            public String rank;
            public Reason reason;
            public String added;
            public String modified;

            public static class Reason {
                @JsonProperty("signature_validated")
                public SignatureValidated signatureValidated;
                @JsonProperty("thirdparty_validated")
                public ThirdPartyValidated thirdpartyValidated;
                @JsonProperty("manual")
                public ManualQA manualQA;
                public Scanned scanned;

                public static class SignatureValidated {
                    public String date;
                    /**
                     * possible value: "unknown", "failed", "passed"
                     */
                    public String status;
                    @JsonProperty("signature_from")
                    public String signatureFrom;
                }

                public static class ThirdPartyValidated {
                    public String date;
                    public String store;
                }

                public static class ManualQA {
                    public String date;
                    public String status;
                    public List<String> av;

                }

                public static class Scanned {

                    /**
                     * possible values: "passed", "warn"
                     */
                    public String status;
                    public String date;
                    @JsonProperty("av_info")
                    public List<AvInfo> avInfo;

                    public static class AvInfo {

                        public List<Infection> infections;
                        public String name;

                        public static class Infection {

                            public String name;
                            public String description;
                        }
                    }

                }
            }
        }

        public static class Flags {
            public static final String GOOD = "GOOD";

            public List<Vote> votes = new ArrayList<>();

            /**
             * When there's a review, there are no votes
             *
             *  flags: {
             *     review": "GOOD"
             *  },
             *
             */
            public String review;

            public static class Vote {
                public static final String FAKE = "FAKE";
                public static final String FREEZE = "FREEZE";
                public static final String GOOD = "GOOD";
                public static final String LICENSE = "LICENSE";
                public static final String VIRUS = "VIRUS";

                /**
                 * type can be:
                 *
                 *  FAKE, FREEZE, GOOD, LICENSE, VIRUS
                 */
                public String type;
                public Number count;
            }
        }
    }

    public static class Media {
        public List<String> keywords = new ArrayList<>();
        public String description;
        public String news;
        public List<Screenshot> screenshots = new ArrayList<>();
        public List<Video> videos = new ArrayList<>();

        public static class Video {
            public String type;
            public String url;
            public String thumbnail;
        }

        public static class Screenshot {
            public String url;
            public Number height;
            public Number width;

            public String getOrientation() {
                return height.intValue() > width.intValue() ? "portrait" : "landscape";
            }
        }
    }

    public static class Urls {
        public String w;
        public String m;
    }

    public static class Stats {
        public Rating rating;
        public Number downloads;
        public Number pdownloads;

        public static class Rating {
            public Number avg;
            public List<Vote> votes = new ArrayList<>();

            public static class Vote {
                public Number value;
                public Number count;
            }
        }
    }


    public static class Pay {

        public Number price;
        public String currency;
        public String symbol;
    }

    /**
     * Class containing the extra Obb file.
     *  http://ws2.aptoide.com/api/7/getApp/app_id/12966861
     */
    public static class Obb {

        public ObbItem patch;
        public ObbItem main;

        public static class ObbItem {

            public String path;
            public String md5sum;
            public Number filesize;
            public String filename;
        }
    }

}
