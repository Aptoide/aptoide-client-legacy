package com.aptoide.dataprovider.webservices.models.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsousa on 15/09/15.
 */
    public class GetStoreWidgets {

    public Info info;
    public Datalist datalist;

    public static class Datalist {

        public Number total;
        public Number count;
        public Number next;
        public Number offset;
        public Number limit;
        public Number hidden;
        @JsonProperty("list")
        public List<WidgetList> widgetList = new ArrayList<>();

        public static class WidgetList {

            /** Constants for values of type */
            public static final String ADS_TYPE = "ADS";
            public static final String APPS_GROUP_TYPE = "APPS_GROUP";
            public static final String CATEGORIES_TYPE = "DISPLAYS";
            public static final String TIMELINE_TYPE = "TIMELINE";
            public static final String REVIEWS_TYPE = "REVIEWS";
            public static final String COMMENTS_TYPE = "COMMENTS";
            public static final String STORE_GROUP = "STORES_GROUP";


            public String type;
            public String tag;
            public String title; // Highlighted, Games, Categories, Timeline, Recommended for you, Aptoide Publishers
            @JsonProperty("view")
            public ListViewItems listApps;
            public List<Action> actions = new ArrayList<>();
            public Data data;

            public static class Data {

                public String layout; // GRID, LIST, BRICK
                public String icon;
                public List<Categories> categories = new ArrayList<>(); //only present if type": "DISPLAYS"

                public static class Categories {
                    public Number id;
                    public String ref_id;
                    public String parent_id;
                    public String parent_ref_id;
                    public String name;
                    public String graphic;
                    public String icon;
                    public Number ads_count;
                }
            }

            public static class Action {
                public String type; // button
                public String label;
                public String tag;
                public Event event;

                public static class Event {
                    //response test

                    // TODO: 3/28/16 Fabio delete



//                    {
//                        "info": {
//                        "status": "OK",
//                                "time": {
//                            "seconds": 0.5692310333252,
//                                    "human": "569 milliseconds"
//                        }
//                    },
//                        "nodes": {
//                        "meta": {
//                            "info": {
//                                "status": "OK",
//                                        "time": {
//                                    "seconds": 0.017822980880737,
//                                            "human": "17 milliseconds"
//                                }
//                            },
//                            "data": {
//                                "id": 15,
//                                        "name": "apps",
//                                        "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg",
//                                        "appearance": {
//                                    "theme": "default",
//                                            "description": "Aptoide Official App Store"
//                                },
//                                "stats": {
//                                    "apps": 136622,
//                                            "subscribers": 121376,
//                                            "downloads": 911900145
//                                }
//                            }
//                        },
//                        "tabs": {
//                            "info": {
//                                "status": "OK",
//                                        "time": {
//                                    "seconds": 0.025887966156006,
//                                            "human": "25 milliseconds"
//                                }
//                            },
//                            "list": [
//                            {
//                                "label": "Home",
//                                    "tag": "home",
//                                    "event": {
//                                "type": "API",
//                                        "name": "getStore",
//                                        "action": "http://ws2.aptoide.com/api/7/getStore/store_id/15/context/store"
//                            }
//                            },
//                            {
//                                "label": "Apps for Kids",
//                                    "tag": "apps-for-kids",
//                                    "event": {
//                                "type": "API",
//                                        "name": "getStoreWidgets",
//                                        "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/store/widget/apps_list%3Aucat_3194"
//                            }
//                            },
//                            {
//                                "label": "Aptoide Publishers",
//                                    "tag": "aptoide-publishers",
//                                    "event": {
//                                "type": "API",
//                                        "name": "getStoreWidgets",
//                                        "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/store/widget/apps_list%3Aucat_3239"
//                            }
//                            },
//                            {
//                                "label": "Music & Video",
//                                    "tag": "music-video",
//                                    "event": {
//                                "type": "API",
//                                        "name": "getStoreWidgets",
//                                        "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/store/widget/apps_list%3Aucat_4458"
//                            }
//                            },
//                            {
//                                "label": "Applications",
//                                    "tag": "applications",
//                                    "event": {
//                                "type": "API",
//                                        "name": "getStoreWidgets",
//                                        "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/store/widget/apps_list%3Acat_1"
//                            }
//                            },
//                            {
//                                "label": "Games",
//                                    "tag": "games",
//                                    "event": {
//                                "type": "API",
//                                        "name": "getStoreWidgets",
//                                        "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/store/widget/apps_list%3Acat_2"
//                            }
//                            },
//                            {
//                                "label": "Summer Apps",
//                                    "tag": "summer-apps",
//                                    "event": {
//                                "type": "API",
//                                        "name": "getStoreWidgets",
//                                        "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/store/widget/apps_list%3Aucat_4087"
//                            }
//                            },
//                            {
//                                "label": "Essential Apps",
//                                    "tag": "essential-apps",
//                                    "event": {
//                                "type": "API",
//                                        "name": "getStoreWidgets",
//                                        "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/store/widget/apps_list%3Aucat_3183"
//                            }
//                            },
//                            {
//                                "label": "Play-it!",
//                                    "tag": "play-it",
//                                    "event": {
//                                "type": "API",
//                                        "name": "getStoreWidgets",
//                                        "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/store/widget/apps_list%3Aucat_4086"
//                            }
//                            },
//                            {
//                                "label": "Latest Comments",
//                                    "tag": "latest-comments",
//                                    "event": {
//                                "type": "v3",
//                                        "name": "getApkComments",
//                                        "action": null
//                            }
//                            },
//                            {
//                                "label": "Latest Reviews",
//                                    "tag": "latest-reviews",
//                                    "event": {
//                                "type": "v3",
//                                        "name": "getReviews",
//                                        "action": null
//                            }
//                            }
//                            ]
//                        },
//                        "widgets": {
//                            "info": {
//                                "status": "OK",
//                                        "time": {
//                                    "seconds": 0.51268100738525,
//                                            "human": "512 milliseconds"
//                                }
//                            },
//                            "datalist": {
//                                "total": 13,
//                                        "count": 13,
//                                        "next": 13,
//                                        "offset": 0,
//                                        "limit": 13,
//                                        "hidden": 0,
//                                        "list": [
//                                {
//                                    "type": "APPS_GROUP",
//                                        "title": "Editors Choice",
//                                        "tag": "apps-group-editors-choice",
//                                        "view": {
//                                    "info": {
//                                        "status": "OK",
//                                                "time": {
//                                            "seconds": 0.10219812393188,
//                                                    "human": "102 milliseconds"
//                                        }
//                                    },
//                                    "datalist": {
//                                        "total": 21,
//                                                "count": 5,
//                                                "next": 6,
//                                                "offset": 0,
//                                                "limit": 5,
//                                                "hidden": 0,
//                                                "list": [
//                                        {
//                                            "id": 18339057,
//                                                "name": "Thumb Drift - Furious Racing",
//                                                "package": "com.smgstudio.thumbdrift",
//                                                "size": 69677647,
//                                                "icon": "http://pool.img.aptoide.com/apps/31a10043cf5326aa342662bd23852e74_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/apps/070de9bf9b94539c1c63769e378ef85e_fgraphic_705x345.jpg",
//                                                "added": "2016-03-25 11:17:28",
//                                                "modified": "2016-03-25 11:17:28",
//                                                "updated": "2016-03-25 11:17:29",
//                                                "uptype": "aptuploader",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.1.1.206",
//                                                    "vercode": 206,
//                                                    "md5sum": "76e3c664e0529c3483dbb3b801cce2c9"
//                                        },
//                                            "stats": {
//                                            "downloads": 69646,
//                                                    "rating": {
//                                                "avg": 4.1,
//                                                        "total": 78,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 52
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 8
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 2
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 6
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 10
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 17859347,
//                                                "name": "Blocky Rugby",
//                                                "package": "com.fullfat.android.blockyrugby",
//                                                "size": 35307294,
//                                                "icon": "http://pool.img.aptoide.com/apps/16f55e0124f8cefab00c70c886019226_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/apps/64b389ec940548520eb4dc73ecca3020_fgraphic_705x345.jpg",
//                                                "added": "2016-02-21 03:27:24",
//                                                "modified": "2016-03-11 20:23:09",
//                                                "updated": "2016-03-11 20:23:09",
//                                                "uptype": "aptuploader",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.1.31",
//                                                    "vercode": 3,
//                                                    "md5sum": "cae2bd582ec66bac45be869eaacf52dc"
//                                        },
//                                            "stats": {
//                                            "downloads": 17482,
//                                                    "rating": {
//                                                "avg": 4.15,
//                                                        "total": 20,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 10
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 5
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 3
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 2
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 0
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18308309,
//                                                "name": "MSQRD",
//                                                "package": "me.msqrd.android",
//                                                "size": 35588752,
//                                                "icon": "http://pool.img.aptoide.com/apps/70f9f19eebc6964c5ee0f4c57202a61f_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/apps/ef99d364653be49db2f46157f7db1f5b_fgraphic_705x345.jpg",
//                                                "added": "2016-03-23 09:11:49",
//                                                "modified": "2016-03-23 09:11:49",
//                                                "updated": "2016-03-23 09:11:49",
//                                                "uptype": "aptbackup",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.2.0",
//                                                    "vercode": 20,
//                                                    "md5sum": "e5aa63029b6ef1a7c0cb7967f4478d2f"
//                                        },
//                                            "stats": {
//                                            "downloads": 87251,
//                                                    "rating": {
//                                                "avg": 4.34,
//                                                        "total": 176,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 129
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 17
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 8
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 5
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 17
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18106892,
//                                                "name": "World of Tanks Blitz",
//                                                "package": "net.wargaming.wot.blitz",
//                                                "size": 54353617,
//                                                "icon": "http://pool.img.aptoide.com/apps/21d97eb1c044693953387439e58a3f90_icon.jpg",
//                                                "graphic": "http://pool.img.aptoide.com/apps/4c466e36c4eac71df07c82538c579514_fgraphic_705x345.jpg",
//                                                "added": "2016-03-09 23:12:06",
//                                                "modified": "2016-03-09 23:12:06",
//                                                "updated": "2016-03-09 23:12:06",
//                                                "uptype": "regular",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "2.7.0.344",
//                                                    "vercode": 20700344,
//                                                    "md5sum": "4908c39c7ad19cd11d39805327d3eace"
//                                        },
//                                            "stats": {
//                                            "downloads": 272474,
//                                                    "rating": {
//                                                "avg": 4.6,
//                                                        "total": 169,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 135
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 17
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 6
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 6
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 5
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18327501,
//                                                "name": "Clash of Clans",
//                                                "package": "com.supercell.clashofclans",
//                                                "size": 61681551,
//                                                "icon": "http://pool.img.aptoide.com/apps/aae8e02f62bf4a4008769ddb14b8fd89_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/apps/51dd3885ff94fa02bb0a87b53abafd92_fgraphic_705x345.jpg",
//                                                "added": "2016-03-24 14:56:38",
//                                                "modified": "2016-03-24 14:56:38",
//                                                "updated": "2016-03-24 14:56:38",
//                                                "uptype": "aptuploader",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "8.212.9",
//                                                    "vercode": 753,
//                                                    "md5sum": "8b7833535bdec60266bbb5ec7ffb06d7"
//                                        },
//                                            "stats": {
//                                            "downloads": 6767396,
//                                                    "rating": {
//                                                "avg": 4.65,
//                                                        "total": 1577,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 1327
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 100
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 53
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 35
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 62
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        }
//                                        ]
//                                    }
//                                },
//                                    "actions": [
//                                    {
//                                        "type": "button",
//                                            "label": "More Editor's Choice",
//                                            "tag": "apps-group-editors-choice-more-editor-s-choice",
//                                            "event": {
//                                        "type": "API",
//                                                "name": "listApps",
//                                                "action": "http://ws2.aptoide.com/api/7/listApps/store_id/15/group/editors/subgroups/%5B%22highlighted%22%5D/order/rand/sort/latest"
//                                    }
//                                    }
//                                    ],
//                                    "data": {
//                                    "layout": "BRICK"
//                                }
//                                },
//                                {
//                                    "type": "ADS",
//                                        "title": "Highlighted",
//                                        "tag": "ads-highlighted",
//                                        "view": null,
//                                        "actions": [],
//                                    "data": null
//                                },
//                                {
//                                    "type": "APPS_GROUP",
//                                        "title": "Trending",
//                                        "tag": "apps-group-trending",
//                                        "view": {
//                                    "info": {
//                                        "status": "OK",
//                                                "time": {
//                                            "seconds": 0.06988787651062,
//                                                    "human": "69 milliseconds"
//                                        }
//                                    },
//                                    "datalist": {
//                                        "total": 9061,
//                                                "count": 6,
//                                                "next": 14,
//                                                "offset": 0,
//                                                "limit": 6,
//                                                "hidden": 0,
//                                                "list": [
//                                        {
//                                            "id": 18013590,
//                                                "name": "Clash Royale",
//                                                "package": "com.supercell.clashroyale",
//                                                "size": 91282556,
//                                                "icon": "http://pool.img.aptoide.com/apps/fc255df60a352a0e1c5fa506db267424_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/apps/045fc5e608ec9900d074ff2ee33fa3d0_fgraphic_705x345.jpg",
//                                                "added": "2016-03-02 13:44:26",
//                                                "modified": "2016-03-02 13:44:26",
//                                                "updated": "2016-03-02 13:44:26",
//                                                "uptype": "aptbackup",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.2.3",
//                                                    "vercode": 28,
//                                                    "md5sum": "9254f3fda85b582828c705156aaf704c"
//                                        },
//                                            "stats": {
//                                            "downloads": 1008228,
//                                                    "rating": {
//                                                "avg": 4.61,
//                                                        "total": 2037,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 1692
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 136
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 65
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 38
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 106
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 15880785,
//                                                "name": "High School Driving Test",
//                                                "package": "com.games2win.highschooldt.aptoide",
//                                                "size": 37162014,
//                                                "icon": "http://pool.img.aptoide.com/apps/3ad6f52f6098c54efa16f39d940fa9c4.png",
//                                                "graphic": "http://pool.img.aptoide.com/apps/727a81139604554115d65127c7d8ce9a_fgraphic_705x345.jpg",
//                                                "added": "2016-01-05 09:27:45",
//                                                "modified": "2016-01-05 12:32:44",
//                                                "updated": "2016-01-05 12:32:45",
//                                                "uptype": "regular",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.0",
//                                                    "vercode": 1,
//                                                    "md5sum": "a32ba5cea78d9e873833d5b449cef9e8"
//                                        },
//                                            "stats": {
//                                            "downloads": 99859,
//                                                    "rating": {
//                                                "avg": 4.09,
//                                                        "total": 437,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 281
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 45
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 32
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 26
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 53
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18308309,
//                                                "name": "MSQRD",
//                                                "package": "me.msqrd.android",
//                                                "size": 35588752,
//                                                "icon": "http://pool.img.aptoide.com/apps/70f9f19eebc6964c5ee0f4c57202a61f_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/apps/ef99d364653be49db2f46157f7db1f5b_fgraphic_705x345.jpg",
//                                                "added": "2016-03-23 09:11:49",
//                                                "modified": "2016-03-23 09:11:49",
//                                                "updated": "2016-03-23 09:11:49",
//                                                "uptype": "aptbackup",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.2.0",
//                                                    "vercode": 20,
//                                                    "md5sum": "e5aa63029b6ef1a7c0cb7967f4478d2f"
//                                        },
//                                            "stats": {
//                                            "downloads": 87581,
//                                                    "rating": {
//                                                "avg": 4.34,
//                                                        "total": 176,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 129
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 17
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 8
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 5
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 17
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18339057,
//                                                "name": "Thumb Drift - Furious Racing",
//                                                "package": "com.smgstudio.thumbdrift",
//                                                "size": 69677647,
//                                                "icon": "http://pool.img.aptoide.com/apps/31a10043cf5326aa342662bd23852e74_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/apps/070de9bf9b94539c1c63769e378ef85e_fgraphic_705x345.jpg",
//                                                "added": "2016-03-25 11:17:28",
//                                                "modified": "2016-03-25 11:17:28",
//                                                "updated": "2016-03-25 11:17:29",
//                                                "uptype": "aptuploader",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.1.1.206",
//                                                    "vercode": 206,
//                                                    "md5sum": "76e3c664e0529c3483dbb3b801cce2c9"
//                                        },
//                                            "stats": {
//                                            "downloads": 70415,
//                                                    "rating": {
//                                                "avg": 4.1,
//                                                        "total": 78,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 52
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 8
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 2
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 6
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 10
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 17625460,
//                                                "name": "MARVEL Avengers Academy TM",
//                                                "package": "com.tinyco.avengers.beta",
//                                                "size": 90454755,
//                                                "icon": "http://pool.img.aptoide.com/apps/fbbc999550934a6f3fd9d925284680fa_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/apps/a4dd2090d9efb83365d47f34b7880995_fgraphic_705x345.jpg",
//                                                "added": "2016-02-05 05:25:37",
//                                                "modified": "2016-02-05 14:41:18",
//                                                "updated": "2016-02-05 14:41:19",
//                                                "uptype": "dropbox",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "0.2.3",
//                                                    "vercode": 69,
//                                                    "md5sum": "0b7b36c968165cc570bb151bea5400d8"
//                                        },
//                                            "stats": {
//                                            "downloads": 68522,
//                                                    "rating": {
//                                                "avg": 4.26,
//                                                        "total": 281,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 196
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 26
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 20
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 13
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 26
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18288126,
//                                                "name": "Punch Free Club",
//                                                "package": "com.tinybuildgames.punchclub_nyear16",
//                                                "size": 76097507,
//                                                "icon": "http://pool.img.aptoide.com/apps/65d435a342161faf9539d4d42ab7cbb5_icon.png",
//                                                "graphic": null,
//                                                "added": "2016-03-22 00:13:38",
//                                                "modified": "2016-03-22 00:13:38",
//                                                "updated": "2016-03-22 00:13:38",
//                                                "uptype": "aptbackup",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.06",
//                                                    "vercode": 6,
//                                                    "md5sum": "0fcaf1b57d08aa207baf84ee188002ae"
//                                        },
//                                            "stats": {
//                                            "downloads": 46853,
//                                                    "rating": {
//                                                "avg": 3.51,
//                                                        "total": 140,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 72
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 11
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 10
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 11
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 36
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        }
//                                        ]
//                                    }
//                                },
//                                    "actions": [
//                                    {
//                                        "type": "button",
//                                            "label": "More",
//                                            "tag": "apps-group-trending-more",
//                                            "event": {
//                                        "type": "API",
//                                                "name": "getStoreWidgets",
//                                                "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/home/widget/apps_list%3Acat_0%3Atrending30d"
//                                    }
//                                    }
//                                    ],
//                                    "data": {
//                                    "layout": "GRID"
//                                }
//                                },
//                                {
//                                    "type": "APPS_GROUP",
//                                        "title": "Local Top Apps",
//                                        "tag": "apps-group-local-top-apps",
//                                        "view": {
//                                    "info": {
//                                        "status": "OK",
//                                                "time": {
//                                            "seconds": 0.10802507400513,
//                                                    "human": "108 milliseconds"
//                                        }
//                                    },
//                                    "datalist": {
//                                        "total": 2235207,
//                                                "count": 6,
//                                                "next": 8,
//                                                "offset": 0,
//                                                "limit": 6,
//                                                "hidden": 0,
//                                                "list": [
//                                        {
//                                            "id": 18327500,
//                                                "name": "Clash of Clans",
//                                                "package": "com.supercell.clashofclans",
//                                                "size": 61681551,
//                                                "icon": "http://pool.img.aptoide.com/burno/aae8e02f62bf4a4008769ddb14b8fd89_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/burno/51dd3885ff94fa02bb0a87b53abafd92_fgraphic_705x345.jpg",
//                                                "added": "2016-03-24 14:56:38",
//                                                "modified": "2016-03-24 14:56:38",
//                                                "updated": "2016-03-28 08:58:22",
//                                                "uptype": "aptuploader",
//                                                "store": {
//                                            "id": 1147203,
//                                                    "name": "burno",
//                                                    "avatar": "http://pool.img.aptoide.com/burno/3bf5adf05843f9f28c486d5ddef8f873_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "8.212.9",
//                                                    "vercode": 753,
//                                                    "md5sum": "8b7833535bdec60266bbb5ec7ffb06d7"
//                                        },
//                                            "stats": {
//                                            "downloads": 13603089,
//                                                    "rating": {
//                                                "avg": 4.63,
//                                                        "total": 269,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 221
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 22
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 10
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 6
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 10
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 12880477,
//                                                "name": "Minecraft - Pocket Edition 0.1.1j alpha",
//                                                "package": "com.mojangcanada.minecraftpe",
//                                                "size": 19392023,
//                                                "icon": "http://pool.img.aptoide.com/mojangpe/e5a58b6d90190d99abd3af119b3ec1eb_icon.png",
//                                                "graphic": null,
//                                                "added": "2015-11-10 11:19:42",
//                                                "modified": "2015-11-10 11:19:42",
//                                                "updated": "2016-03-26 22:47:24",
//                                                "uptype": "regular",
//                                                "store": {
//                                            "id": 993535,
//                                                    "name": "mojangpe",
//                                                    "avatar": "http://pool.img.aptoide.com/mojangpe/3bf5adf05843f9f28c486d5ddef8f873_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "0.14.1",
//                                                    "vercode": 1013,
//                                                    "md5sum": "59bd23f0cbc7af49fab26b9e0221c0d0"
//                                        },
//                                            "stats": {
//                                            "downloads": 1280982,
//                                                    "rating": {
//                                                "avg": 3.94,
//                                                        "total": 1204,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 773
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 88
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 60
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 65
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 218
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18345523,
//                                                "name": "WhatsApp",
//                                                "package": "com.whatsapp",
//                                                "size": 28725065,
//                                                "icon": "http://pool.img.aptoide.com/milaupv/461638042f6303c2860627f842116ccd_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/milaupv/95b807a4fde1e56cf2984db2b9291a53.png",
//                                                "added": "2016-03-25 20:49:47",
//                                                "modified": "2016-03-25 20:49:47",
//                                                "updated": "2016-03-28 08:02:14",
//                                                "uptype": "aptbackup",
//                                                "store": {
//                                            "id": 354763,
//                                                    "name": "milaupv",
//                                                    "avatar": "http://pool.img.aptoide.com/milaupv/ffa8bf1178dd61576f099644622b8bed_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "2.12.559",
//                                                    "vercode": 451060,
//                                                    "md5sum": "397c057e58806042233d9b4f69d4543a"
//                                        },
//                                            "stats": {
//                                            "downloads": 63839934,
//                                                    "rating": {
//                                                "avg": 4.39,
//                                                        "total": 613,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 446
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 67
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 34
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 25
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 41
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18013589,
//                                                "name": "Clash Royale",
//                                                "package": "com.supercell.clashroyale",
//                                                "size": 91282556,
//                                                "icon": "http://pool.img.aptoide.com/pocketappz/fc255df60a352a0e1c5fa506db267424_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/pocketappz/045fc5e608ec9900d074ff2ee33fa3d0_fgraphic_705x345.jpg",
//                                                "added": "2016-03-02 13:44:26",
//                                                "modified": "2016-03-02 13:44:26",
//                                                "updated": "2016-03-28 06:33:45",
//                                                "uptype": "aptbackup",
//                                                "store": {
//                                            "id": 925184,
//                                                    "name": "pocketappz",
//                                                    "avatar": "http://pool.img.aptoide.com/pocketappz/cbb3743b160875217e6ddfa6a927a464_ravatar.png"
//                                        },
//                                            "file": {
//                                            "vername": "1.2.3",
//                                                    "vercode": 28,
//                                                    "md5sum": "9254f3fda85b582828c705156aaf704c"
//                                        },
//                                            "stats": {
//                                            "downloads": 2001134,
//                                                    "rating": {
//                                                "avg": 4.64,
//                                                        "total": 567,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 478
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 39
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 14
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 8
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 28
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18384249,
//                                                "name": "Facebook",
//                                                "package": "com.facebook.katana",
//                                                "size": 40427146,
//                                                "icon": "http://pool.img.aptoide.com/sf49ers/c2d8d5b9d6504d248dc5ddb5318159a2_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/sf49ers/768382b4d34642c12f71f5bbc517ba64_fgraphic_705x345.jpg",
//                                                "added": "2016-03-27 18:04:29",
//                                                "modified": "2016-03-27 18:04:29",
//                                                "updated": "2016-03-28 07:11:15",
//                                                "uptype": "aptuploader",
//                                                "store": {
//                                            "id": 679536,
//                                                    "name": "sf49ers",
//                                                    "avatar": "http://pool.img.aptoide.com/sf49ers/3bf5adf05843f9f28c486d5ddef8f873_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "71.0.0.0.68",
//                                                    "vercode": 26328115,
//                                                    "md5sum": "2c7c830b37dbfe17376618e1c4d87fc6"
//                                        },
//                                            "stats": {
//                                            "downloads": 45631209,
//                                                    "rating": {
//                                                "avg": 3.77,
//                                                        "total": 13,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 8
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 1
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 0
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 1
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 3
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 8047524,
//                                                "name": "GTA: SA",
//                                                "package": "com.rockstargames.gtasa",
//                                                "size": 2617814002,
//                                                "icon": "http://pool.img.aptoide.com/androidiyop/9f71e5aa68c2297a39c6966fa6dc80d7_icon.png",
//                                                "graphic": null,
//                                                "added": "2014-12-25 12:01:42",
//                                                "modified": "2014-12-25 12:01:42",
//                                                "updated": "2016-03-27 21:14:41",
//                                                "uptype": "aptuploader",
//                                                "store": {
//                                            "id": 762581,
//                                                    "name": "androidiyop",
//                                                    "avatar": "http://pool.img.aptoide.com/androidiyop/2ae764d3b4a8021d0e1d42e18c908623_ravatar.png"
//                                        },
//                                            "file": {
//                                            "vername": "1.07",
//                                                    "vercode": 11,
//                                                    "md5sum": "7a202c9ac2c604fbb1d69175ad710315"
//                                        },
//                                            "stats": {
//                                            "downloads": 3324463,
//                                                    "rating": {
//                                                "avg": 4.35,
//                                                        "total": 3045,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 2334
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 174
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 116
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 116
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 305
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        }
//                                        ]
//                                    }
//                                },
//                                    "actions": [
//                                    {
//                                        "type": "button",
//                                            "label": "More",
//                                            "tag": "apps-group-local-top-apps-more",
//                                            "event": {
//                                        "type": "API",
//                                                "name": "listApps",
//                                                "action": "http://ws2.aptoide.com/api/7/listApps/group/local"
//                                    }
//                                    }
//                                    ],
//                                    "data": {
//                                    "layout": "GRID"
//                                }
//                                },
//                                {
//                                    "type": "TIMELINE",
//                                        "title": "Your friends' installs",
//                                        "tag": "timeline-your-friends-installs",
//                                        "view": null,
//                                        "actions": [],
//                                    "data": null
//                                },
//                                {
//                                    "type": "DISPLAYS",
//                                        "title": "Displays",
//                                        "tag": "displays-displays",
//                                        "view": {
//                                    "info": {
//                                        "status": "OK",
//                                                "time": {
//                                            "seconds": 0.0092380046844482,
//                                                    "human": "9 milliseconds"
//                                        }
//                                    },
//                                    "list": [
//                                    {
//                                        "label": "Essential Apps",
//                                            "tag": "essential-apps",
//                                            "graphic": "http://pool.img.aptoide.com/apps/b24b2a03b9a34587d53b276e7387c6ac_cat_graphic.png",
//                                            "event": {
//                                        "type": "API",
//                                                "name": "getStoreWidgets",
//                                                "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/home/widget/apps_list%3Aucat_3183"
//                                    }
//                                    },
//                                    {
//                                        "label": "Play-it!",
//                                            "tag": "play-it",
//                                            "graphic": "http://pool.img.aptoide.com/apps/084075a6fb11786e7ab247f4b782bee1_cat_graphic.png",
//                                            "event": {
//                                        "type": "API",
//                                                "name": "getStoreWidgets",
//                                                "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/home/widget/apps_list%3Aucat_4086"
//                                    }
//                                    }
//                                    ]
//                                },
//                                    "actions": [],
//                                    "data": null
//                                },
//                                {
//                                    "type": "APPS_GROUP",
//                                        "title": "Top Games",
//                                        "tag": "apps-group-top-games",
//                                        "view": {
//                                    "info": {
//                                        "status": "OK",
//                                                "time": {
//                                            "seconds": 0.12337493896484,
//                                                    "human": "123 milliseconds"
//                                        }
//                                    },
//                                    "datalist": {
//                                        "total": 897473,
//                                                "count": 6,
//                                                "next": 7,
//                                                "offset": 0,
//                                                "limit": 6,
//                                                "hidden": 0,
//                                                "list": [
//                                        {
//                                            "id": 18327500,
//                                                "name": "Clash of Clans",
//                                                "package": "com.supercell.clashofclans",
//                                                "size": 61681551,
//                                                "icon": "http://pool.img.aptoide.com/burno/aae8e02f62bf4a4008769ddb14b8fd89_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/burno/51dd3885ff94fa02bb0a87b53abafd92_fgraphic_705x345.jpg",
//                                                "added": "2016-03-24 14:56:38",
//                                                "modified": "2016-03-24 14:56:38",
//                                                "updated": "2016-03-28 08:58:22",
//                                                "uptype": "aptuploader",
//                                                "store": {
//                                            "id": 1147203,
//                                                    "name": "burno",
//                                                    "avatar": "http://pool.img.aptoide.com/burno/3bf5adf05843f9f28c486d5ddef8f873_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "8.212.9",
//                                                    "vercode": 753,
//                                                    "md5sum": "8b7833535bdec60266bbb5ec7ffb06d7"
//                                        },
//                                            "stats": {
//                                            "downloads": 597049,
//                                                    "rating": {
//                                                "avg": 4.63,
//                                                        "total": 269,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 221
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 22
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 10
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 6
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 10
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 12880477,
//                                                "name": "Minecraft - Pocket Edition 0.1.1j alpha",
//                                                "package": "com.mojangcanada.minecraftpe",
//                                                "size": 19392023,
//                                                "icon": "http://pool.img.aptoide.com/mojangpe/e5a58b6d90190d99abd3af119b3ec1eb_icon.png",
//                                                "graphic": null,
//                                                "added": "2015-11-10 11:19:42",
//                                                "modified": "2015-11-10 11:19:42",
//                                                "updated": "2016-03-26 22:47:24",
//                                                "uptype": "regular",
//                                                "store": {
//                                            "id": 993535,
//                                                    "name": "mojangpe",
//                                                    "avatar": "http://pool.img.aptoide.com/mojangpe/3bf5adf05843f9f28c486d5ddef8f873_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "0.14.1",
//                                                    "vercode": 1013,
//                                                    "md5sum": "59bd23f0cbc7af49fab26b9e0221c0d0"
//                                        },
//                                            "stats": {
//                                            "downloads": 234956,
//                                                    "rating": {
//                                                "avg": 3.94,
//                                                        "total": 1204,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 773
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 88
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 60
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 65
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 218
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18013589,
//                                                "name": "Clash Royale",
//                                                "package": "com.supercell.clashroyale",
//                                                "size": 91282556,
//                                                "icon": "http://pool.img.aptoide.com/pocketappz/fc255df60a352a0e1c5fa506db267424_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/pocketappz/045fc5e608ec9900d074ff2ee33fa3d0_fgraphic_705x345.jpg",
//                                                "added": "2016-03-02 13:44:26",
//                                                "modified": "2016-03-02 13:44:26",
//                                                "updated": "2016-03-28 06:33:45",
//                                                "uptype": "aptbackup",
//                                                "store": {
//                                            "id": 925184,
//                                                    "name": "pocketappz",
//                                                    "avatar": "http://pool.img.aptoide.com/pocketappz/cbb3743b160875217e6ddfa6a927a464_ravatar.png"
//                                        },
//                                            "file": {
//                                            "vername": "1.2.3",
//                                                    "vercode": 28,
//                                                    "md5sum": "9254f3fda85b582828c705156aaf704c"
//                                        },
//                                            "stats": {
//                                            "downloads": 140248,
//                                                    "rating": {
//                                                "avg": 4.64,
//                                                        "total": 567,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 478
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 39
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 14
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 8
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 28
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 8047524,
//                                                "name": "GTA: SA",
//                                                "package": "com.rockstargames.gtasa",
//                                                "size": 2617814002,
//                                                "icon": "http://pool.img.aptoide.com/androidiyop/9f71e5aa68c2297a39c6966fa6dc80d7_icon.png",
//                                                "graphic": null,
//                                                "added": "2014-12-25 12:01:42",
//                                                "modified": "2014-12-25 12:01:42",
//                                                "updated": "2016-03-27 21:14:41",
//                                                "uptype": "aptuploader",
//                                                "store": {
//                                            "id": 762581,
//                                                    "name": "androidiyop",
//                                                    "avatar": "http://pool.img.aptoide.com/androidiyop/2ae764d3b4a8021d0e1d42e18c908623_ravatar.png"
//                                        },
//                                            "file": {
//                                            "vername": "1.07",
//                                                    "vercode": 11,
//                                                    "md5sum": "7a202c9ac2c604fbb1d69175ad710315"
//                                        },
//                                            "stats": {
//                                            "downloads": 129155,
//                                                    "rating": {
//                                                "avg": 4.35,
//                                                        "total": 3045,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 2334
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 174
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 116
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 116
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 305
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 13222134,
//                                                "name": "Minecraft: Windows 10 Edition",
//                                                "package": "com.mojang.minecraft",
//                                                "size": 19151132,
//                                                "icon": "http://pool.img.aptoide.com/mycraftfreeapps/284f8657af98d305c1eb44beee572299_icon.png",
//                                                "graphic": null,
//                                                "added": "2015-11-21 16:39:40",
//                                                "modified": "2015-11-21 16:39:40",
//                                                "updated": "2016-03-28 05:21:55",
//                                                "uptype": "aptuploader",
//                                                "store": {
//                                            "id": 1001846,
//                                                    "name": "mycraftfreeapps",
//                                                    "avatar": "http://pool.img.aptoide.com/mycraftfreeapps/3bf5adf05843f9f28c486d5ddef8f873_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.0.0",
//                                                    "vercode": 665565698,
//                                                    "md5sum": "e1089c1fc683510878a3974c99a30f2f"
//                                        },
//                                            "stats": {
//                                            "downloads": 96242,
//                                                    "rating": {
//                                                "avg": 3.91,
//                                                        "total": 3325,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 1974
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 353
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 252
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 232
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 514
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18163887,
//                                                "name": "Piano Tiles 2",
//                                                "package": "com.cmplay.tiles2",
//                                                "size": 24917113,
//                                                "icon": "http://pool.img.aptoide.com/darkmarth/ef5f41fd2f72677115f8fd7c3d3b73a8_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/darkmarth/b2e00aa0b583f24321a938bc9bc0c16e_fgraphic_705x345.png",
//                                                "added": "2016-03-13 20:32:08",
//                                                "modified": "2016-03-13 20:32:08",
//                                                "updated": "2016-03-28 02:41:40",
//                                                "uptype": "aptbackup",
//                                                "store": {
//                                            "id": 1142917,
//                                                    "name": "darkmarth",
//                                                    "avatar": "http://pool.img.aptoide.com/darkmarth/3bf5adf05843f9f28c486d5ddef8f873_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.2.0.880",
//                                                    "vercode": 50200880,
//                                                    "md5sum": "b39c2cd202768f47e979afe8a0075024"
//                                        },
//                                            "stats": {
//                                            "downloads": 77988,
//                                                    "rating": {
//                                                "avg": 4.94,
//                                                        "total": 16,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 15
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 1
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 0
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 0
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 0
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        }
//                                        ]
//                                    }
//                                },
//                                    "actions": [
//                                    {
//                                        "type": "button",
//                                            "label": "More",
//                                            "tag": "apps-group-top-games-more",
//                                            "event": {
//                                        "type": "API",
//                                                "name": "getStoreWidgets",
//                                                "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/home/widget/apps_list%3Acat_2%3Adownloads7d%3Aglobal"
//                                    }
//                                    }
//                                    ],
//                                    "data": {
//                                    "layout": "GRID"
//                                }
//                                },
//                                {
//                                    "type": "DISPLAYS",
//                                        "title": "Displays",
//                                        "tag": "displays-displays",
//                                        "view": {
//                                    "info": {
//                                        "status": "OK",
//                                                "time": {
//                                            "seconds": 0.024734020233154,
//                                                    "human": "24 milliseconds"
//                                        }
//                                    },
//                                    "list": [
//                                    {
//                                        "label": "Apps for Kids",
//                                            "tag": "apps-for-kids",
//                                            "graphic": "http://1.bp.blogspot.com/-I_OKPHEJQSk/VW4r9YKdt1I/AAAAAAAAQ0g/BtLK-aD2TgY/s1600/benfica_34_championship_by_courato-d8u7gnw.jpg",
//                                            "event": {
//                                        "type": "BROWSER",
//                                                "name": "CLICK",
//                                                "action": "https://www.youtube.com/channel/UCgyqtNWZmIxTx3b6OxTSALw"
//                                    }
//                                    },
//                                    {
//                                        "label": "Music & Video",
//                                            "tag": "music-video",
//                                            "graphic": "http://pool.img.aptoide.com/apps/7f151e0ed1d51c5285bb9147a53d95d2_cat_graphic.png",
//                                            "event": {
//                                        "type": "API",
//                                                "name": "getStoreWidgets",
//                                                "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/home/widget/apps_list%3Aucat_4458"
//                                    }
//                                    }
//                                    ]
//                                },
//                                    "actions": [],
//                                    "data": null
//                                },
//                                {
//                                    "type": "STORES_GROUP",
//                                        "title": "Top Stores",
//                                        "tag": "stores-group-top-stores",
//                                        "view": {
//                                    "info": {
//                                        "status": "OK",
//                                                "time": {
//                                            "seconds": 0.000946044921875,
//                                                    "human": "946 microseconds"
//                                        }
//                                    },
//                                    "datalist": {
//                                        "total": 878205,
//                                                "count": 4,
//                                                "next": 4,
//                                                "offset": 0,
//                                                "limit": 4,
//                                                "hidden": 0,
//                                                "list": [
//                                        {
//                                            "id": 15,
//                                                "name": "apps",
//                                                "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg",
//                                                "added": "2010-11-04 12:21:52",
//                                                "modified": "2010-11-04 12:21:52",
//                                                "appearance": {
//                                            "theme": "default",
//                                                    "description": "Aptoide Official App Store"
//                                        },
//                                            "stats": {
//                                            "apps": 136622,
//                                                    "subscribers": 121374,
//                                                    "downloads": 1053086
//                                        }
//                                        },
//                                        {
//                                            "id": 354763,
//                                                "name": "milaupv",
//                                                "avatar": "http://pool.img.aptoide.com/milaupv/ffa8bf1178dd61576f099644622b8bed_ravatar.jpg",
//                                                "added": "2013-01-20 14:23:46",
//                                                "modified": "2013-01-20 14:23:46",
//                                                "appearance": {
//                                            "theme": "gold",
//                                                    "description": ""
//                                        },
//                                            "stats": {
//                                            "apps": 1865,
//                                                    "subscribers": 219384,
//                                                    "downloads": 376986
//                                        }
//                                        },
//                                        {
//                                            "id": 65518,
//                                                "name": "mark8",
//                                                "avatar": "http://pool.img.aptoide.com/mark8/3bf5adf05843f9f28c486d5ddef8f873_ravatar.jpg",
//                                                "added": "2012-06-25 05:17:51",
//                                                "modified": "2016-02-15 14:05:48",
//                                                "appearance": {
//                                            "theme": "lightsky",
//                                                    "description": ""
//                                        },
//                                            "stats": {
//                                            "apps": 2837,
//                                                    "subscribers": 87175,
//                                                    "downloads": 69121
//                                        }
//                                        },
//                                        {
//                                            "id": 915628,
//                                                "name": "vip-apk",
//                                                "avatar": "http://pool.img.aptoide.com/vip-apk/7febcadb8252366ea782a9e57197173f_ravatar.png",
//                                                "added": "2015-05-07 23:19:32",
//                                                "modified": "2016-02-15 19:36:44",
//                                                "appearance": {
//                                            "theme": "black",
//                                                    "description": "● STOP 'R FUCKING SPAM REQUEST OR I CLOSE THE STORE ●\r\n\r\n"
//                                        },
//                                            "stats": {
//                                            "apps": 5522,
//                                                    "subscribers": 68724,
//                                                    "downloads": 56133
//                                        }
//                                        }
//                                        ]
//                                    }
//                                },
//                                    "actions": [
//                                    {
//                                        "type": "button",
//                                            "label": "More",
//                                            "tag": "stores-group-top-stores-more",
//                                            "event": {
//                                        "type": "API",
//                                                "name": "listStores",
//                                                "action": "http://ws2.aptoide.com/api/7/listStores/sort/downloads7d"
//                                    }
//                                    }
//                                    ],
//                                    "data": {
//                                    "layout": "GRID"
//                                }
//                                },
//                                {
//                                    "type": "REVIEWS",
//                                        "title": "Reviews",
//                                        "tag": "reviews-reviews",
//                                        "view": null,
//                                        "actions": [],
//                                    "data": null
//                                },
//                                {
//                                    "type": "STORES_GROUP",
//                                        "title": "Featured Stores",
//                                        "tag": "stores-group-featured-stores",
//                                        "view": {
//                                    "info": {
//                                        "status": "OK",
//                                                "time": {
//                                            "seconds": 0.00079679489135742,
//                                                    "human": "796 microseconds"
//                                        }
//                                    },
//                                    "datalist": {
//                                        "total": 2,
//                                                "count": 2,
//                                                "next": 2,
//                                                "offset": 0,
//                                                "limit": 25,
//                                                "hidden": 0,
//                                                "list": [
//                                        {
//                                            "id": 958583,
//                                                "name": "zepto",
//                                                "avatar": "http://pool.img.aptoide.com/zepto/498b4354897c59a61b7d4428cb26399b_ravatar.png",
//                                                "added": "2015-08-31 04:37:32",
//                                                "modified": "2015-08-31 05:37:32",
//                                                "appearance": {
//                                            "theme": "springgreen",
//                                                    "description": ""
//                                        },
//                                            "stats": {
//                                            "apps": 13,
//                                                    "subscribers": 3075,
//                                                    "downloads": 224685
//                                        }
//                                        },
//                                        {
//                                            "id": 940192,
//                                                "name": "worldsports",
//                                                "avatar": "http://pool.img.aptoide.com/worldsports/c84456051826b53a5efe788164e6d329_ravatar.png",
//                                                "added": "2015-07-20 17:09:09",
//                                                "modified": "2015-07-20 23:04:33",
//                                                "appearance": {
//                                            "theme": "black",
//                                                    "description": "This store have sport`s applications "
//                                        },
//                                            "stats": {
//                                            "apps": 10,
//                                                    "subscribers": 2428,
//                                                    "downloads": 4511
//                                        }
//                                        }
//                                        ]
//                                    }
//                                },
//                                    "actions": [],
//                                    "data": {
//                                    "layout": "GRID"
//                                }
//                                },
//                                {
//                                    "type": "DISPLAYS",
//                                        "title": "Displays",
//                                        "tag": "displays-displays",
//                                        "view": {
//                                    "info": {
//                                        "status": "OK",
//                                                "time": {
//                                            "seconds": 0.0051538944244385,
//                                                    "human": "5 milliseconds"
//                                        }
//                                    },
//                                    "list": []
//                                },
//                                    "actions": [],
//                                    "data": null
//                                },
//                                {
//                                    "type": "APPS_GROUP",
//                                        "title": "Aptoide Publishers",
//                                        "tag": "apps-group-aptoide-publishers",
//                                        "view": {
//                                    "info": {
//                                        "status": "OK",
//                                                "time": {
//                                            "seconds": 0.035176992416382,
//                                                    "human": "35 milliseconds"
//                                        }
//                                    },
//                                    "datalist": {
//                                        "total": 21785,
//                                                "count": 3,
//                                                "next": 3,
//                                                "offset": 0,
//                                                "limit": 3,
//                                                "hidden": 0,
//                                                "list": [
//                                        {
//                                            "id": 18330726,
//                                                "name": "Owl night",
//                                                "package": "com.magnetogame.owlynight",
//                                                "size": 35548175,
//                                                "icon": "http://pool.img.aptoide.com/apps/c3fa78cd6cc50693d094ef6e00fad79c_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/apps/fc40b009c2c4f5ca4f8cd6214f7e8092_fgraphic_705x345.png",
//                                                "added": "2016-03-24 19:51:35",
//                                                "modified": "2016-03-24 19:51:35",
//                                                "updated": "2016-03-24 19:51:35",
//                                                "uptype": "regular",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.13",
//                                                    "vercode": 13,
//                                                    "md5sum": "a75387dbebb6c9313690f20882d14d17"
//                                        },
//                                            "stats": {
//                                            "downloads": 1729,
//                                                    "rating": {
//                                                "avg": 3.5,
//                                                        "total": 4,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 2
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 0
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 1
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 0
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 1
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 10451986,
//                                                "name": "杓e?",
//                                                "package": "com.cootek.smartinputv5",
//                                                "size": 26191785,
//                                                "icon": "http://pool.img.aptoide.com/apps/a99f3a531d0a6e078a8e94d41869f7df_icon.png",
//                                                "graphic": "http://pool.img.aptoide.com/apps/c1b429c48b2a1990e12ff73ca7040412_fgraphic_705x345.jpg",
//                                                "added": "2015-08-21 15:19:44",
//                                                "modified": "2015-08-21 15:19:44",
//                                                "updated": "2016-03-25 20:11:08",
//                                                "uptype": "regular",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "5.7.4.0",
//                                                    "vercode": 5659,
//                                                    "md5sum": "9d414ebb93aa7ac61ddd3f61bd27c7cd"
//                                        },
//                                            "stats": {
//                                            "downloads": 402152,
//                                                    "rating": {
//                                                "avg": 4.27,
//                                                        "total": 11,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 6
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 2
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 3
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 0
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 0
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        },
//                                        {
//                                            "id": 18282439,
//                                                "name": "Modern Copter Warship Battle",
//                                                "package": "com.aag.modern.copter.warship.battle",
//                                                "size": 3895738,
//                                                "icon": "http://pool.img.aptoide.com/apps/c67076769ebac53638b6e2cd5c255bb5_icon.png",
//                                                "graphic": null,
//                                                "added": "2016-03-21 15:55:52",
//                                                "modified": "2016-03-21 15:55:52",
//                                                "updated": "2016-03-21 15:55:52",
//                                                "uptype": "regular",
//                                                "store": {
//                                            "id": 15,
//                                                    "name": "apps",
//                                                    "avatar": "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg"
//                                        },
//                                            "file": {
//                                            "vername": "1.1.1",
//                                                    "vercode": 11,
//                                                    "md5sum": "420fe2fb0dd69a360b4d17767e8bf64b"
//                                        },
//                                            "stats": {
//                                            "downloads": 9486,
//                                                    "rating": {
//                                                "avg": 3.67,
//                                                        "total": 12,
//                                                        "votes": [
//                                                {
//                                                    "value": 5,
//                                                        "count": 7
//                                                },
//                                                {
//                                                    "value": 4,
//                                                        "count": 0
//                                                },
//                                                {
//                                                    "value": 3,
//                                                        "count": 1
//                                                },
//                                                {
//                                                    "value": 2,
//                                                        "count": 2
//                                                },
//                                                {
//                                                    "value": 1,
//                                                        "count": 2
//                                                }
//                                                ]
//                                            }
//                                        }
//                                        }
//                                        ]
//                                    }
//                                },
//                                    "actions": [
//                                    {
//                                        "type": "button",
//                                            "label": "More",
//                                            "tag": "apps-group-aptoide-publishers-more",
//                                            "event": {
//                                        "type": "API",
//                                                "name": "getStoreWidgets",
//                                                "action": "http://ws2.aptoide.com/api/7/getStoreWidgets/store_id/15/context/home/widget/apps_list%3Aucat_3239"
//                                    }
//                                    }
//                                    ],
//                                    "data": {
//                                    "layout": "GRID"
//                                }
//                                }
//                                ]
//                            }
//                        }
//                    }
//                    }

                    public static final String GET_STORE_TAB = "getStore";
                    public static final String GET_STORE_WIDGETS_TAB = "getStoreWidgets";
                    public static final String GET_APK_COMMENTS_TAB = "getApkComments";
                    public static final String GET_REVIEWS_TAB = "getReviews";

                    public static final String API_V7_TYPE = "API";
                    public static final String API_V3_TYPE = "v3";
                    public static final String API_BROWSER_TYPE = "BROWSER";

                    public static final String EVENT_LIST_APPS = "listApps";
                    public static final String EVENT_LIST_STORES = "listStores";
                    public static final String EVENT_GETSTOREWIDGETS = "getStoreWidgets";
                    public static final String EVENT_CLICK_TYPE = "CLICK";
                    public static final String EVENT_GETAPKCOMMENTS = "getApkComments";

                    public String type; // API, v3
                    public String name; // listApps, getStore, getStoreWidgets, getApkComments
                    public String action;
                }
            }
        }
    }
}
