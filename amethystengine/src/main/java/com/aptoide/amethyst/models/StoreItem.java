package com.aptoide.amethyst.models;

import com.aptoide.models.Displayable;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.stores.Login;

public class StoreItem extends Displayable {

    public final Login login;
    public String storeName;
	public String storeDwnNumber;
	public String storeAvatar;
	public EnumStoreTheme theme;
    public boolean list;
    public long id;

    public StoreItem(String name, String number, String avatar, EnumStoreTheme theme, boolean list, long id, Login login, int bucketSize) {
        super(bucketSize);
        this.login = login;
        this.storeName = name;
        this.storeDwnNumber = number;
        this.storeAvatar = avatar;
        this.theme = theme;
        this.list = list;
        this.id = id;
        FULL_ROW = bucketSize;
        setSpanSize(1);
	}

    @Override
    public int getSpanSize() {
        return 1;
    }
}