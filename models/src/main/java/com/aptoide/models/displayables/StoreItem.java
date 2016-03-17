package com.aptoide.models.displayables;

import android.support.annotation.ColorRes;

import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.stores.Login;

public class StoreItem extends Displayable {

	public final Login login;
	public String storeName;
	public String storeDwnNumber;
	public String storeAvatar;
	public boolean list;
	public long id;
	@ColorRes private int storeHeaderColor;
	private int themeId;

	public StoreItem(String name, String number, String avatar, @ColorRes int storeHeaderColor, int themeId, boolean list, long id, Login login, int bucketSize) {
		super(bucketSize);
		this.login = login;
		this.storeName = name;
		this.storeDwnNumber = number;
		this.storeAvatar = avatar;
		this.list = list;
		this.id = id;
		FULL_ROW = bucketSize;
		this.storeHeaderColor = storeHeaderColor;
		this.themeId = themeId;
		setSpanSize(1);
	}

	@Override
	public int getSpanSize() {
		return 1;
	}

	public
	@ColorRes
	int getStoreHeaderColor() {
		return storeHeaderColor;
	}

	public int getThemeId() {
		return themeId;
	}
}